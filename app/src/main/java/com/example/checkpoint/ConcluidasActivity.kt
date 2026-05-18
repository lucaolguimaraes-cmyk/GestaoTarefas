package com.example.checkpoint

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkpoint.R

class ConcluidasActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView

    lateinit var adapter: TarefaAdapter

    lateinit var db: DatabaseHelper

    lateinit var txtQuantidade: TextView

    var lista = ArrayList<Tarefa>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_concluidas)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        val btnLimpar =
            findViewById<Button>(R.id.btnLimpar)

        btnVoltar.setOnClickListener {

            finish()
        }

        btnLimpar.setOnClickListener {

            confirmarLimpeza()
        }

        recycler =
            findViewById(R.id.recyclerConcluidas)

        txtQuantidade =
            findViewById(R.id.txtQuantidade)

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
            SELECT id,
                   titulo,
                   descricao,
                   data,
                   status,
                   prioridade
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

                    cursor.getString(3),

                    cursor.getString(4),

                    cursor.getString(5)
                )
            )
        }

        cursor.close()

        val quantidade =
            lista.size

        txtQuantidade.text =

            if (quantidade == 1) {

                "1 tarefa concluída"

            } else {

                "$quantidade tarefas concluídas"
            }

        adapter = TarefaAdapter(

            lista,

            onEditar = {},

            onDeletar = {},

            onConcluir = {},

            modoConcluidas = true,

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
    }

    private fun confirmarLimpeza() {

        AlertDialog.Builder(this)

            .setTitle("Limpeza")

            .setMessage(
                "Tem certeza que deseja apagar todas as tarefas concluídas?"
            )

            .setPositiveButton("Sim") { _, _ ->

                limparConcluidas()
            }

            .setNegativeButton("Cancelar", null)

            .show()
    }

    private fun limparConcluidas() {

        val usuario =
            intent.getStringExtra("usuario")

        db.writableDatabase.delete(

            "tarefas",

            "usuario=? AND status=?",

            arrayOf(
                usuario ?: "",
                "Concluída"
            )
        )

        carregarConcluidas()
    }
}