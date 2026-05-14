package com.example.gestaotarefas

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaTarefasActivity : AppCompatActivity() {

    // Banco
    lateinit var db: DatabaseHelper

    // Recycler
    lateinit var recycler: RecyclerView

    // Adapter
    lateinit var adapter: TarefaAdapter

    // Busca
    lateinit var edtBusca: EditText

    // Spinner status
    lateinit var spnStatus: Spinner

    // Título
    lateinit var txtTitulo: TextView

    // Lista completa
    var lista = ArrayList<Tarefa>()

    // Lista filtrada
    var listaFiltrada = ArrayList<Tarefa>()

    // Usuário
    var usuario: String? = null

    // Prioridade
    var prioridadeSelecionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lista)

        // Banco
        db = DatabaseHelper(this)

        // Componentes
        recycler = findViewById(R.id.recycler)

        edtBusca = findViewById(R.id.edtBusca)

        spnStatus = findViewById(R.id.spnFiltroStatus)

        txtTitulo = findViewById(R.id.txtTitulo)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        // Dados recebidos
        usuario =
            intent.getStringExtra("usuario")

        prioridadeSelecionada =
            intent.getStringExtra("prioridade")

        // Título
        txtTitulo.text =
            prioridadeSelecionada

        // Voltar
        btnVoltar.setOnClickListener {

            finish()
        }

        // Recycler
        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.setHasFixedSize(true)

        // Adapter
        adapter = TarefaAdapter(

            listaFiltrada,

            // EDITAR
            onEditar = { tarefa ->

                val intent = Intent(

                    this,

                    EditarTarefasActivity::class.java
                )

                intent.putExtra(
                    "id",
                    tarefa.id
                )

                intent.putExtra(
                    "usuario",
                    usuario
                )

                startActivity(intent)
            },

            // DELETAR
            onDeletar = { tarefa ->

                deletar(tarefa.id)
            },

            // CONCLUIR
            onConcluir = { tarefa ->

                concluirTarefa(tarefa.id)
            }
        )

        recycler.adapter = adapter

        // Spinner
        configurarSpinner()

        // Carrega tarefas
        carregarTarefas()

        // Busca
        edtBusca.addTextChangedListener {

            aplicarFiltros()
        }

        // Evento spinner
        spnStatus.onItemSelectedListener =

            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(

                    parent: AdapterView<*>?,

                    view: View?,

                    position: Int,

                    id: Long
                ) {

                    aplicarFiltros()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // Swipe deletar
        val swipe = ItemTouchHelper(

            object : ItemTouchHelper.SimpleCallback(

                0,

                ItemTouchHelper.LEFT
            ) {

                override fun onMove(

                    recyclerView: RecyclerView,

                    viewHolder: RecyclerView.ViewHolder,

                    target: RecyclerView.ViewHolder

                ): Boolean {

                    return false
                }

                override fun onSwiped(

                    holder: RecyclerView.ViewHolder,

                    dir: Int
                ) {

                    val pos =
                        holder.bindingAdapterPosition

                    if (pos != RecyclerView.NO_POSITION) {

                        val tarefa =
                            listaFiltrada[pos]

                        deletar(tarefa.id)
                    }
                }
            }
        )

        swipe.attachToRecyclerView(recycler)
    }

    // Atualiza ao voltar
    override fun onResume() {

        super.onResume()

        carregarTarefas()
    }

    // Spinner status
    private fun configurarSpinner() {

        val status = arrayOf(

            "Sem filtro",

            "A fazer",

            "Em andamento",

            "Quase concluída"
        )

        spnStatus.adapter =

            ArrayAdapter(

                this,

                android.R.layout.simple_spinner_dropdown_item,

                status
            )
    }

    // Carrega tarefas
    private fun carregarTarefas() {

        lista.clear()

        val cursor = db.readableDatabase.rawQuery(

            """
            SELECT id, titulo, status, prioridade
            FROM tarefas
            WHERE usuario = ?
            AND prioridade = ?
            AND status != 'Concluída'
            """.trimIndent(),

            arrayOf(

                usuario ?: "",

                prioridadeSelecionada ?: ""
            )
        )

        while (cursor.moveToNext()) {

            lista.add(

                Tarefa(

                    cursor.getInt(0),

                    cursor.getString(1),

                    cursor.getString(2),

                    cursor.getString(3)
                )
            )
        }

        cursor.close()

        aplicarFiltros()
    }

    // Busca e filtros
    private fun aplicarFiltros() {

        val texto =
            edtBusca.text.toString()

        val statusFiltro =
            spnStatus.selectedItem.toString()

        listaFiltrada.clear()

        for (t in lista) {

            val matchTexto =

                t.titulo.lowercase()
                    .contains(texto.lowercase())

            val matchStatus =

                statusFiltro == "Sem filtro" ||

                        t.status == statusFiltro

            if (matchTexto && matchStatus) {

                listaFiltrada.add(t)
            }
        }

        adapter.notifyDataSetChanged()
    }

    // Concluir tarefa
    private fun concluirTarefa(id: Int) {

        val valores = ContentValues()

        valores.put(

            "status",

            "Concluída"
        )

        db.writableDatabase.update(

            "tarefas",

            valores,

            "id=?",

            arrayOf(id.toString())
        )

        Toast.makeText(

            this,

            "Tarefa concluída!",

            Toast.LENGTH_SHORT

        ).show()

        carregarTarefas()
    }

    // Deletar
    private fun deletar(id: Int) {

        db.writableDatabase.delete(

            "tarefas",

            "id=?",

            arrayOf(id.toString())
        )

        Toast.makeText(

            this,

            "Tarefa deletada",

            Toast.LENGTH_SHORT

        ).show()

        carregarTarefas()
    }
}