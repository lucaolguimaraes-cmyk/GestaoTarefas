package com.example.gestaotarefas

import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditarTarefasActivity : AppCompatActivity() {

    // Banco de dados
    lateinit var dbHelper: DatabaseHelper

    // Campo título
    lateinit var edtTitulo: EditText

    // Spinner prioridade
    lateinit var spnPrioridade: Spinner

    // Spinner status
    lateinit var spnStatus: Spinner

    // Botão salvar
    lateinit var btnSalvar: Button

    // ID da tarefa
    var tarefaId: Int = -1

    // Usuário
    var usuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_editar_tarefa)

        // Inicializa banco
        dbHelper = DatabaseHelper(this)

        // Liga XML
        edtTitulo =
            findViewById(R.id.edtTituloEditar)

        spnPrioridade =
            findViewById(R.id.spnPrioridadeEditar)

        spnStatus =
            findViewById(R.id.spnStatusEditar)

        btnSalvar =
            findViewById(R.id.btnSalvarEditar)

        // PRIORIDADES
        val prioridades = arrayOf(

            "Baixa Prioridade",

            "Média Prioridade",

            "Alta Prioridade"
        )

        // STATUS
        val statusList = arrayOf(

            "A fazer",

            "Em andamento",

            "Quase concluída"
        )

        // Adapter prioridade
        spnPrioridade.adapter =

            ArrayAdapter(

                this,

                android.R.layout.simple_spinner_dropdown_item,

                prioridades
            )

        // Adapter status
        spnStatus.adapter =

            ArrayAdapter(

                this,

                android.R.layout.simple_spinner_dropdown_item,

                statusList
            )

        // Recebe ID
        tarefaId =
            intent.getIntExtra("id", -1)

        // Recebe usuário
        usuario =
            intent.getStringExtra("usuario") ?: ""

        // Carrega tarefa
        carregarTarefa()

        // Clique salvar
        btnSalvar.setOnClickListener {

            atualizarTarefa()
        }
    }

    // Carrega dados da tarefa
    private fun carregarTarefa() {

        val db =
            dbHelper.readableDatabase

        val cursor = db.rawQuery(

            """
            SELECT titulo, status, prioridade
            FROM tarefas
            WHERE id=?
            """.trimIndent(),

            arrayOf(tarefaId.toString())
        )

        if (cursor.moveToFirst()) {

            // Título
            edtTitulo.setText(
                cursor.getString(0)
            )

            // Status
            val status =
                cursor.getString(1)

            // Prioridade
            val prioridade =
                cursor.getString(2)

            // Seleciona status
            spnStatus.setSelection(

                (
                        spnStatus.adapter
                                as ArrayAdapter<String>
                        )

                    .getPosition(status)
            )

            // Seleciona prioridade
            spnPrioridade.setSelection(

                (
                        spnPrioridade.adapter
                                as ArrayAdapter<String>
                        )

                    .getPosition(prioridade)
            )
        }

        cursor.close()
    }

    // Atualiza tarefa
    private fun atualizarTarefa() {

        // Título digitado
        val titulo =
            edtTitulo.text.toString()

        // Validação
        if (titulo.isEmpty()) {

            Toast.makeText(

                this,

                "Digite um título",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        // Valores atualizados
        val valores = ContentValues().apply {

            // Atualiza título
            put(
                "titulo",
                titulo
            )

            // Atualiza status
            put(
                "status",
                spnStatus.selectedItem.toString()
            )

            // Atualiza prioridade
            put(
                "prioridade",
                spnPrioridade.selectedItem.toString()
            )

            // Atualiza usuário
            put(
                "usuario",
                usuario
            )
        }

        // Atualiza banco
        dbHelper.writableDatabase.update(

            "tarefas",

            valores,

            "id=?",

            arrayOf(
                tarefaId.toString()
            )
        )

        // Mensagem sucesso
        Toast.makeText(

            this,

            "Tarefa atualizada!",

            Toast.LENGTH_SHORT

        ).show()

        // Fecha tela
        finish()
    }
}