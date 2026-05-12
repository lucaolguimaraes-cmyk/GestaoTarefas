package com.example.gestaotarefas

// Importa Intent
import android.content.Intent

// Importa LayoutInflater
// Responsável por transformar arquivos XML em Views reais
import android.view.LayoutInflater

// Importa View
import android.view.View

// Importa ViewGroup
import android.view.ViewGroup

// Importa ImageButton
import android.widget.ImageButton

// Importa TextView
import android.widget.TextView

// Importa RecyclerView
import androidx.recyclerview.widget.RecyclerView

// Adapter responsável por exibir tarefas dentro do RecyclerView
class TarefaAdapter(

    // Lista de tarefas que será exibida
    private var lista: List<Tarefa>,

    // Função executada ao clicar em editar
    private val onEditar: (Tarefa) -> Unit,

    // Função executada ao clicar em deletar
    private val onDeletar: (Tarefa) -> Unit

) : RecyclerView.Adapter<TarefaAdapter.ViewHolder>() {

    // Classe ViewHolder
    // Responsável por armazenar referências dos componentes do item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // TextView onde será exibido o texto da tarefa
        val txt = view.findViewById<TextView>(R.id.txtTarefa)

        // Botão de editar
        val btnEditar =
            view.findViewById<ImageButton>(R.id.btnEditar)

        // Botão de deletar
        val btnDeletar =
            view.findViewById<ImageButton>(R.id.btnDeletar)
    }

    // Método responsável por criar os itens do RecyclerView
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        // Infla o layout XML item_tarefa
        // transformando ele em uma View real
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_tarefa,
                parent,
                false
            )

        // Retorna ViewHolder contendo a View criada
        return ViewHolder(view)
    }

    // Retorna quantidade de itens da lista
    override fun getItemCount() = lista.size

    // Método responsável por preencher cada item do RecyclerView
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        // Obtém tarefa da posição atual
        val t = lista[position]

        // Define texto exibido no TextView
        // \n quebra linha
        holder.txt.text =
            "${t.titulo}\n${t.status} | ${t.prioridade}"

        // Evento de clique do botão editar
        holder.btnEditar.setOnClickListener {

            // Chama função onEditar enviando tarefa
            onEditar(t)
        }

        // Evento de clique do botão deletar
        holder.btnDeletar.setOnClickListener {

            // Chama função onDeletar enviando tarefa
            onDeletar(t)
        }
    }

    // Função responsável por atualizar a lista do RecyclerView
    fun updateList(novaLista: List<Tarefa>) {

        // Substitui lista antiga pela nova
        lista = novaLista

        // Atualiza RecyclerView inteiro
        notifyDataSetChanged()
    }
}