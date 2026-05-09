package com.example.gestaotarefas

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarTarefaActivity : AppCompatActivity() {

    lateinit var dbHelper: DatabaseHelper

    lateinit var edtTitulo: EditText
    lateinit var spnPrioridade: Spinner
    lateinit var spnStatus: Spinner
    lateinit var btnSalvar: Button

    var tarefaId: Int = -1
    var usuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_editar_tarefa)

        dbHelper = DatabaseHelper(this)

        edtTitulo = findViewById(R.id.edtTituloEditar)
        spnPrioridade = findViewById(R.id.spnPrioridadeEditar)
        spnStatus = findViewById(R.id.spnStatusEditar)
        btnSalvar = findViewById(R.id.btnSalvarEditar)

        val prioridades = arrayOf("Baixa Prioridade", "Média Prioridade", "Alta Prioridade")
        val statusList = arrayOf("A fazer", "Em andamento", "Quase concluída")

        spnPrioridade.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prioridades)

        spnStatus.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusList)

        tarefaId = intent.getIntExtra("id", -1)
        usuario = intent.getStringExtra("usuario") ?: ""

        carregarTarefa()

        btnSalvar.setOnClickListener {
            atualizarTarefa()
        }
    }

    private fun carregarTarefa() {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT titulo, status, prioridade FROM tarefas WHERE id=?",
            arrayOf(tarefaId.toString())
        )

        if (cursor.moveToFirst()) {

            edtTitulo.setText(cursor.getString(0))

            val status = cursor.getString(1)
            val prioridade = cursor.getString(2)

            spnStatus.setSelection(
                (spnStatus.adapter as ArrayAdapter<String>).getPosition(status)
            )

            spnPrioridade.setSelection(
                (spnPrioridade.adapter as ArrayAdapter<String>).getPosition(prioridade)
            )
        }

        cursor.close()
    }

    private fun atualizarTarefa() {

        val titulo = edtTitulo.text.toString()

        if (titulo.isEmpty()) {

            Toast.makeText(this, "Digite um título", Toast.LENGTH_SHORT).show()

            return
        }

        val valores = ContentValues().apply {

            put("titulo", titulo)
            put("status", spnStatus.selectedItem.toString())
            put("prioridade", spnPrioridade.selectedItem.toString())
            put("usuario", usuario)
        }

        dbHelper.writableDatabase.update(
            "tarefas",
            valores,
            "id=?",
            arrayOf(tarefaId.toString())
        )

        Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()

        finish()
    }
}