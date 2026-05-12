package com.example.gestaotarefas

// Importa ContentValues para armazenar dados antes de inserir/atualizar no banco
import android.content.ContentValues

// Importa Intent para troca de telas
import android.content.Intent

// Importa AppCompatActivity
import androidx.appcompat.app.AppCompatActivity

// Importa Bundle utilizado no ciclo de vida da Activity
import android.os.Bundle

// Importa todos os widgets do Android
import android.widget.*

// Importa Cursor para leitura de dados do banco
import android.database.Cursor

// Importa SharedPreferences para armazenamento local simples
import android.content.SharedPreferences

// Importa TextView
import android.widget.TextView

// Importa Button
import android.widget.Button

// Activity principal do aplicativo
class MainActivity : AppCompatActivity() {

    // Instância do banco de dados
    lateinit var dbHelper: DatabaseHelper

    // Campo de texto do título da tarefa
    lateinit var edtTitulo: EditText

    // Spinner de prioridade
    lateinit var spnPrioridade: Spinner

    // Spinner de status
    lateinit var spnStatus: Spinner

    // Variável que armazenará o ID da tarefa
    // -1 significa que ainda não há tarefa selecionada
    var tarefaId: Int = -1

    // Variável que armazenará o usuário logado
    var usuario: String? = null

    // SharedPreferences usadas para salvar login localmente
    lateinit var prefs: SharedPreferences

    // Método chamado quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {

        // Chama método da classe pai
        super.onCreate(savedInstanceState)

        // Define layout XML da tela
        setContentView(R.layout.activity_main)

        // Obtém SharedPreferences chamadas "login"
        prefs = getSharedPreferences("login", MODE_PRIVATE)

        // Inicializa banco de dados
        dbHelper = DatabaseHelper(this)

        // Liga componentes XML às variáveis Kotlin
        edtTitulo = findViewById(R.id.edtTitulo)

        spnPrioridade = findViewById(R.id.spnPrioridade)

        spnStatus = findViewById(R.id.spnStatus)

        // Lista de prioridades disponíveis
        val prioridades = arrayOf(
            "Baixa Prioridade",
            "Média Prioridade",
            "Alta Prioridade"
        )

        // Lista de status disponíveis
        val statusList = arrayOf(
            "A fazer",
            "Em andamento",
            "Quase concluída"
        )

        // Recebe usuário enviado pela tela anterior
        usuario = intent.getStringExtra("usuario")

        // Caso usuário não exista no Intent
        // recupera usuário salvo no SharedPreferences
        if (usuario.isNullOrEmpty()) {

            usuario = prefs.getString("usuario", "") ?: ""
        }

        // Referência do texto de saudação
        val txtOla = findViewById<TextView>(R.id.txtOla)

        // Referência do botão logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Pega apenas o primeiro nome do usuário
        val primeiroNome =
            usuario?.split(" ")?.firstOrNull() ?: "Usuário"

        // Define texto de saudação
        txtOla.text = "Olá, $primeiroNome"

        // Evento de clique do botão logout
        btnLogout.setOnClickListener {

            // Limpa SharedPreferences
            prefs.edit().clear().apply()

            // Cria Intent para voltar ao login
            val intent = Intent(this, LoginActivity::class.java)

            // Abre tela de login
            startActivity(intent)

            // Fecha tela atual
            finish()
        }

        // Define adapter do Spinner de prioridade
        spnPrioridade.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                prioridades
            )

        // Define adapter do Spinner de status
        spnStatus.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statusList
            )

        // Verifica se veio um ID para edição
        tarefaId = intent.getIntExtra("id", -1)

        // Caso exista ID válido
        if (tarefaId != -1) {

            // Carrega dados da tarefa
            carregarTarefa(tarefaId)
        }

        // Evento de clique do botão salvar
        findViewById<Button>(R.id.btnSalvar).setOnClickListener {

            // Chama função de salvar ou atualizar
            salvarOuAtualizar()
        }

        // Evento do botão visualizar tarefas
        findViewById<Button>(R.id.btnVerTarefas).setOnClickListener {

            // Cria Intent para abrir lista de tarefas
            val intent =
                Intent(this, ListaTarefasActivity::class.java)

            // Envia usuário para próxima tela
            intent.putExtra("usuario", usuario)

            // Abre Activity
            startActivity(intent)
        }
    }

    // Método chamado quando a Activity volta ao foco
    override fun onResume() {

        // Chama método da classe pai
        super.onResume()

        // Limpa os campos
        limparCampos()
    }

    // Função responsável por salvar ou atualizar tarefas
    private fun salvarOuAtualizar() {

        // Obtém título digitado
        val titulo = edtTitulo.text.toString().trim()

        // Validação do campo título
        if (titulo.isEmpty()) {

            // Exibe mensagem de erro
            Toast.makeText(
                this,
                "Digite um título",
                Toast.LENGTH_SHORT
            ).show()

            // Interrompe execução
            return
        }

        // Obtém status selecionado
        val status = spnStatus.selectedItem.toString()

        // Obtém prioridade selecionada
        val prioridade = spnPrioridade.selectedItem.toString()

        // Obtém banco em modo escrita
        val db = dbHelper.writableDatabase

        // Cria objeto contendo os dados da tarefa
        val valores = ContentValues().apply {

            // Salva título
            put("titulo", titulo)

            // Salva status
            put("status", status)

            // Salva prioridade
            put("prioridade", prioridade)

            // Salva usuário responsável
            put("usuario", usuario)
        }

        // Verifica se é uma nova tarefa
        if (tarefaId == -1) {

            // Insere nova tarefa no banco
            db.insert("tarefas", null, valores)

            // Exibe mensagem de sucesso
            Toast.makeText(
                this,
                "Tarefa criada!",
                Toast.LENGTH_SHORT
            ).show()

            // Limpa campos
            limparCampos()

        } else {

            // Atualiza tarefa existente
            db.update(
                "tarefas",
                valores,
                "id=?",
                arrayOf(tarefaId.toString())
            )

            // Exibe mensagem de sucesso
            Toast.makeText(
                this,
                "Tarefa atualizada!",
                Toast.LENGTH_SHORT
            ).show()

            // Reseta ID
            tarefaId = -1

            // Limpa campos
            limparCampos()
        }
    }

    // Função responsável por carregar dados da tarefa
    private fun carregarTarefa(id: Int) {

        // Obtém banco em modo leitura
        val db = dbHelper.readableDatabase

        // Executa consulta SQL
        val cursor: Cursor = db.rawQuery(
            "SELECT id, titulo, status, prioridade FROM tarefas WHERE id=?",
            arrayOf(id.toString())
        )

        // Verifica se encontrou resultado
        if (cursor.moveToFirst()) {

            // Define título no EditText
            edtTitulo.setText(cursor.getString(1))

            // Obtém status da tarefa
            val status = cursor.getString(2)

            // Obtém prioridade da tarefa
            val prioridade = cursor.getString(3)

            // Busca posição do status no Spinner
            val statusIndex =
                (spnStatus.adapter as ArrayAdapter<String>)
                    .getPosition(status)

            // Verifica se encontrou posição válida
            if (statusIndex >= 0) {

                // Define item selecionado
                spnStatus.setSelection(statusIndex)
            }

            // Busca posição da prioridade no Spinner
            val prioridadeIndex =
                (spnPrioridade.adapter as ArrayAdapter<String>)
                    .getPosition(prioridade)

            // Verifica se encontrou posição válida
            if (prioridadeIndex >= 0) {

                // Define item selecionado
                spnPrioridade.setSelection(prioridadeIndex)
            }
        }

        // Fecha cursor
        cursor.close()
    }

    // Função responsável por limpar os campos
    private fun limparCampos() {

        // Limpa EditText
        edtTitulo.setText("")

        // Define primeira opção do Spinner de prioridade
        spnPrioridade.setSelection(0)

        // Define primeira opção do Spinner de status
        spnStatus.setSelection(0)

        // Reseta ID da tarefa
        tarefaId = -1

        // Remove extra "id" da Intent
        intent.removeExtra("id")
    }
}