package com.example.gestaotarefas

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.database.Cursor
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    // Banco
    lateinit var dbHelper: DatabaseHelper

    // Campos
    lateinit var edtTitulo: EditText

    lateinit var edtDescricao: EditText

    lateinit var edtData: EditText

    // Spinners
    lateinit var spnPrioridade: Spinner

    lateinit var spnStatus: Spinner

    // ID tarefa
    var tarefaId: Int = -1

    // Usuário
    var usuario: String? = null

    // SharedPreferences
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // SharedPreferences
        prefs = getSharedPreferences(
            "login",
            MODE_PRIVATE
        )

        // Banco
        dbHelper = DatabaseHelper(this)

        // Componentes
        edtTitulo =
            findViewById(R.id.edtTitulo)

        edtDescricao =
            findViewById(R.id.edtDescricao)

        edtData =
            findViewById(R.id.edtData)

        edtData.addTextChangedListener(
            DateMask(edtData)
        )

        spnPrioridade =
            findViewById(R.id.spnPrioridade)

        spnStatus =
            findViewById(R.id.spnStatus)

        val txtOla =
            findViewById<TextView>(R.id.txtOla)

        val btnLogout =
            findViewById<Button>(R.id.btnLogout)

        val btnSalvar =
            findViewById<Button>(R.id.btnSalvar)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        // Prioridades
        val prioridades = arrayOf(

            "Baixa Prioridade",

            "Média Prioridade",

            "Alta Prioridade"
        )

        // Status
        val statusList = arrayOf(

            "A fazer",

            "Em andamento",

            "Quase concluída"
        )

        // Usuário
        usuario =
            intent.getStringExtra("usuario")

        // Se não veio
        if (usuario.isNullOrEmpty()) {

            usuario =
                prefs.getString(
                    "usuario",
                    ""
                ) ?: ""
        }

        // Primeiro nome
        val primeiroNome =

            usuario?.split(" ")
                ?.firstOrNull()

                ?: "Usuário"

        // Saudação
        txtOla.text =
            "Olá, $primeiroNome"

        // Logout
        btnLogout.setOnClickListener {

            prefs.edit()
                .clear()
                .apply()

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            startActivity(intent)

            finish()
        }


        btnVoltar.setOnClickListener {

            finish()
        }

        // Spinner prioridade
        spnPrioridade.adapter =

            ArrayAdapter(

                this,

                android.R.layout.simple_spinner_dropdown_item,

                prioridades
            )

        // Spinner status
        spnStatus.adapter =

            ArrayAdapter(

                this,

                android.R.layout.simple_spinner_dropdown_item,

                statusList
            )

        // ID tarefa
        tarefaId =
            intent.getIntExtra("id", -1)

        // Editando
        if (tarefaId != -1) {

            carregarTarefa(tarefaId)
        }

        // Salvar
        btnSalvar.setOnClickListener {

            salvarOuAtualizar()
        }
    }

    // Quando voltar
    override fun onResume() {

        super.onResume()

        limparCampos()
    }

    // Salvar
    private fun salvarOuAtualizar() {

        val titulo =
            edtTitulo.text.toString().trim()

        val descricao =
            edtDescricao.text.toString().trim()

        val data =
            edtData.text.toString().trim()

        // Validação
        if (titulo.isEmpty()) {

            Toast.makeText(

                this,

                "Digite um título",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        val status =
            spnStatus.selectedItem.toString()

        val prioridade =
            spnPrioridade.selectedItem.toString()

        val db =
            dbHelper.writableDatabase

        // Valores
        val valores = ContentValues().apply {

            put("titulo", titulo)

            put("descricao", descricao)

            put("data", data)

            put("status", status)

            put("prioridade", prioridade)

            put("usuario", usuario)
        }

        // Nova tarefa
        if (tarefaId == -1) {

            db.insert(

                "tarefas",

                null,

                valores
            )

            Toast.makeText(

                this,

                "Tarefa criada!",

                Toast.LENGTH_SHORT

            ).show()

        } else {

            // Atualiza
            db.update(

                "tarefas",

                valores,

                "id=?",

                arrayOf(
                    tarefaId.toString()
                )
            )

            Toast.makeText(

                this,

                "Tarefa atualizada!",

                Toast.LENGTH_SHORT

            ).show()

            tarefaId = -1
        }

        limparCampos()
    }

    // Carrega tarefa
    private fun carregarTarefa(id: Int) {

        val db =
            dbHelper.readableDatabase

        val cursor: Cursor =

            db.rawQuery(

                """
                SELECT id,
                       titulo,
                       descricao,
                       data,
                       status,
                       prioridade
                FROM tarefas
                WHERE id=?
                """.trimIndent(),

                arrayOf(id.toString())
            )

        if (cursor.moveToFirst()) {

            // Título
            edtTitulo.setText(
                cursor.getString(1)
            )

            // Descrição
            edtDescricao.setText(
                cursor.getString(2)
            )

            // Data
            edtData.setText(
                cursor.getString(3)
            )

            // Status
            val status =
                cursor.getString(4)

            // Prioridade
            val prioridade =
                cursor.getString(5)

            // Status spinner
            val statusIndex =

                (spnStatus.adapter
                        as ArrayAdapter<String>)

                    .getPosition(status)

            if (statusIndex >= 0) {

                spnStatus.setSelection(
                    statusIndex
                )
            }

            // Prioridade spinner
            val prioridadeIndex =

                (spnPrioridade.adapter
                        as ArrayAdapter<String>)

                    .getPosition(prioridade)

            if (prioridadeIndex >= 0) {

                spnPrioridade.setSelection(
                    prioridadeIndex
                )
            }
        }

        cursor.close()
    }

    // Limpa
    private fun limparCampos() {

        edtTitulo.setText("")

        edtDescricao.setText("")

        edtData.setText("")

        spnPrioridade.setSelection(0)

        spnStatus.setSelection(0)

        tarefaId = -1

        intent.removeExtra("id")
    }
}