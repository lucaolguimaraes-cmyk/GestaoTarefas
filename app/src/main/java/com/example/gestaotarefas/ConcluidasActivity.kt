package com.example.gestaotarefas

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConcluidasActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView

    lateinit var adapter: TarefaAdapter

    lateinit var db: DatabaseHelper

    var lista = ArrayList<Tarefa>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_concluidas)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {

            finish()
        }

        recycler =
            findViewById(R.id.recyclerConcluidas)

        recycler.layoutManager =
            LinearLayoutManager(this)

        db = DatabaseHelper(this)

        carregarConcluidas()
    }

    override fun onResume() {

        super.onResume()

        carregarConcluidas()
    }

    private fun carregarConcluidas() {

        lista.clear()

        val usuario =
            intent.getStringExtra("usuario")

        val cursor = db.readableDatabase.rawQuery(

            """
            SELECT id, titulo, status, prioridade
            FROM tarefas
            WHERE usuario = ?
            AND status = ?
            """.trimIndent(),

            arrayOf(
                usuario ?: "",
                "Concluída"
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

        adapter = TarefaAdapter(

            lista,

            onEditar = {},

            onDeletar = {},

            onConcluir = {},

            modoConcluidas = true
        )

        recycler.adapter = adapter
    }
}