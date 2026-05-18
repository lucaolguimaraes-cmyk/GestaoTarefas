package com.example.checkpoint

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :

    SQLiteOpenHelper(
        context,
        "tarefas.db",
        null,
        3
    ) {

    // Criação do banco
    override fun onCreate(db: SQLiteDatabase) {

        // Usuários
        val createUsuarios = """

            CREATE TABLE usuarios (

                id INTEGER PRIMARY KEY AUTOINCREMENT,

                nome TEXT,

                senha TEXT
            )

        """.trimIndent()

        // Tarefas
        val createTarefas = """

            CREATE TABLE tarefas (

                id INTEGER PRIMARY KEY AUTOINCREMENT,

                titulo TEXT,

                descricao TEXT,

                data TEXT,

                status TEXT,

                prioridade TEXT,

                usuario TEXT
            )

        """.trimIndent()

        db.execSQL(createUsuarios)

        db.execSQL(createTarefas)
    }

    // Atualização do banco
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL(
            "DROP TABLE IF EXISTS tarefas"
        )

        db.execSQL(
            "DROP TABLE IF EXISTS usuarios"
        )

        onCreate(db)
    }

    // Inserir usuário
    fun inserirUsuario(
        nome: String,
        senha: String
    ): Long {

        val db = writableDatabase

        val values = ContentValues().apply {

            put("nome", nome)

            put("senha", senha)
        }

        return db.insert(
            "usuarios",
            null,
            values
        )
    }

    // Validar login
    fun validarUsuario(
        nome: String,
        senha: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(

            """
            SELECT id
            FROM usuarios
            WHERE nome = ?
            AND senha = ?
            """.trimIndent(),

            arrayOf(nome, senha)
        )

        val existe =
            cursor.moveToFirst()

        cursor.close()

        return existe
    }
}