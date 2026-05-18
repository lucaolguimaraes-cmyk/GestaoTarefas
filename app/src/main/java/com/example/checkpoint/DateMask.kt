package com.example.checkpoint

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class DateMask(private val editText: EditText) : TextWatcher {

    private var isUpdating = false

    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
    }

    override fun afterTextChanged(s: Editable?) {

        if (isUpdating) return

        isUpdating = true

        val texto =
            s.toString()
                .replace("/", "")

        var formatado = ""

        for (i in texto.indices) {

            formatado += texto[i]

            if ((i == 1 || i == 3) && i != texto.lastIndex) {

                formatado += "/"
            }
        }

        editText.setText(formatado)

        editText.setSelection(formatado.length)

        isUpdating = false
    }
}