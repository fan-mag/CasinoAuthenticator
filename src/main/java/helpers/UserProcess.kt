package helpers

import model.User
import services.Logger

object UserProcess {

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

    fun isUserExists(user: User): Boolean {
        Logger.log(service = "Auth", message = "Checking if user with login ${user.login} exists")
        val count = Auth.checkLogin(user.login)
        Logger.log(service = "Auth", message = "User with login ${user.login} count: $count")
        return (count == 1)
    }

    fun isPasswordCorrect(user: User): Boolean {
        Logger.log(service = "Auth", message = "Checking if password ${user.password} is correct on login ${user.login}")
        val isPasswordCorrect = Auth.checkLoginAndPassword(user.login, user.password)
        Logger.log (service = "Auth", message = "Is password correct: $isPasswordCorrect")
        return isPasswordCorrect
    }

    fun getApikey(user: User): String {
        Logger.log(service = "Auth", message = "Retrieving apikey for user ${user.login}")
        val apikey = Auth.getApikey(user.login)
        Logger.log(service = "Auth", message = "Retrieved apikey $apikey")
        return apikey
    }
}