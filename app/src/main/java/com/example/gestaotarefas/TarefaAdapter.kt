package com.example.gestaotarefas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TarefaAdapter(
    private var lista: List<Tarefa>,
    private val onEditar: (Tarefa) -> Unit,
    private val onDeletar: (Tarefa) -> Unit
) : RecyclerView.Adapter<TarefaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt = view.findViewById<TextView>(R.id.txtTarefa)
        val btnEditar = view.findViewById<ImageButton>(R.id.btnEditar)
        val btnDeletar = view.findViewById<ImageButton>(R.id.btnDeletar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarefa, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = lista[position]

        holder.txt.text = "${t.titulo}\n${t.status} | ${t.prioridade}"

        holder.btnEditar.setOnClickListener {
            onEditar(t)
        }

        holder.btnDeletar.setOnClickListener {
            onDeletar(t)
        }
    }

    fun updateList(novaLista: List<Tarefa>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}