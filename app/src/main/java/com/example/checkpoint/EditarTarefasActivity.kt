package com.example.checkpoint

import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import java.util.Calendar

class EditarTarefasActivity : AppCompatActivity() {

    // Banco de dados
    lateinit var dbHelper: DatabaseHelper

    // Campos
    lateinit var edtTitulo: EditText

    lateinit var edtDescricao: EditText

    lateinit var edtData: EditText

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

        edtDescricao =
            findViewById(R.id.edtDescricaoEditar)

        edtData =
            findViewById(R.id.edtDataEditar)

        edtData.addTextChangedListener(
            DateMask(edtData)
        )

        spnPrioridade =
            findViewById(R.id.spnPrioridadeEditar)

        spnStatus =
            findViewById(R.id.spnStatusEditar)

        btnSalvar =
            findViewById(R.id.btnSalvarEditar)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

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

        edtData.setOnClickListener {

            val calendario = Calendar.getInstance()

            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(

                this,

                { _, year, month, dayOfMonth ->

                    val dataFormatada =

                        String.format(
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                        )

                    edtData.setText(dataFormatada)
                },

                ano,
                mes,
                dia
            )

            datePicker.show()
        }

        btnVoltar.setOnClickListener {

            finish()
        }

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
        SELECT titulo,
               descricao,
               data,
               status,
               prioridade
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

            // Descrição
            edtDescricao.setText(
                cursor.getString(1)
            )

            // Data
            edtData.setText(
                cursor.getString(2)
            )

            // Status
            val status =
                cursor.getString(3)

            // Prioridade
            val prioridade =
                cursor.getString(4)

            // Spinner status
            spnStatus.setSelection(

                (
                        spnStatus.adapter
                                as ArrayAdapter<String>
                        )

                    .getPosition(status)
            )

            // Spinner prioridade
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

        // Dados
        val titulo =
            edtTitulo.text.toString()

        val descricao =
            edtDescricao.text.toString()

        val data =
            edtData.text.toString()

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

            // Atualiza descrição
            put(
                "descricao",
                descricao
            )

            // Atualiza data
            put(
                "data",
                data
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