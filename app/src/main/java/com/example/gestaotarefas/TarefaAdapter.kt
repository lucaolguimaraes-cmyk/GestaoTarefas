package com.example.gestaotarefas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TarefaAdapter(

    // Lista
    private var lista: List<Tarefa>,

    // DETALHES
    private val onDetalhes: (Tarefa) -> Unit,

    // Editar
    private val onEditar: (Tarefa) -> Unit,

    // Deletar
    private val onDeletar: (Tarefa) -> Unit,

    // Concluir
    private val onConcluir: (Tarefa) -> Unit,

    // Tela concluídas
    private val modoConcluidas: Boolean = false

) : RecyclerView.Adapter<TarefaAdapter.ViewHolder>() {

    // ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // CARD
        val card =
            view.findViewById<CardView>(R.id.cardTarefa)

        // Texto
        val txt =
            view.findViewById<TextView>(R.id.txtTarefa)

        // Editar
        val btnEditar =
            view.findViewById<ImageButton>(R.id.btnEditar)

        // Deletar
        val btnDeletar =
            view.findViewById<ImageButton>(R.id.btnDeletar)

        // Concluir
        val btnConcluir =
            view.findViewById<Button>(R.id.btnConcluir)
    }

    // Cria item
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)

            .inflate(

                R.layout.item_tarefa,

                parent,

                false
            )

        return ViewHolder(view)
    }

    // Quantidade
    override fun getItemCount() = lista.size

    // Preenche item
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val t = lista[position]

        holder.txt.text =

            "${t.titulo} \n\n${t.status}"

        // DETALHES
        holder.card.setOnClickListener {

            onDetalhes(t)
        }

        // Editar
        holder.btnEditar.setOnClickListener {

            onEditar(t)
        }

        // Deletar
        holder.btnDeletar.setOnClickListener {

            onDeletar(t)
        }

        // Concluir
        holder.btnConcluir.setOnClickListener {

            onConcluir(t)
        }

        // Tela concluídas
        if (modoConcluidas) {

            holder.btnEditar.visibility =
                View.GONE

            holder.btnDeletar.visibility =
                View.GONE

            holder.btnConcluir.visibility =
                View.GONE
        }
    }

    // Atualiza lista
    fun updateList(novaLista: List<Tarefa>) {

        lista = novaLista

        notifyDataSetChanged()
    }
}