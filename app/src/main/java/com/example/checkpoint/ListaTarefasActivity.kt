package com.example.checkpoint

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.java

class ListaTarefasActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    lateinit var recycler: RecyclerView

    lateinit var adapter: TarefaAdapter

    lateinit var edtBusca: EditText

    lateinit var spnStatus: Spinner

    lateinit var txtTitulo: TextView

    var lista = ArrayList<Tarefa>()

    var listaFiltrada = ArrayList<Tarefa>()

    var usuario: String? = null

    var prioridadeSelecionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lista)

        db = DatabaseHelper(this)

        recycler = findViewById(R.id.recycler)

        edtBusca = findViewById(R.id.edtBusca)

        spnStatus = findViewById(R.id.spnFiltroStatus)

        txtTitulo = findViewById(R.id.txtTitulo)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        usuario =
            intent.getStringExtra("usuario")

        prioridadeSelecionada =
            intent.getStringExtra("prioridade")

        txtTitulo.text =
            prioridadeSelecionada

        btnVoltar.setOnClickListener {

            finish()
        }

        recycler.layoutManager =
            LinearLayoutManager(this)

        adapter = TarefaAdapter(

            listaFiltrada,

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

            onDeletar = { tarefa ->

                deletar(tarefa.id)
            },

            onConcluir = { tarefa ->

                concluirTarefa(tarefa.id)
            },

            onDetalhes = { tarefa ->

                val intent = Intent(

                    this,

                    DetalhesTarefaActivity::class.java
                )

                intent.putExtra("titulo", tarefa.titulo)

                intent.putExtra("descricao", tarefa.descricao)

                intent.putExtra("data", tarefa.data)

                intent.putExtra("status", tarefa.status)

                intent.putExtra("prioridade", tarefa.prioridade)

                startActivity(intent)
            }
        )

        recycler.adapter = adapter

        configurarSpinner()

        carregarTarefas()

        edtBusca.addTextChangedListener {

            aplicarFiltros()
        }

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

    override fun onResume() {

        super.onResume()

        carregarTarefas()
    }

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

    private fun carregarTarefas() {

        lista.clear()

        val cursor = db.readableDatabase.rawQuery(

            """
            SELECT id,
                   titulo,
                   descricao,
                   data,
                   status,
                   prioridade
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

                    cursor.getString(3),

                    cursor.getString(4),

                    cursor.getString(5)
                )
            )
        }

        cursor.close()

        aplicarFiltros()
    }

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