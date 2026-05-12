package com.example.gestaotarefas

// Importa a classe ContentValues, usada para armazenar valores antes de inserir no banco
import android.content.ContentValues

// Importa o Context, necessário para acessar recursos do aplicativo
import android.content.Context

// Importa a classe SQLiteDatabase para manipulação do banco SQLite
import android.database.sqlite.SQLiteDatabase

// Importa a classe SQLiteOpenHelper, responsável por gerenciar o banco de dados
import android.database.sqlite.SQLiteOpenHelper

// Classe DatabaseHelper responsável por criar e manipular o banco de dados
class DatabaseHelper(context: Context) :

// SQLiteOpenHelper recebe:
// context -> contexto da aplicação
// "tarefas.db" -> nome do banco
// null -> cursor factory padrão
// 1 -> versão do banco de dados
    SQLiteOpenHelper(context, "tarefas.db", null, 1) {

    // Método executado automaticamente quando o banco é criado pela primeira vez
    override fun onCreate(db: SQLiteDatabase) {

        // Comando SQL para criar a tabela de usuários
        val createUsuarios = """
            CREATE TABLE usuarios (
                
                -- ID único do usuário
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                
                -- Nome do usuário
                nome TEXT,
                
                -- Senha do usuário
                senha TEXT
            )
        """

        // Comando SQL para criar a tabela de tarefas
        val createTarefas = """
            CREATE TABLE tarefas (
                
                -- ID único da tarefa
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                
                -- Título da tarefa
                titulo TEXT,
                
                -- Status da tarefa
                status TEXT,
                
                -- Prioridade da tarefa
                prioridade TEXT,
                
                -- Usuário responsável pela tarefa
                usuario TEXT
            )
        """

        // Executa o comando SQL de criação da tabela usuarios
        db.execSQL(createUsuarios)

        // Executa o comando SQL de criação da tabela tarefas
        db.execSQL(createTarefas)
    }

    // Método chamado quando a versão do banco é alterada
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        // Remove a tabela tarefas caso ela exista
        db.execSQL("DROP TABLE IF EXISTS tarefas")

        // Remove a tabela usuarios caso ela exista
        db.execSQL("DROP TABLE IF EXISTS usuarios")

        // Cria novamente as tabelas
        onCreate(db)
    }

    // Função responsável por inserir um novo usuário no banco
    fun inserirUsuario(nome: String, senha: String): Long {

        // Obtém o banco em modo de escrita
        val db = writableDatabase

        // Cria um objeto ContentValues para armazenar os dados
        val values = ContentValues().apply {

            // Insere o nome na coluna "nome"
            put("nome", nome)

            // Insere a senha na coluna "senha"
            put("senha", senha)
        }

        // Insere os dados na tabela usuarios
        // Retorna o ID da linha inserida
        return db.insert("usuarios", null, values)
    }

    // Função responsável por validar login do usuário
    fun validarUsuario(nome: String, senha: String): Boolean {

        // Obtém o banco em modo de leitura
        val db = readableDatabase

        // Executa uma consulta SQL procurando usuário e senha iguais
        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE nome = ? AND senha = ?",

            // Substitui os ? pelos valores digitados
            arrayOf(nome, senha)
        )

        // moveToFirst() retorna true se encontrar algum resultado
        val existe = cursor.moveToFirst()

        // Fecha o cursor para liberar memória
        cursor.close()

        // Retorna true se encontrou usuário
        // Retorna false caso contrário
        return existe
    }
}