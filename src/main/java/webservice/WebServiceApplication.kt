package webservice

import com.google.gson.Gson
import helpers.Database
import helpers.UserProcess
import model.Apikey
import model.Message
import model.User
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import services.CasinoLibrary
import services.Logger

@SpringBootApplication
@RestController
open class WebServiceApplication {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Database()
            CasinoLibrary.init("src/main/resources/casinolib.properties")
            SpringApplication.run(WebServiceApplication::class.java)
        }
    }

    @PutMapping("/auth")
    fun getUserKey(@RequestBody requestBody: String): ResponseEntity<Any> {
        try {
            val user = Gson().fromJson(requestBody, User::class.java)
            if (!UserProcess.validatePutBody(user)) return ResponseEntity(Message("Bad Request"), HttpStatus.BAD_REQUEST)
            if (!UserProcess.isUserExists(user)) return ResponseEntity(Message("User with login ${user.login} not found"), HttpStatus.NOT_FOUND)
            if (!UserProcess.isPasswordCorrect(user)) return ResponseEntity(Message("Incorrect password for user ${user.login}"), HttpStatus.UNAUTHORIZED)
            return ResponseEntity(Apikey(UserProcess.getApikey(user)), HttpStatus.OK)
        } catch (exception: Exception) {
            if (exception.message != null)
                Logger.log(service = "Auth", message = exception.message!!)
            else
                Logger.log(service = "Auth", message = "Exception without any message")
        }
        return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }

}