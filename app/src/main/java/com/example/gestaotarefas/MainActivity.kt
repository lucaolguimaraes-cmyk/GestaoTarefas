package com.example.gestaotarefas

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.database.Cursor
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    // Banco de dados
    lateinit var dbHelper: DatabaseHelper

    // Campo título
    lateinit var edtTitulo: EditText

    // Spinner prioridade
    lateinit var spnPrioridade: Spinner

    // Spinner status
    lateinit var spnStatus: Spinner

    // ID da tarefa
    var tarefaId: Int = -1

    // Usuário logado
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

        val btnVerTarefas =
            findViewById<Button>(R.id.btnVerTarefas)

        // NOVO BOTÃO
        val btnConcluidas =
            findViewById<Button>(R.id.btnConcluidas)

        // Lista prioridades
        val prioridades = arrayOf(

            "Baixa Prioridade",

            "Média Prioridade",

            "Alta Prioridade"
        )

        // LISTA STATUS MODIFICADA
        val statusList = arrayOf(

            "A fazer",

            "Em andamento",

            "Quase concluída",
        )

        // Recebe usuário
        usuario =
            intent.getStringExtra("usuario")

        // Se não veio pelo intent
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

        // LOGOUT
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

        // Verifica edição
        tarefaId =
            intent.getIntExtra("id", -1)

        // Se estiver editando
        if (tarefaId != -1) {

            carregarTarefa(tarefaId)
        }

        // SALVAR
        btnSalvar.setOnClickListener {

            salvarOuAtualizar()
        }

        // VER TAREFAS
        btnVerTarefas.setOnClickListener {

            val intent =

                Intent(
                    this,
                    PrioridadesActivity::class.java
                )

            intent.putExtra(
                "usuario",
                usuario
            )

            startActivity(intent)
        }

        // NOVO
        // VER CONCLUÍDAS
        btnConcluidas.setOnClickListener {

            val intent =

                Intent(
                    this,
                    ConcluidasActivity::class.java
                )

            intent.putExtra(
                "usuario",
                usuario
            )

            startActivity(intent)
        }
    }

    // Quando Activity volta
    override fun onResume() {

        super.onResume()

        limparCampos()
    }

    // Salvar ou atualizar
    private fun salvarOuAtualizar() {

        val titulo =
            edtTitulo.text.toString().trim()

        // Validação
        if (titulo.isEmpty()) {

            Toast.makeText(

                this,

                "Digite um título",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        // Dados
        val status =
            spnStatus.selectedItem.toString()

        val prioridade =
            spnPrioridade.selectedItem.toString()

        val db =
            dbHelper.writableDatabase

        // Valores
        val valores = ContentValues().apply {

            put("titulo", titulo)

            put("status", status)

            put("prioridade", prioridade)

            put("usuario", usuario)
        }

        // NOVA TAREFA
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

            limparCampos()

        } else {

            // ATUALIZA
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

            limparCampos()
        }
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
                       status,
                       prioridade
                FROM tarefas
                WHERE id=?
                """.trimIndent(),

                arrayOf(id.toString())
            )

        // Se encontrou
        if (cursor.moveToFirst()) {

            // Título
            edtTitulo.setText(
                cursor.getString(1)
            )

            // Status
            val status =
                cursor.getString(2)

            // Prioridade
            val prioridade =
                cursor.getString(3)

            // Posição status
            val statusIndex =

                (spnStatus.adapter
                        as ArrayAdapter<String>)

                    .getPosition(status)

            // Se encontrou
            if (statusIndex >= 0) {

                spnStatus.setSelection(
                    statusIndex
                )
            }

            // Posição prioridade
            val prioridadeIndex =

                (spnPrioridade.adapter
                        as ArrayAdapter<String>)

                    .getPosition(prioridade)

            // Se encontrou
            if (prioridadeIndex >= 0) {

                spnPrioridade.setSelection(
                    prioridadeIndex
                )
            }
        }

        cursor.close()
    }

    // Limpa campos
    private fun limparCampos() {

        // Limpa texto
        edtTitulo.setText("")

        // Primeira prioridade
        spnPrioridade.setSelection(0)

        // Primeiro status
        spnStatus.setSelection(0)

        // Reseta ID
        tarefaId = -1

        // Remove extra
        intent.removeExtra("id")
    }
}