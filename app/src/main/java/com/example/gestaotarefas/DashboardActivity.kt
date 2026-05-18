package com.example.gestaotarefas

import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class DashboardActivity : AppCompatActivity() {

    lateinit var pieChart: PieChart

    lateinit var txtHumor: TextView

    lateinit var txtTotal: TextView

    lateinit var db: DatabaseHelper

    var usuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {

            finish()
        }

        pieChart =
            findViewById(R.id.pieChart)

        txtHumor =
            findViewById(R.id.txtHumor)

        txtTotal =
            findViewById(R.id.txtTotal)

        db = DatabaseHelper(this)

        usuario =
            intent.getStringExtra("usuario")

        carregarDashboard()
    }

    override fun onResume() {

        super.onResume()

        carregarDashboard()
    }

    private fun carregarDashboard() {

        val dbRead =
            db.readableDatabase

        val cursor: Cursor = dbRead.rawQuery(

            """
            SELECT status, COUNT(*)
            FROM tarefas
            WHERE usuario = ?
            GROUP BY status
            """.trimIndent(),

            arrayOf(usuario ?: "")
        )

        var fazer = 0
        var andamento = 0
        var quase = 0
        var concluidas = 0

        while (cursor.moveToNext()) {

            val status =
                cursor.getString(0)

            val quantidade =
                cursor.getInt(1)

            when (status) {

                "A fazer" ->
                    fazer = quantidade

                "Em andamento" ->
                    andamento = quantidade

                "Quase concluída" ->
                    quase = quantidade

                "Concluída" ->
                    concluidas = quantidade
            }
        }

        cursor.close()

        val total =
            fazer + andamento + quase + concluidas

        txtTotal.text =
            "$total tarefas no total"

        val entries =
            ArrayList<PieEntry>()

        // Só adiciona se for maior que 0

        if (fazer > 0) {

            entries.add(
                PieEntry(
                    fazer.toFloat(),
                    "A fazer"
                )
            )
        }

        if (andamento > 0) {

            entries.add(
                PieEntry(
                    andamento.toFloat(),
                    "Em andamento"
                )
            )
        }

        if (quase > 0) {

            entries.add(
                PieEntry(
                    quase.toFloat(),
                    "Quase concluída"
                )
            )
        }

        if (concluidas > 0) {

            entries.add(
                PieEntry(
                    concluidas.toFloat(),
                    "Concluídas"
                )
            )
        }

        val dataSet =
            PieDataSet(
                entries,
                ""
            )

        // CORES
        dataSet.colors = listOf(

            Color.rgb(244, 67, 54),     // vermelho
            Color.rgb(255, 152, 0),    // laranja
            Color.rgb(156, 39, 176),   // roxo
            Color.rgb(76, 175, 80)     // verde
        )

        dataSet.valueTextSize = 22f

        dataSet.valueTextColor =
            Color.WHITE

        // REMOVE DECIMAIS
        dataSet.valueFormatter =
            object : ValueFormatter() {

                override fun getPieLabel(
                    value: Float,
                    pieEntry: PieEntry?
                ): String {

                    return value.toInt().toString()
                }
            }

        val data =
            PieData(dataSet)

        pieChart.data = data

        pieChart.description.isEnabled = false

        pieChart.setUsePercentValues(false)

        pieChart.setEntryLabelTextSize(20f)

        pieChart.setEntryLabelColor(Color.WHITE)

        pieChart.centerText =
            "Resumo\nSemanal"

        pieChart.setCenterTextSize(24f)

        pieChart.animateY(1500)

        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.transparentCircleRadius = 0f

        // LEGENDA
        val legend =
            pieChart.legend

        legend.textSize = 18f

        legend.formSize = 16f

        legend.verticalAlignment =
            Legend.LegendVerticalAlignment.BOTTOM

        legend.horizontalAlignment =
            Legend.LegendHorizontalAlignment.CENTER

        legend.orientation =
            Legend.LegendOrientation.VERTICAL

        legend.setDrawInside(false)

        legend.yEntrySpace = 14f

        legend.xOffset = -60f

        legend.yOffset = 10f

        pieChart.invalidate()

        atualizarHumor(

            fazer,

            andamento,

            quase,

            concluidas
        )
    }

    private fun atualizarHumor(

        fazer: Int,

        andamento: Int,

        quase: Int,

        concluidas: Int
    ) {

        val total =
            fazer + andamento + quase + concluidas

        if (total == 0) {

            txtHumor.text =
                "😴 Sem tarefas ainda"

            return
        }

        val produtividade =

            (concluidas * 100) / total

        txtHumor.text = when {

            produtividade >= 80 ->

                "🔥 extrema produção"

            produtividade >= 60 ->

                "😄 Semana muito boa"

            produtividade >= 40 ->

                "🙂 Você está indo bem"

            produtividade >= 20 ->

                "😐 Dá para melhorar"

            else ->

                "😵 Semana complicada"
        }
    }
}