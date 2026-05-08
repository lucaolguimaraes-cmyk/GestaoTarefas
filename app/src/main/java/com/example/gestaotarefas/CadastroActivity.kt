package com.example.gestaotarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        db = DatabaseHelper(this)

        val edtNome = findViewById<EditText>(R.id.edtNomeCadastro)
        val edtSenha = findViewById<EditText>(R.id.edtSenhaCadastro)

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnJaTenhoLogin = findViewById<Button>(R.id.btnJaTenhoLogin)

        btnCadastrar.setOnClickListener {

            val nome = edtNome.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (nome.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.validarUsuario(nome, senha)) {
                Toast.makeText(this, "Usuário já existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.inserirUsuario(nome, senha)

            Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

        btnJaTenhoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}