package com.example.gestaotarefas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "tarefas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val createUsuarios = """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                senha TEXT
            )
        """

        val createTarefas = """
            CREATE TABLE tarefas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT,
                status TEXT,
                prioridade TEXT,
                usuario TEXT
            )
        """

        db.execSQL(createUsuarios)
        db.execSQL(createTarefas)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tarefas")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun inserirUsuario(nome: String, senha: String): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("nome", nome)
            put("senha", senha)
        }

        return db.insert("usuarios", null, values)
    }

    fun validarUsuario(nome: String, senha: String): Boolean {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE nome = ? AND senha = ?",
            arrayOf(nome, senha)
        )

        val existe = cursor.moveToFirst()
        cursor.close()

        return existe
    }
}