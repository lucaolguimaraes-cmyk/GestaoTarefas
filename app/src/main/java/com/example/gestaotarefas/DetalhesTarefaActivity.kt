package com.example.gestaotarefas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalhesTarefaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalhes_tarefa)

        // COMPONENTES
        val txtTitulo =
            findViewById<TextView>(R.id.txtTituloDetalhe)

        val txtStatus =
            findViewById<TextView>(R.id.txtStatusDetalhe)

        val txtPrioridade =
            findViewById<TextView>(R.id.txtPrioridadeDetalhe)

        val txtDescricao =
            findViewById<TextView>(R.id.txtDescricaoDetalhe)

        val txtData =
            findViewById<TextView>(R.id.txtDataDetalhe)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltarDetalhes)

        // RECEBE DADOS
        val titulo =
            intent.getStringExtra("titulo") ?: ""

        val status =
            intent.getStringExtra("status") ?: ""

        val prioridade =
            intent.getStringExtra("prioridade") ?: ""

        val descricao =
            intent.getStringExtra("descricao") ?: ""

        val data =
            intent.getStringExtra("data") ?: ""

        // DEFINE TEXTOS
        txtTitulo.text =
            titulo

        txtStatus.text =
            "Status: $status"

        txtPrioridade.text =
            "Prioridade: $prioridade"

        txtDescricao.text =
            descricao

        txtData.text =
            "Data: $data"

        // VOLTAR
        btnVoltar.setOnClickListener {

            finish()
        }
    }
}