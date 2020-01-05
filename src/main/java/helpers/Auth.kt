package helpers

object Auth {
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

    fun checkLoginAndPassword(login: String?, password: String?): Boolean {
        val query = "select password from users where login = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val databasePassword = resultSet.getString("password")
        return (databasePassword == password)
    }

    fun getApikey(login: String?): String {
        val query = "select apikey from users where login = ?"
        val preparedStatement = Database.conn.prepareStatement(query)
        preparedStatement.setString(1, login)
        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val apikey = resultSet.getString("apikey")
        return apikey
    }


}