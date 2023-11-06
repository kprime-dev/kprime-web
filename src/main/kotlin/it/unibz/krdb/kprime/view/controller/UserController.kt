package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserApi
import it.unibz.krdb.kprime.domain.user.UserService
import it.unibz.krdb.kprime.view.RestError
import javax.servlet.http.HttpServletResponse

class UserController(val userService: UserService) {

    var getUsers = Handler { ctx : Context -> ctx.json(userService.allUsers()) }

    var getUser = Handler { ctx : Context ->
        val userApi = userApi(ctx)
        if (userApi == null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else ctx.json(userApi)
    }

    companion object {
        val userName =  { ctx : Context -> ctx.sessionAttribute<String>("currentUser") }

    }

    private fun userApi(ctx: Context): UserApi? {
        return userService.userApiFromName(userName(ctx) ?: User.NO_USER)
    }

    var putUser = Handler { ctx : Context ->
        val user = ctx.bodyAsClass<User>()
        val userToStore = User(
                user.id,
                user.name,
                user.role,
                user.memberOf,
                user.pass,//BCrypt.hashpw(user.pass, "$2a$10\$h.dl5J86rGH7I8bD9bZeZe"),
                user.email)
        userService.putUser(userToStore)
    }

    var putUsers = Handler { ctx: Context ->
        val users = ctx.bodyAsClass<List<User>>()// fu Array<User>>
        if (users.size > 6) {
            ctx.json(RestError("Raggiungo numero massimo utenti.", HttpServletResponse.SC_NOT_ACCEPTABLE))
            ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
        } else {
            userService.putUsers(users)
            ctx.status(HttpServletResponse.SC_ACCEPTED)
        }
    }

}