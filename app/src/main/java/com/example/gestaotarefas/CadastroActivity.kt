package com.example.gestaotarefas

// Importa a classe Intent, usada para trocar de tela (Activity)
import android.content.Intent

// Importa a classe Bundle, utilizada no ciclo de vida da Activity
import android.os.Bundle

// Importa o componente Button
import android.widget.Button

// Importa o componente EditText para entrada de texto
import android.widget.EditText

// Importa o Toast, utilizado para mostrar mensagens rápidas na tela
import android.widget.Toast

// Importa a classe base AppCompatActivity
import androidx.appcompat.app.AppCompatActivity

// Criação da Activity de cadastro
class CadastroActivity : AppCompatActivity() {

    // Declaração da variável do banco de dados
    // lateinit significa que ela será inicializada depois
    lateinit var db: DatabaseHelper

    // Método chamado quando a tela é criada
    override fun onCreate(savedInstanceState: Bundle?) {

        // Chama o método da classe pai
        super.onCreate(savedInstanceState)

        // Define qual layout XML será exibido nesta tela
        setContentView(R.layout.activity_cadastro)

        // Inicializa o helper do banco de dados
        db = DatabaseHelper(this)

        // Conecta as variáveis Kotlin com os componentes do XML
        val edtNome = findViewById<EditText>(R.id.edtNomeCadastro)
        val edtSenha = findViewById<EditText>(R.id.edtSenhaCadastro)

        // Referência do botão de cadastrar
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)

        // Referência do botão "Já tenho login"
        val btnJaTenhoLogin = findViewById<Button>(R.id.btnJaTenhoLogin)

        // Evento de clique do botão cadastrar
        btnCadastrar.setOnClickListener {

            // Pega o texto digitado no campo nome
            // toString() transforma em String
            // trim() remove espaços extras
            val nome = edtNome.text.toString().trim()

            // Pega o texto digitado no campo senha
            val senha = edtSenha.text.toString().trim()

            // Verifica se algum campo está vazio
            if (nome.isEmpty() || senha.isEmpty()) {

                // Exibe mensagem na tela
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()

                // Interrompe a execução do clique
                return@setOnClickListener
            }

            // Verifica se o usuário já existe no banco
            if (db.validarUsuario(nome, senha)) {

                // Mostra mensagem informando que o usuário já existe
                Toast.makeText(this, "Usuário já existe", Toast.LENGTH_SHORT).show()

                // Interrompe a execução
                return@setOnClickListener
            }

            // Insere o novo usuário no banco de dados
            db.inserirUsuario(nome, senha)

            // Mostra mensagem de sucesso
            Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show()

            // Cria uma intenção para abrir a tela de login
            val intent = Intent(this, LoginActivity::class.java)

            // Inicia a Activity de login
            startActivity(intent)

            // Finaliza a tela atual para impedir voltar nela pelo botão voltar
            finish()
        }

        // Evento de clique do botão "Já tenho login"
        btnJaTenhoLogin.setOnClickListener {

            // Abre a tela de login
            startActivity(Intent(this, LoginActivity::class.java))

            // Fecha a tela atual
            finish()
        }
    }
}