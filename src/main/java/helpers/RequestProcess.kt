package helpers

import CasinoLib.model.User
import CasinoLib.services.Logger
import com.google.gson.Gson

object RequestProcess {

    @Throws(Exception::class)
    fun validateContentType(contentType: String?): Boolean {
        Logger.log(service = "Auth", message = "Validating Content-Type header: $contentType")
        if (contentType == null) return false
        if (!contentType.contains("application/json")) return false
        return true
    }

    @Throws(Exception::class)
    fun isBodyNull(requestBody: String?) : Boolean {
        Logger.log(service = "Auth", message = "Checking if requestBody $requestBody is null")
        return (requestBody == null)
    }

    @Throws(Exception::class)
    fun bodyToUser(requestBody: String?) : User {
        Logger.log(service = "Auth", message = "Converting $requestBody to user")
        return Gson().fromJson(requestBody, User::class.java)
    }
}