package com.example.gestaotarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class PrioridadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_prioridades)

        val usuario =
            intent.getStringExtra("usuario")

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
}