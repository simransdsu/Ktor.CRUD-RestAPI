package com.example.controllers

import com.example.entities.NotesEntity
import com.example.models.Note
import com.example.models.NoteRequest
import com.example.models.NoteResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import org.ktorm.database.Database
import org.ktorm.dsl.*

public class NotesController(var db: Database) {
    /**
     * Return all the notes from the Database
     */
    suspend fun getAllNotes(call: ApplicationCall) {
        val notes = db.from(NotesEntity).select()
            .map {
                val id = it[NotesEntity.id]
                val note = it[NotesEntity.note]
                Note(id ?: -1, note ?: "")
            }
        call.respond(notes)
    }

    /**
     * Create the Note from the parameters sent in the body
     */
    suspend fun createNote(call: ApplicationCall) {
        val request = call.receive<NoteRequest>()

        val result = db.insert(NotesEntity) {
            set(it.note, request.note)
        }

        if (result == 1) {
            // Send successfully response to the client
            call.respond(
                HttpStatusCode.OK, NoteResponse(
                    success = true,
                    data = "Values has been successfully inserted"
                )
            )
        } else {
            // Send failure response to the client
            call.respond(
                HttpStatusCode.BadRequest, NoteResponse(
                    success = false,
                    data = "Failed to insert values."
                )
            )
        }
    }

    /**
     * Get a single note with the id passed in the url if available
     */
    suspend fun getNoteById(call: ApplicationCall) {
        val id = call.parameters["id"]?.toInt() ?: -1

        val note = db.from(NotesEntity)
            .select()
            .where { NotesEntity.id eq id }
            .map {
                val id = it[NotesEntity.id]!!
                val note = it[NotesEntity.note]!!
                Note(id = id, note = note)
            }.firstOrNull()

        if(note == null) {
            call.respond(
                HttpStatusCode.NotFound,
                NoteResponse(
                    success = false,
                    data = "Could not found note with  id = $id"
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(
                    success = true,
                    data = note
                )
            )
        }
    }


    /**
     * Update the note with the id passed in the url and new parameters in the body is available
     */
    suspend fun updateNote(call: ApplicationCall) {
        val id =call.parameters["id"]?.toInt() ?: -1
        val updatedNote =call.receive<NoteRequest>()

        val rowsEffected = db.update(NotesEntity){
            set(it.note, updatedNote.note)
            where{
                it.id eq id
            }
        }

        if(rowsEffected == 1) {
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(
                    success = true,
                    data = "Note has been updated"
                )
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(
                    success = false,
                    data = "Note failed to update"
                )
            )
        }
    }

    /**
     * Delete the note with the id passed in the url if available
     */
    suspend fun deleteNote(call: ApplicationCall) {
        val id =call.parameters["id"]?.toInt() ?: -1
        val rowsEffected = db.delete(NotesEntity){
            it.id eq id
        }

        if(rowsEffected == 1) {
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(
                    success = true,
                    data = "Note has been delete"
                )
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                NoteResponse(
                    success = false,
                    data = "Note failed to delete"
                )
            )
        }
    }
}