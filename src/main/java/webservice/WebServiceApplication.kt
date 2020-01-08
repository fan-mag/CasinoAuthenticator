package webservice

import CasinoLib.exceptions.UserNotFoundException
import CasinoLib.helpers.Exceptions
import CasinoLib.model.Apikey
import CasinoLib.model.Message
import CasinoLib.model.User
import CasinoLib.services.CasinoLibrary
import helpers.Auth
import helpers.Database
import helpers.RequestProcess
import helpers.UserProcess
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Callable

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

    @Async
    @PutMapping("/auth")
    open fun getUserKey(@RequestBody requestBody: String,
                        @RequestHeader(name = "Content-Type", required = true) contentType: String): Callable<ResponseEntity<*>> {
        try {
            if (!RequestProcess.validateContentType(contentType)) return Callable { ResponseEntity(Message("Wrong Content-Type header"), HttpStatus.BAD_REQUEST) }
            val user = RequestProcess.bodyToUser(requestBody)
            if (!UserProcess.validatePutBody(user)) return Callable { ResponseEntity(Message("Bad Request"), HttpStatus.BAD_REQUEST) }
            if (!UserProcess.isUserExists(user)) return Callable { ResponseEntity(Message("User with login ${user.login} not found"), HttpStatus.NOT_FOUND) }
            if (!UserProcess.isPasswordCorrect(user)) return Callable { ResponseEntity(Message("Incorrect password for user ${user.login}"), HttpStatus.UNAUTHORIZED) }
            return Callable { ResponseEntity(Apikey(UserProcess.getApikey(user)), HttpStatus.OK) }
        } catch (exception: Exception) {
            Exceptions.handle(exception, "Auth")
        }
        return Callable { ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR) }
    }

    @Async
    @PatchMapping("/auth")
    open fun getLoginByApikey(@RequestHeader(name = "apikey", required = true) apikey: String): Callable<ResponseEntity<*>> {
        try {
            val login = Auth.getLoginByApikey(apikey)
            return Callable { ResponseEntity(User(login = login), HttpStatus.OK) }
        } catch (exception: Exception) {
            when (exception) {
                is UserNotFoundException -> return Callable { ResponseEntity(Message("Wrong apikey provided"), HttpStatus.NOT_FOUND) }
                else -> Exceptions.handle(exception, "Auth")
            }
        }
        return Callable { ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR) }
    }

    @Async
    @PostMapping("/auth")
    open fun createUser(@RequestBody requestBody: String,
                   @RequestHeader(name = "Content-Type", required = true) contentType: String): Callable<ResponseEntity<*>> {
        try {
            if (!RequestProcess.validateContentType(contentType)) return Callable { ResponseEntity(Message("Wrong Content-Type header"), HttpStatus.BAD_REQUEST) }
            val user = RequestProcess.bodyToUser(requestBody)
            if (!UserProcess.validatePutBody(user)) return Callable { ResponseEntity(Message("Bad Request"), HttpStatus.BAD_REQUEST) }
            if (UserProcess.isUserExists(user)) return Callable { ResponseEntity(Message("User with login ${user.login} already exists"), HttpStatus.UNPROCESSABLE_ENTITY) }
            return Callable { ResponseEntity(Apikey(UserProcess.createUser(user)), HttpStatus.CREATED) }
        } catch (exception: Exception) {
            Exceptions.handle(exception, "Auth")
        }
        return Callable { ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR) }
    }
    @Async
    @GetMapping("/auth")
    open fun getUserPrivilege(
            @RequestHeader(name = "apikey", required = true) apikey: String): Callable<ResponseEntity<*>> {
        try {
            val privilege = UserProcess.getPrivilege(apikey)
            if (privilege.level == 0) return Callable { ResponseEntity(privilege, HttpStatus.NOT_FOUND) }
            return Callable { ResponseEntity(privilege, HttpStatus.OK) }
        } catch (exception: Exception) {
            Exceptions.handle(exception, "Auth")
        }
        return Callable { ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR) }
    }

    @Async
    @DeleteMapping("/auth")
    open fun deleteUser(@RequestBody(required = false) requestBody: String?,
                   @RequestHeader(name = "Content-Type", required = false) contentType: String?,
                   @RequestHeader(name = "apikey", required = true) apikey: String): Callable<ResponseEntity<*>> {
        try {
            if (!RequestProcess.validateContentType(contentType)) return Callable { ResponseEntity(Message("Wrong Content-Type header"), HttpStatus.BAD_REQUEST) }
            val privilege = UserProcess.getPrivilege(apikey)
            when (privilege.level) {
                0 -> return Callable { ResponseEntity(privilege, HttpStatus.NOT_FOUND) }
                7 -> {
                    UserProcess.deleteUserByApikey(apikey)
                    return Callable { ResponseEntity(Message("Your account has been deleted"), HttpStatus.ACCEPTED) }
                }
                15 -> {
                    if (!RequestProcess.validateContentType(contentType)) return Callable { ResponseEntity(Message("Wrong Content-Type header"), HttpStatus.BAD_REQUEST) }
                    if (RequestProcess.isBodyNull(requestBody)) return Callable { ResponseEntity(Message("Missing request body for delete operation"), HttpStatus.BAD_REQUEST) }
                    val user = RequestProcess.bodyToUser(requestBody)
                    if (user.login != null) {
                        UserProcess.deleteUserByLogin(user.login!!)
                        return Callable { ResponseEntity(Message("Account with login ${user.login} has been deleted"), HttpStatus.ACCEPTED) }
                    }
                    if (user.apikey != null) {
                        UserProcess.deleteUserByApikey(user.apikey!!)
                        return Callable { ResponseEntity(Message("Account with apikey ${user.apikey} has been deleted"), HttpStatus.ACCEPTED) }
                    }
                    if (user.login == null && user.apikey == null)
                        return Callable { ResponseEntity(Message("Login or apikey of deleting user must be specified"), HttpStatus.UNPROCESSABLE_ENTITY) }
                }
            }
        } catch (exception: Exception) {
            Exceptions.handle(exception, "Auth")
        }
        return Callable { ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR) }
    }
}