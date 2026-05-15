package com.example.gestaotarefas

// Importa a classe Intent utilizada para trocar de telas
import android.content.Intent

// Importa Bundle utilizado no ciclo de vida da Activity
import android.os.Bundle

// Importa Button
import android.widget.Button

// Importa EditText para entrada de texto
import android.widget.EditText

// Importa Toast para exibir mensagens rápidas
import android.widget.Toast

// Importa AppCompatActivity
import androidx.appcompat.app.AppCompatActivity

// Activity responsável pela tela de login
class LoginActivity : AppCompatActivity() {

    // Declaração da variável do banco de dados
    // lateinit indica que será inicializada depois
    lateinit var db: DatabaseHelper

    // Método chamado quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {

        // Chama método da classe pai
        super.onCreate(savedInstanceState)

        // Define o layout XML da tela
        setContentView(R.layout.activity_login)

        // Inicializa o banco de dados
        db = DatabaseHelper(this)

        // Liga os campos do XML às variáveis Kotlin
        val edtNome = findViewById<EditText>(R.id.edtNome)

        val edtSenha = findViewById<EditText>(R.id.edtSenha)

        // Referência do botão de login
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Referência do botão para ir ao cadastro
        val btnIrCadastro = findViewById<Button>(R.id.btnIrCadastro)

        // Evento de clique do botão login
        btnLogin.setOnClickListener {

            // Obtém texto digitado no campo nome
            // trim() remove espaços extras
            val nome = edtNome.text.toString().trim()

            // Obtém texto digitado no campo senha
            val senha = edtSenha.text.toString().trim()

            // Verifica se algum campo está vazio
            if (nome.isEmpty() || senha.isEmpty()) {

                // Exibe mensagem de erro
                Toast.makeText(
                    this,
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()

                // Interrompe execução
                return@setOnClickListener
            }

            // Verifica se usuário existe no banco
            if (db.validarUsuario(nome, senha)) {

                // Exibe mensagem de sucesso
                Toast.makeText(
                    this,
                    "Login realizado!",
                    Toast.LENGTH_SHORT
                ).show()

                // Cria Intent para abrir MainActivity
                val intent = Intent(this, CheckpointActivity::class.java)

                // Envia o nome do usuário para próxima tela
                intent.putExtra("usuario", nome)

                // Cria/acessa SharedPreferences chamadas "login"
                // SharedPreferences servem para armazenar dados simples localmente
                val prefs = getSharedPreferences("login", MODE_PRIVATE)

                // Salva usuário logado
                prefs.edit()

                    // Armazena o nome do usuário
                    .putString("usuario", nome)

                    // Aplica alterações
                    .apply()

                // Abre tela principal
                startActivity(intent)

                // Fecha tela de login
                finish()

            } else {

                // Caso usuário ou senha estejam incorretos
                Toast.makeText(
                    this,
                    "Usuário ou senha inválidos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Evento de clique do botão ir para cadastro
        btnIrCadastro.setOnClickListener {

            // Abre tela de cadastro
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )

            // Fecha tela atual
            finish()
        }
    }
}