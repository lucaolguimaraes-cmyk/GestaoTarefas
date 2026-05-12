package com.example.gestaotarefas

// Importa a classe ContentValues para armazenar valores antes de atualizar no banco
import android.content.ContentValues

// Importa Bundle, utilizado no ciclo de vida da Activity
import android.os.Bundle

// Importa todos os componentes de widgets do Android
import android.widget.*

// Importa a classe AppCompatActivity
import androidx.appcompat.app.AppCompatActivity

// Activity responsável por editar tarefas existentes
class EditarTarefaActivity : AppCompatActivity() {

    // Instância do banco de dados
    lateinit var dbHelper: DatabaseHelper

    // Campo de texto para editar o título da tarefa
    lateinit var edtTitulo: EditText

    // Spinner para selecionar prioridade
    lateinit var spnPrioridade: Spinner

    // Spinner para selecionar status
    lateinit var spnStatus: Spinner

    // Botão para salvar alterações
    lateinit var btnSalvar: Button

    // Variável que armazenará o ID da tarefa
    // Começa com -1 indicando valor inválido
    var tarefaId: Int = -1

    // Variável que armazenará o usuário da tarefa
    var usuario: String = ""

    // Método executado quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {

        // Chama o método da classe pai
        super.onCreate(savedInstanceState)

        // Define o layout XML da tela
        setContentView(R.layout.activity_editar_tarefa)

        // Inicializa o helper do banco
        dbHelper = DatabaseHelper(this)

        // Conecta os componentes do XML com as variáveis Kotlin
        edtTitulo = findViewById(R.id.edtTituloEditar)

        spnPrioridade = findViewById(R.id.spnPrioridadeEditar)

        spnStatus = findViewById(R.id.spnStatusEditar)

        btnSalvar = findViewById(R.id.btnSalvarEditar)

        // Array contendo as opções de prioridade
        val prioridades = arrayOf(
            "Baixa Prioridade",
            "Média Prioridade",
            "Alta Prioridade"
        )

        // Array contendo as opções de status
        val statusList = arrayOf(
            "A fazer",
            "Em andamento",
            "Quase concluída"
        )

        // Define o adapter do Spinner de prioridade
        // O adapter é responsável por preencher os itens do Spinner
        spnPrioridade.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                prioridades
            )

        // Define o adapter do Spinner de status
        spnStatus.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statusList
            )

        // Recebe o ID da tarefa enviado pela tela anterior
        // Caso não exista, retorna -1
        tarefaId = intent.getIntExtra("id", -1)

        // Recebe o nome do usuário enviado pela tela anterior
        // ?: "" evita valor nulo
        usuario = intent.getStringExtra("usuario") ?: ""

        // Chama função responsável por carregar os dados da tarefa
        carregarTarefa()

        // Evento de clique do botão salvar
        btnSalvar.setOnClickListener {

            // Chama a função que atualiza a tarefa no banco
            atualizarTarefa()
        }
    }

    // Função responsável por carregar os dados da tarefa
    private fun carregarTarefa() {

        // Obtém o banco em modo leitura
        val db = dbHelper.readableDatabase

        // Executa consulta SQL buscando os dados da tarefa pelo ID
        val cursor = db.rawQuery(
            "SELECT titulo, status, prioridade FROM tarefas WHERE id=?",

            // Substitui o ? pelo ID da tarefa
            arrayOf(tarefaId.toString())
        )

        // Verifica se encontrou algum resultado
        if (cursor.moveToFirst()) {

            // Define o título da tarefa no EditText
            edtTitulo.setText(cursor.getString(0))

            // Obtém o status da tarefa
            val status = cursor.getString(1)

            // Obtém a prioridade da tarefa
            val prioridade = cursor.getString(2)

            // Define automaticamente o item correto no Spinner de status
            spnStatus.setSelection(
                (spnStatus.adapter as ArrayAdapter<String>).getPosition(status)
            )

            // Define automaticamente o item correto no Spinner de prioridade
            spnPrioridade.setSelection(
                (spnPrioridade.adapter as ArrayAdapter<String>).getPosition(prioridade)
            )
        }

        // Fecha o cursor para liberar memória
        cursor.close()
    }

    // Função responsável por atualizar a tarefa
    private fun atualizarTarefa() {

        // Obtém o título digitado
        val titulo = edtTitulo.text.toString()

        // Verifica se o campo está vazio
        if (titulo.isEmpty()) {

            // Exibe mensagem de erro
            Toast.makeText(this, "Digite um título", Toast.LENGTH_SHORT).show()

            // Interrompe a execução da função
            return
        }

        // Cria objeto ContentValues para armazenar os novos dados
        val valores = ContentValues().apply {

            // Atualiza o título
            put("titulo", titulo)

            // Atualiza o status selecionado
            put("status", spnStatus.selectedItem.toString())

            // Atualiza a prioridade selecionada
            put("prioridade", spnPrioridade.selectedItem.toString())

            // Atualiza o usuário
            put("usuario", usuario)
        }

        // Executa atualização no banco de dados
        dbHelper.writableDatabase.update(

            // Nome da tabela
            "tarefas",

            // Valores atualizados
            valores,

            // Condição WHERE
            "id=?",

            // Valor que substituirá o ?
            arrayOf(tarefaId.toString())
        )

        // Exibe mensagem de sucesso
        Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()

        // Fecha a tela atual
        finish()
    }
}