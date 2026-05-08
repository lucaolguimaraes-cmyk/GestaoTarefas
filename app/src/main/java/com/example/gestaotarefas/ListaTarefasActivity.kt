package com.example.gestaotarefas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaTarefasActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var recycler: RecyclerView
    lateinit var adapter: TarefaAdapter

    lateinit var edtBusca: EditText
    lateinit var spnStatus: Spinner
    lateinit var spnPrioridade: Spinner

    var lista = ArrayList<Tarefa>()
    var listaFiltrada = ArrayList<Tarefa>()
    var usuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        usuario = intent.getStringExtra("usuario")


        btnVoltar.setOnClickListener {
            finish()
        }

        db = DatabaseHelper(this)

        recycler = findViewById(R.id.recycler)
        edtBusca = findViewById(R.id.edtBusca)
        spnStatus = findViewById(R.id.spnFiltroStatus)
        spnPrioridade = findViewById(R.id.spnFiltroPrioridade)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = TarefaAdapter(
            listaFiltrada,
            onEditar = { tarefa ->
                val intent = Intent(this, EditarTarefaActivity::class.java)
                intent.putExtra("id", tarefa.id)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            },
            onDeletar = { tarefa ->
                deletar(tarefa.id)
            }
        )
        recycler.adapter = adapter

        configurarSpinners()

        carregarTarefas()

        // 🔍 Busca em tempo real
        edtBusca.addTextChangedListener {
            aplicarFiltros()
        }

        // 🎯 Filtros
        spnStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spnPrioridade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 🧹 Swipe para deletar
        val swipe = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(holder: RecyclerView.ViewHolder, dir: Int) {
                val pos = holder.bindingAdapterPosition

                if (pos != RecyclerView.NO_POSITION) {
                    val tarefa = listaFiltrada[pos]
                    deletar(tarefa.id)
                }
            }
        })

        swipe.attachToRecyclerView(recycler)
    }

    override fun onResume() {
        super.onResume()
        carregarTarefas()
    }

    private fun configurarSpinners() {
        val status = arrayOf("Sem filtro (STATUS)", "Pendente", "Em andamento", "Concluída")
        val prioridade = arrayOf("Sem filtro (PRIORIDADE)", "Baixa", "Média", "Alta")

        spnStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, status)
        spnPrioridade.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prioridade)
    }

    private fun carregarTarefas() {
        lista.clear()

        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, titulo, status, prioridade FROM tarefas WHERE usuario = ?",
            arrayOf(usuario ?: "")
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

    private fun aplicarFiltros() {
        val texto = edtBusca.text.toString()
        val statusFiltro = spnStatus.selectedItem.toString()
        val prioridadeFiltro = spnPrioridade.selectedItem.toString()

        listaFiltrada.clear()

        for (t in lista) {
            val matchTexto = t.titulo.lowercase().contains(texto.lowercase())
            val matchStatus = statusFiltro == "Sem filtro (STATUS)" || t.status == statusFiltro
            val matchPrioridade = prioridadeFiltro == "Sem filtro (PRIORIDADE)" || t.prioridade == prioridadeFiltro

            if (matchTexto && matchStatus && matchPrioridade) {
                listaFiltrada.add(t)
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun deletar(id: Int) {
        db.writableDatabase.delete("tarefas", "id=?", arrayOf(id.toString()))
        Toast.makeText(this, "Tarefa deletada", Toast.LENGTH_SHORT).show()
        carregarTarefas()
    }
}