package helpers

import CasinoLib.model.Privilege
import CasinoLib.model.User
import CasinoLib.services.Logger

object UserProcess {

    @Throws(Exception::class)
    fun validatePutBody(user: User): Boolean {
        if (user.login == null) {
            Logger.log(service = "Auth", message = "Request has no login field")
            return false
        }
        if (user.password == null) {
            Logger.log(service = "Auth", message = "Request has no password field")
            return false
        }
        return true
    }

    @Throws(Exception::class)
    fun isUserExists(user: User): Boolean {
        Logger.log(service = "Auth", message = "Checking if user with login ${user.login} exists")
        val count = Auth.checkLogin(user.login)
        Logger.log(service = "Auth", message = "User with login ${user.login} count: $count")
        return (count == 1)
    }

    @Throws(Exception::class)
    fun isPasswordCorrect(user: User): Boolean {
        Logger.log(service = "Auth", message = "Checking if password ${user.password} is correct on login ${user.login}")
        val isPasswordCorrect = Auth.checkLoginAndPassword(user.login, user.password)
        Logger.log(service = "Auth", message = "Is password correct: $isPasswordCorrect")
        return isPasswordCorrect
    }

    @Throws(Exception::class)
    fun getApikey(user: User): String {
        Logger.log(service = "Auth", message = "Retrieving apikey for user ${user.login}")
        val apikey = Auth.getApikey(user.login)
        Logger.log(service = "Auth", message = "Retrieved apikey $apikey")
        return apikey
    }

    @Throws(Exception::class)
    fun createUser(user: User): String {
        Logger.log(service = "Auth", message = "Creating user with login ${user.login} and password ${user.password}")
        val apikey = Auth.createUser(user.login, user.password)
        Logger.log(service = "Auth", message = "Created user with login ${user.login} and apikey is $apikey")
        return apikey
    }

    @Throws(Exception::class)
    fun getPrivilege(apikey: String): Privilege {
        Logger.log(service = "Auth", message = "Getting privilege level for apikey $apikey")
        val privilege = Auth.getPrivilege(apikey)
        Logger.log(service = "Auth", message = "Privilege for apikey $apikey: ${privilege.level} - ${privilege.description}")
        return privilege
    }

    @Throws(Exception::class)
    fun deleteUserByApikey(apikey: String) {
        Logger.log(service = "Auth", message = "Deleting user with apikey $apikey")
        Auth.deleteUserByApikey(apikey)
        Logger.log(service = "Auth", message = "User with apikey $apikey has been deleted")
    }

    @Throws(Exception::class)
    fun deleteUserByLogin(login: String) {
        Logger.log(service = "Auth", message = "Deletin user with login $login")
        Auth.deleteUserByLogin(login)
        Logger.log(service = "Auth", message = "User with login $login has been deleted")
    }
}