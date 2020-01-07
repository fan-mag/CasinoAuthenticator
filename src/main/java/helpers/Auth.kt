package helpers

import CasinoLib.exceptions.UserNotFoundException
import CasinoLib.model.Privilege

object Auth {

    @Throws(Exception::class)
    fun checkLogin(login: String?): Int {
        val query = "select count(*) from users where login = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val count = resultSet.getInt("count")
        resultSet.close()
        return count
    }

    @Throws(Exception::class)
    fun checkLoginAndPassword(login: String?, password: String?): Boolean {
        val query = "select password from users where login = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val databasePassword = resultSet.getString("password")
        return (databasePassword == password)
    }

    @Throws(Exception::class)
    fun getApikey(login: String?): String {
        val cached = Cache.get(CacheType.LOGIN_APIKEY, login!!)
        if (cached != null) return cached.toString()
        val query = "select apikey from users where login = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val apikey = resultSet.getString("apikey")
        Cache.put(CacheType.LOGIN_APIKEY, login, apikey)
        return apikey
    }

    @Throws(Exception::class)
    fun createUser(login: String?, password: String?): String {
        val query = "insert into users (login, password) values (?, ?) returning apikey"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        preparedStatement.setString(2, password)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        return resultSet.getString("apikey")
    }

    @Throws(Exception::class)
    fun getPrivilege(apikey: String): Privilege {
        val cached = Cache.get(CacheType.APIKEY_PRIVILEGE, apikey)
        if (cached != null) return cached as Privilege
        val query = "select privilege, description from users u join privileges p on u.privilege = p.id where apikey = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, apikey)
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            val privilege = Privilege(resultSet.getInt("privilege"), resultSet.getString("description"))
            Cache.put(CacheType.APIKEY_PRIVILEGE, apikey, privilege)
            return privilege
        } else {
            val privilege = Privilege(0, "Invalid apikey")
            Cache.put(CacheType.APIKEY_PRIVILEGE, apikey, privilege)
            return privilege
        }
    }

    @Throws(Exception::class)
    fun deleteUserByApikey(apikey: String) {
        val query = "delete from users where apikey = ? returning login"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, apikey)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val login = resultSet.getString("login")
        Cache.delete(CacheType.LOGIN_APIKEY, login)
        Cache.delete(CacheType.APIKEY_PRIVILEGE, apikey)
    }

    @Throws(Exception::class)
    fun deleteUserByLogin(login: String) {
        val query = "delete from users where login = ? returning apikey"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val apikey = resultSet.getString("apikey")
        Cache.delete(CacheType.LOGIN_APIKEY, login)
        Cache.delete(CacheType.APIKEY_PRIVILEGE, apikey)
    }

    @Throws(Exception::class)
    fun getLoginByApikey(apikey: String): String {
        val query = "select login from users where apikey = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, apikey)
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            val login = resultSet.getString("login")
            Cache.put(CacheType.LOGIN_APIKEY, login, apikey)
            return login
        }
        throw UserNotFoundException()
    }
}