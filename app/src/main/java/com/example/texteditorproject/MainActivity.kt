package com.example.texteditorproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editor: EditText
    private lateinit var txtStats: TextView
    private val undoStack = Stack<String>()
    private val redoStack = Stack<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editor = findViewById(R.id.editor)
        txtStats = findViewById(R.id.txtStats)

        val btnNew: Button = findViewById(R.id.btnNew)
        val btnUndo: Button = findViewById(R.id.btnUndo)
        val btnRedo: Button = findViewById(R.id.btnRedo)

        editor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateStats()
                if (!undoStack.isEmpty() && undoStack.peek() == s.toString()) return
                undoStack.push(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnNew.setOnClickListener {
            editor.setText("")
            undoStack.clear()
            redoStack.clear()
        }

        btnUndo.setOnClickListener {
            if (undoStack.isNotEmpty()) {
                redoStack.push(editor.text.toString())
                val last = undoStack.pop()
                editor.setText(last)
                editor.setSelection(last.length)
            }
        }

        btnRedo.setOnClickListener {
            if (redoStack.isNotEmpty()) {
                val redo = redoStack.pop()
                undoStack.push(editor.text.toString())
                editor.setText(redo)
                editor.setSelection(redo.length)
            }
        }

        updateStats()
    }

    private fun updateStats() {
        val text = editor.text.toString()
        val words = text.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        val chars = text.length
        txtStats.text = "Words: $words  Characters: $chars"
    }
}
