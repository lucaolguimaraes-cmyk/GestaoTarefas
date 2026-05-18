package com.example.gestaotarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class PrioridadesActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_prioridades)

        db = DatabaseHelper(this)

        val usuario =
            intent.getStringExtra("usuario")

        contarPrioridades(usuario)

        val cardAlta =
            findViewById<CardView>(R.id.cardAlta)

        val cardMedia =
            findViewById<CardView>(R.id.cardMedia)

        val cardBaixa =
            findViewById<CardView>(R.id.cardBaixa)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        cardAlta.setOnClickListener {

            abrirLista(
                "Alta Prioridade",
                usuario
            )
        }

        cardMedia.setOnClickListener {

            abrirLista(
                "Média Prioridade",
                usuario
            )
        }

        cardBaixa.setOnClickListener {

            abrirLista(
                "Baixa Prioridade",
                usuario
            )
        }

        btnVoltar.setOnClickListener {

            finish()
        }
    }

    override fun onResume() {

        super.onResume()

        val usuario =
            intent.getStringExtra("usuario")

        contarPrioridades(usuario)
    }

    private fun abrirLista(
        prioridade: String,
        usuario: String?
    ) {

        val intent =
            Intent(
                this,
                ListaTarefasActivity::class.java
            )

        intent.putExtra(
            "prioridade",
            prioridade
        )

        intent.putExtra(
            "usuario",
            usuario
        )

        startActivity(intent)
    }

    private fun contarPrioridades(
        usuario: String?
    ) {

        val dbRead =
            db.readableDatabase

        fun contar(prioridade: String): Int {

            val cursor = dbRead.rawQuery(

                """
                SELECT COUNT(*)
                FROM tarefas
                WHERE usuario = ?
                AND prioridade = ?
                AND status != ?
                """.trimIndent(),

                arrayOf(
                    usuario ?: "",
                    prioridade,
                    "Concluída"
                )
            )

            var total = 0

            if (cursor.moveToFirst()) {

                total = cursor.getInt(0)
            }

            cursor.close()

            return total
        }

        val alta =
            contar("Alta Prioridade")

        val media =
            contar("Média Prioridade")

        val baixa =
            contar("Baixa Prioridade")

        val totalAtivas =
            alta + media + baixa

        findViewById<TextView>(R.id.txtTotalPrioridades).text =
            "$totalAtivas tarefas ativas"

        findViewById<TextView>(R.id.txtAlta).text =
            "Alta Prioridade ($alta)"

        findViewById<TextView>(R.id.txtMedia).text =
            "Média Prioridade ($media)"

        findViewById<TextView>(R.id.txtBaixa).text =
            "Baixa Prioridade ($baixa)"
    }
}