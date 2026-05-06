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

        btnLogin.setOnClickListener {

            val nome = edtNome.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (nome.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha tudo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!db.validarUsuario(nome, senha)) {
                db.inserirUsuario(nome, senha)
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("usuario", nome)
            startActivity(intent)

            finish()
        }
    }
}