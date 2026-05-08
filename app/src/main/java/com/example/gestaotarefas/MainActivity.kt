package com.example.gestaotarefas

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.database.Cursor
import android.content.SharedPreferences
import android.widget.TextView
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var dbHelper: DatabaseHelper
    lateinit var edtTitulo: EditText
    lateinit var spnPrioridade: Spinner
    lateinit var spnStatus: Spinner

    var tarefaId: Int = -1
    var usuario: String? = null
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("login", MODE_PRIVATE)

        dbHelper = DatabaseHelper(this)

        edtTitulo = findViewById(R.id.edtTitulo)
        spnPrioridade = findViewById(R.id.spnPrioridade)
        spnStatus = findViewById(R.id.spnStatus)

        val prioridades = arrayOf("Baixa", "Média", "Alta")
        val statusList = arrayOf("Pendente", "Em andamento", "Concluída")

        usuario = intent.getStringExtra("usuario")

        if (usuario.isNullOrEmpty()) {
            usuario = prefs.getString("usuario", "") ?: ""
        }

        val txtOla = findViewById<TextView>(R.id.txtOla)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val primeiroNome = usuario?.split(" ")?.firstOrNull() ?: "Usuário"

        txtOla.text = "Olá, $primeiroNome"

        btnLogout.setOnClickListener {

            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)

            finish()
        }

        spnPrioridade.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prioridades)

        spnStatus.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusList)

        // verifica se veio para editar
        tarefaId = intent.getIntExtra("id", -1)

        if (tarefaId != -1) {
            carregarTarefa(tarefaId)
        }

        findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            salvarOuAtualizar()
        }

        findViewById<Button>(R.id.btnVerTarefas).setOnClickListener {

            val intent = Intent(this, ListaTarefasActivity::class.java)

            intent.putExtra("usuario", usuario)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        limparCampos()
    }

    private fun salvarOuAtualizar() {

        val titulo = edtTitulo.text.toString().trim()

        // validação
        if (titulo.isEmpty()) {

            Toast.makeText(this, "Digite um título", Toast.LENGTH_SHORT).show()

            return
        }

        val status = spnStatus.selectedItem.toString()
        val prioridade = spnPrioridade.selectedItem.toString()

        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {

            put("titulo", titulo)
            put("status", status)
            put("prioridade", prioridade)
            put("usuario", usuario)
        }

        if (tarefaId == -1) {

            // NOVA TAREFA
            db.insert("tarefas", null, valores)

            Toast.makeText(this, "Tarefa criada!", Toast.LENGTH_SHORT).show()

            limparCampos()

        } else {

            // EDITAR
            db.update(
                "tarefas",
                valores,
                "id=?",
                arrayOf(tarefaId.toString())
            )

            Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()

            tarefaId = -1

            limparCampos()
        }
    }

    private fun carregarTarefa(id: Int) {

        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            "SELECT id, titulo, status, prioridade FROM tarefas WHERE id=?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {

            edtTitulo.setText(cursor.getString(1))

            val status = cursor.getString(2)
            val prioridade = cursor.getString(3)

            val statusIndex =
                (spnStatus.adapter as ArrayAdapter<String>).getPosition(status)

            if (statusIndex >= 0) {
                spnStatus.setSelection(statusIndex)
            }

            val prioridadeIndex =
                (spnPrioridade.adapter as ArrayAdapter<String>).getPosition(prioridade)

            if (prioridadeIndex >= 0) {
                spnPrioridade.setSelection(prioridadeIndex)
            }
        }

        cursor.close()
    }

    private fun limparCampos() {

        edtTitulo.setText("")

        spnPrioridade.setSelection(0)
        spnStatus.setSelection(0)

        tarefaId = -1

        intent.removeExtra("id")
    }
}