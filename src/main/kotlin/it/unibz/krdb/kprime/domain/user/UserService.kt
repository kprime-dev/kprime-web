package it.unibz.krdb.kprime.domain.user

import it.unibz.krdb.kprime.domain.setting.SettingService

class UserService(private val settingService: SettingService, private val userRepo: UserRepositoryBuilder) {

    fun allUsers():List<User> {
        return userRepo.build(settingService.getInstanceDir()).findAll()
    }

    fun putUser(userToStore: User) {
        userRepo.build(settingService.getInstanceDir()).save(userToStore)
    }

    fun putUsers(users: List<User>) {
        userRepo.build(settingService.getInstanceDir()).saveAll(users)
    }

    fun getUsers(projectLocation: String): List<User> {
        return userRepo.build(projectLocation).findAll()
    }

    fun userApiFromName(userName: String): UserApi? {
        val user = userRepo.build(settingService.getInstanceDir()).findFirstBy { user -> user.name == userName }
        val userObj: UserApi? = when {
            user != null -> {
                UserApi(userName, user.role)
            }
            else -> {
                UserApi(userName, User.ROLE.ANONYMOUS.name)
            }
        }
        return userObj
    }

    // Authenticate the user by hashing the inputted password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    fun authenticate(userName: String?, password: String?): Boolean {
        if (userName == null || password == null) {
            return false
        }
        val user = userRepo.build(settingService.getInstanceDir())
            .findFirstBy { user -> user.name == userName } ?: return false
        //val hashedPassword: String = BCrypt.hashpw(password, user.salt)
        //return hashedPassword == user.pass
        return password == user.pass
    }

    // This method doesn't do anything, it's just included as an example
    /*
    fun setPassword(username: String?, oldPassword: String?, newPassword: String?) {
        if (authenticate(username, oldPassword)) {
            val newSalt: String = BCrypt.gensalt()
            val newHashedPassword: String = BCrypt.hashpw(newSalt, newPassword)
            // Update the user salt and password
        }
    }
     */


}