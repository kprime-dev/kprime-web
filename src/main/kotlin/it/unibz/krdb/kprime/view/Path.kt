package it.unibz.krdb.kprime.view

internal class Path {

    object Page {
        const val INDEX = "/index.html"
    }

    object Web {
        const val INDEX = "/index"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val BOOKS = "/books"
        const val ONE_BOOK = "/books/:isbn"
    }

    object Template {
        const val INDEX = "freemarker/index/index.ftl"
        //const val INDEX = "/velocity/index/index.vm"
        const val LOGIN = "freemarker/login/login.ftl"
        const val NOT_FOUND = "/velocity/notFound.vm"
    }
}