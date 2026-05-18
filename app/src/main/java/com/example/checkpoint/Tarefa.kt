package com.example.checkpoint

// "data class" é uma classe usada para armazenar dados
// O Kotlin automaticamente cria métodos úteis como:
// toString(), copy(), equals() e hashCode()

data class Tarefa(

    // ID único da tarefa
    // Tipo Int representa números inteiros
    val id: Int,

    // Título da tarefa
    // Tipo String representa texto
    val titulo: String,

    val descricao: String,

    val data: String,

    // Status atual da tarefa
    // Exemplo: "A fazer", "Em andamento", etc.
    val status: String,

    // Prioridade da tarefa
    // Exemplo: "Baixa", "Média" ou "Alta"
    val prioridade: String
)