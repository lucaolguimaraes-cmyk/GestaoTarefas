package com.example.gestaotarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val edtNome = findViewById<EditText>(R.id.edtNome)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnIrCadastro = findViewById<Button>(R.id.btnIrCadastro)

        btnLogin.setOnClickListener {

            val nome = edtNome.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (nome.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.validarUsuario(nome, senha)) {

                Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("usuario", nome)

                val prefs = getSharedPreferences("login", MODE_PRIVATE)

                prefs.edit()
                    .putString("usuario", nome)
                    .apply()

                startActivity(intent)
                finish()

            } else {

                Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
            finish()
        }
    }
}