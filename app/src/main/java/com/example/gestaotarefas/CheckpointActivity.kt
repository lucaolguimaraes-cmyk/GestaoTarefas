package com.example.gestaotarefas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CheckpointActivity : AppCompatActivity() {

    // SharedPreferences
    lateinit var prefs: SharedPreferences

    // Usuário
    var usuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_checkpoint)

        // SharedPreferences
        prefs = getSharedPreferences(
            "login",
            MODE_PRIVATE
        )

        // Usuário recebido
        usuario =
            intent.getStringExtra("usuario")

        // Caso não venha pelo intent
        if (usuario.isNullOrEmpty()) {

            usuario =
                prefs.getString(
                    "usuario",
                    ""
                ) ?: ""
        }

        // Primeiro nome
        val primeiroNome =

            usuario?.split(" ")
                ?.firstOrNull()

                ?: "Usuário"

        // Componentes
        val txtOla =
            findViewById<TextView>(R.id.txtOla)

        val btnAdicionar =
            findViewById<Button>(R.id.btnAdicionar)

        val btnVer =
            findViewById<Button>(R.id.btnVer)

        val btnConcluidas =
            findViewById<Button>(R.id.btnConcluidas)

        val btnDashboard =
            findViewById<Button>(R.id.btnDashboard)

        val btnLogout =
            findViewById<Button>(R.id.btnLogout)

        // Saudação
        txtOla.text =
            "Olá, $primeiroNome"

        // ADICIONAR TAREFA
        btnAdicionar.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.putExtra(
                "usuario",
                usuario
            )

            startActivity(intent)
        }

        // VER TAREFAS
        btnVer.setOnClickListener {

            val intent =
                Intent(
                    this,
                    PrioridadesActivity::class.java
                )

            intent.putExtra(
                "usuario",
                usuario
            )

            startActivity(intent)
        }

        // VER CONCLUÍDAS
        btnConcluidas.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ConcluidasActivity::class.java
                )

            intent.putExtra(
                "usuario",
                usuario
            )

            startActivity(intent)
        }

        // DASHBOARD
        btnDashboard.setOnClickListener {

            val intent =
                Intent(this, DashboardActivity::class.java)

            intent.putExtra(
                "usuario",
                usuario
            )

            startActivity(intent)
        }

        // LOGOUT
        btnLogout.setOnClickListener {

            prefs.edit()
                .clear()
                .apply()

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            startActivity(intent)

            finish()
        }
    }
}