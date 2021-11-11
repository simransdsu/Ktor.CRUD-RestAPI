package com.example.plugins

import com.example.routing.authenticationRoutes
import com.example.routing.notesRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import org.ktorm.database.Database

fun Application.configureRouting(db: Database) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    notesRoutes(db)
    authenticationRoutes(db)
}
