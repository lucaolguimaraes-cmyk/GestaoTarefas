package com.example.gestaotarefas

// Importa a classe Intent para troca de telas
import android.content.Intent

// Importa Bundle utilizado no ciclo de vida da Activity
import android.os.Bundle

// Importa a classe View
import android.view.View

// Importa AdapterView para trabalhar com eventos do Spinner
import android.widget.AdapterView

// Importa EditText
import android.widget.EditText

// Importa Spinner
import android.widget.Spinner

// Importa ArrayAdapter para preencher os Spinners
import android.widget.ArrayAdapter

// Importa Button
import android.widget.Button

// Importa Toast para mensagens rápidas
import android.widget.Toast

// Importa AppCompatActivity
import androidx.appcompat.app.AppCompatActivity

// Importa listener de mudança de texto em tempo real
import androidx.core.widget.addTextChangedListener

// Importa ItemTouchHelper para swipe no RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

// Importa Layout linear para RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

// Importa RecyclerView
import androidx.recyclerview.widget.RecyclerView

// Activity responsável pela listagem das tarefas
class ListaTarefasActivity : AppCompatActivity() {

    // Instância do banco de dados
    lateinit var db: DatabaseHelper

    // RecyclerView que exibirá as tarefas
    lateinit var recycler: RecyclerView

    // Adapter responsável por ligar os dados ao RecyclerView
    lateinit var adapter: TarefaAdapter

    // Campo de busca
    lateinit var edtBusca: EditText

    // Spinner de filtro por status
    lateinit var spnStatus: Spinner

    // Spinner de filtro por prioridade
    lateinit var spnPrioridade: Spinner

    // Lista principal contendo todas as tarefas
    var lista = ArrayList<Tarefa>()

    // Lista contendo apenas tarefas filtradas
    var listaFiltrada = ArrayList<Tarefa>()

    // Usuário logado
    var usuario: String? = null

    // Método chamado ao criar a Activity
    override fun onCreate(savedInstanceState: Bundle?) {

        // Chama método da classe pai
        super.onCreate(savedInstanceState)

        // Define layout XML da tela
        setContentView(R.layout.activity_lista)

        // Referência do botão voltar
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        // Recebe usuário vindo da tela anterior
        usuario = intent.getStringExtra("usuario")

        // Evento de clique do botão voltar
        btnVoltar.setOnClickListener {

            // Fecha a tela atual
            finish()
        }

        // Inicializa banco de dados
        db = DatabaseHelper(this)

        // Liga componentes XML às variáveis Kotlin
        recycler = findViewById(R.id.recycler)

        edtBusca = findViewById(R.id.edtBusca)

        spnStatus = findViewById(R.id.spnFiltroStatus)

        spnPrioridade = findViewById(R.id.spnFiltroPrioridade)

        // Define layout do RecyclerView em formato vertical
        recycler.layoutManager = LinearLayoutManager(this)

        // Informa que o tamanho do RecyclerView não muda
        // Isso melhora desempenho
        recycler.setHasFixedSize(true)

        // Ativa botão de voltar na ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Criação do adapter do RecyclerView
        adapter = TarefaAdapter(

            // Lista que será exibida
            listaFiltrada,

            // Função executada ao editar tarefa
            onEditar = { tarefa ->

                // Cria Intent para abrir tela de edição
                val intent = Intent(this, EditarTarefaActivity::class.java)

                // Envia ID da tarefa
                intent.putExtra("id", tarefa.id)

                // Envia usuário
                intent.putExtra("usuario", usuario)

                // Abre tela de edição
                startActivity(intent)
            },

            // Função executada ao deletar tarefa
            onDeletar = { tarefa ->

                // Chama função de deletar
                deletar(tarefa.id)
            }
        )

        // Define adapter no RecyclerView
        recycler.adapter = adapter

        // Configura os Spinners
        configurarSpinners()

        // Carrega tarefas do banco
        carregarTarefas()

        // Listener de busca em tempo real
        edtBusca.addTextChangedListener {

            // Atualiza filtros conforme usuário digita
            aplicarFiltros()
        }

        // Evento de seleção do Spinner de status
        spnStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                // Chamado ao selecionar item
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    // Atualiza filtros
                    aplicarFiltros()
                }

                // Chamado quando nada é selecionado
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // Evento de seleção do Spinner de prioridade
        spnPrioridade.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                // Chamado ao selecionar item
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    // Atualiza filtros
                    aplicarFiltros()
                }

                // Chamado quando nada é selecionado
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // Configuração do swipe lateral para deletar tarefa
        val swipe = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(

                // Sem movimentação vertical/horizontal
                0,

                // Permite swipe para esquerda
                ItemTouchHelper.LEFT
            ) {

                // Método obrigatório do SimpleCallback
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    // Não permite mover itens
                    return false
                }

                // Chamado quando item é arrastado para o lado
                override fun onSwiped(
                    holder: RecyclerView.ViewHolder,
                    dir: Int
                ) {

                    // Obtém posição do item
                    val pos = holder.bindingAdapterPosition

                    // Verifica se posição é válida
                    if (pos != RecyclerView.NO_POSITION) {

                        // Obtém tarefa da lista filtrada
                        val tarefa = listaFiltrada[pos]

                        // Deleta tarefa
                        deletar(tarefa.id)
                    }
                }
            }
        )

        // Liga swipe ao RecyclerView
        swipe.attachToRecyclerView(recycler)
    }

    // Método chamado quando a Activity volta ao foco
    override fun onResume() {

        // Chama método da classe pai
        super.onResume()

        // Recarrega tarefas
        carregarTarefas()
    }

    // Função responsável por configurar os Spinners
    private fun configurarSpinners() {

        // Lista de opções de status
        val status = arrayOf(
            "Sem filtro (STATUS)",
            "A fazer",
            "Em andamento",
            "Quase concluída"
        )

        // Lista de opções de prioridade
        val prioridade = arrayOf(
            "Sem filtro (PRIORIDADE)",
            "Baixa Prioridade",
            "Média Prioridade",
            "Alta Prioridade"
        )

        // Define adapter do Spinner de status
        spnStatus.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                status
            )

        // Define adapter do Spinner de prioridade
        spnPrioridade.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                prioridade
            )
    }

    // Função responsável por carregar tarefas do banco
    private fun carregarTarefas() {

        // Limpa lista atual
        lista.clear()

        // Consulta SQL buscando tarefas do usuário
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, titulo, status, prioridade FROM tarefas WHERE usuario = ?",

            // Substitui ? pelo usuário logado
            arrayOf(usuario ?: "")
        )

        // Percorre todos os resultados encontrados
        while (cursor.moveToNext()) {

            // Adiciona tarefa à lista
            lista.add(
                Tarefa(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
            )
        }

        // Fecha cursor
        cursor.close()

        // Atualiza filtros
        aplicarFiltros()
    }

    // Função responsável por aplicar busca e filtros
    private fun aplicarFiltros() {

        // Obtém texto digitado
        val texto = edtBusca.text.toString()

        // Obtém filtro de status selecionado
        val statusFiltro = spnStatus.selectedItem.toString()

        // Obtém filtro de prioridade selecionado
        val prioridadeFiltro = spnPrioridade.selectedItem.toString()

        // Limpa lista filtrada
        listaFiltrada.clear()

        // Percorre todas as tarefas
        for (t in lista) {

            // Verifica se título contém texto pesquisado
            val matchTexto =
                t.titulo.lowercase().contains(texto.lowercase())

            // Verifica filtro de status
            val matchStatus =
                statusFiltro == "Sem filtro (STATUS)" ||
                        t.status == statusFiltro

            // Verifica filtro de prioridade
            val matchPrioridade =
                prioridadeFiltro == "Sem filtro (PRIORIDADE)" ||
                        t.prioridade == prioridadeFiltro

            // Se todos os filtros baterem
            if (matchTexto && matchStatus && matchPrioridade) {

                // Adiciona tarefa na lista filtrada
                listaFiltrada.add(t)
            }
        }

        // Atualiza RecyclerView
        adapter.notifyDataSetChanged()
    }

    // Função responsável por deletar tarefa
    private fun deletar(id: Int) {

        // Remove tarefa do banco pelo ID
        db.writableDatabase.delete(
            "tarefas",
            "id=?",
            arrayOf(id.toString())
        )

        // Exibe mensagem de sucesso
        Toast.makeText(this, "Tarefa deletada", Toast.LENGTH_SHORT).show()

        // Recarrega tarefas
        carregarTarefas()
    }
}