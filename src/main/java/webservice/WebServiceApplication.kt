package webservice

import CasinoLib.model.User
import CasinoLib.services.CasinoLibrary
import CasinoLib.services.Logger
import com.google.gson.Gson
import helpers.Database
import helpers.UserProcess
import model.Apikey
import model.Message
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun getUserKey(@RequestBody requestBody: String,
                   @RequestHeader(name = "Content-Type", required = true) contentType: String): ResponseEntity<Any> {
        try {
            if (!contentType.contains("application/json")) return ResponseEntity(Message("Wrong Content-Type header"), HttpStatus.BAD_REQUEST)
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
        return ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @PostMapping("/auth")
    fun createUser(@RequestBody requestBody: String,
                   @RequestHeader(name = "Content-Type", required = true) contentType: String): ResponseEntity<Any> {
        try {
            if (!contentType.contains("application/json")) return ResponseEntity(Message("Wrong Content-Type header"), HttpStatus.BAD_REQUEST)
            val user = Gson().fromJson(requestBody, User::class.java)
            if (!UserProcess.validatePutBody(user)) return ResponseEntity(Message("Bad Request"), HttpStatus.BAD_REQUEST)
            if (UserProcess.isUserExists(user)) return ResponseEntity(Message("User with login ${user.login} already exists"), HttpStatus.UNPROCESSABLE_ENTITY)
            return ResponseEntity(Apikey(UserProcess.createUser(user)), HttpStatus.CREATED)
        } catch (exception: Exception) {
            if (exception.message != null)
                Logger.log(service = "Auth", message = exception.message!!)
            else
                Logger.log(service = "Auth", message = "Exception without any message")
        }
        return ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/auth")
    fun getUserPrivilege(
            @RequestHeader(name = "apikey", required = true) apikey: String): ResponseEntity<Any> {
        try {
            val privilege = UserProcess.getPrivilege(apikey)
            if (privilege.level == 0) return ResponseEntity(privilege, HttpStatus.NOT_FOUND)
            return ResponseEntity(privilege, HttpStatus.OK)
        } catch (exception: Exception) {
            if (exception.message != null)
                Logger.log(service = "Auth", message = exception.message!!)
            else
                Logger.log(service = "Auth", message = "Exception without any message")
        }
        return ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}