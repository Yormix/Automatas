package com.itu.automatas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itu.automatas.ui.theme.AutomatasTheme

// ─────────────────────────────────────────────────────────
//  Definición del autómata  (a*b)
//  Estados: Q0, Q1, Q_TRAP
//  Alfabeto: { 'a', 'b' }
//  Estado inicial: Q0
//  Estado de aceptación: Q1
//
//  Tabla de transiciones:
//         a         b
//  Q0  →  Q0        Q1
//  Q1  →  Q_TRAP    Q_TRAP
//  TRAP→  Q_TRAP    Q_TRAP
// ─────────────────────────────────────────────────────────

enum class Estado { Q0, Q1, Q_TRAP }

fun transicion(estadoActual: Estado, simbolo: Char): Estado {
    return when (estadoActual) {
        Estado.Q0     -> when (simbolo) {
            'a'  -> Estado.Q0
            'b'  -> Estado.Q1
            else -> Estado.Q_TRAP
        }
        Estado.Q1     -> Estado.Q_TRAP
        Estado.Q_TRAP -> Estado.Q_TRAP
    }
}

fun nombreEstado(e: Estado) = when (e) {
    Estado.Q0     -> "q0"
    Estado.Q1     -> "q1"
    Estado.Q_TRAP -> "q_trap"
}

data class ResultadoAutomata(
    val estadoFinal: Estado,
    val aceptada: Boolean,
    val log: String
)

fun evaluar(cadena: String): ResultadoAutomata {
    var q = Estado.Q0
    val sb = StringBuilder()
    sb.appendLine("Estado inicial → q = ${nombreEstado(q)}")
    sb.appendLine()

    for ((i, simbolo) in cadena.withIndex()) {
        val anterior = q
        q = transicion(q, simbolo)
        sb.appendLine("Paso ${i + 1}: δ(${nombreEstado(anterior)}, '$simbolo') → ${nombreEstado(q)}")
    }

    sb.appendLine()
    sb.append("Estado final → q = ${nombreEstado(q)}")

    return ResultadoAutomata(
        estadoFinal = q,
        aceptada    = q == Estado.Q1,
        log         = sb.toString()
    )
}

// ─────────────────────────────────────────────────────────
//  Colores semánticos por estado
// ─────────────────────────────────────────────────────────

fun colorDeEstado(e: Estado) = when (e) {
    Estado.Q0     -> Color(0xFF3949AB)  // índigo  – inicial
    Estado.Q1     -> Color(0xFF2E7D32)  // verde   – aceptación
    Estado.Q_TRAP -> Color(0xFFC62828)  // rojo    – trampa
}

// ─────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutomatasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimuladorAutomata()
                }
            }
        }
    }
}

@Composable
fun SimuladorAutomata() {
    var cadena      by remember { mutableStateOf("") }
    var resultado   by remember { mutableStateOf<ResultadoAutomata?>(null) }
    var errorMsg    by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    fun procesar() {
        errorMsg = ""
        when {
            cadena.isEmpty() -> {
                errorMsg = "⚠ Ingresa una cadena."
                resultado = null
            }
            cadena.any { it != 'a' && it != 'b' } -> {
                errorMsg = "⚠ Solo se permiten los símbolos 'a' y 'b'."
                resultado = null
            }
            else -> resultado = evaluar(cadena)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Título ──────────────────────────────────────
        Text(
            text = "Simulador de Autómata",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Lenguaje reconocido: a*b",
            fontSize = 14.sp,
            color = Color(0xFF5C6BC0)
        )

        Spacer(Modifier.height(28.dp))

        // ── Círculo de estado ────────────────────────────
        val estadoActual = resultado?.estadoFinal ?: Estado.Q0

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(colorDeEstado(estadoActual)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = nombreEstado(estadoActual),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (estadoActual) {
                        Estado.Q0     -> "inicial"
                        Estado.Q1     -> "aceptación"
                        Estado.Q_TRAP -> "trampa"
                    },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Campo de entrada ─────────────────────────────
        Text(
            text = "Ingresa una cadena:",
            fontSize = 14.sp,
            color = Color(0xFF37474F),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = cadena,
            onValueChange = { cadena = it },
            placeholder = { Text("Ej: aaab") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { procesar() }),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        if (errorMsg.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))

        // ── Botones ──────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { procesar() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
            ) {
                Text("▶  Evaluar")
            }
            OutlinedButton(
                onClick = {
                    cadena = ""
                    resultado = null
                    errorMsg = ""
                }
            ) {
                Text("↺  Limpiar")
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Banner resultado ──────────────────────────────
        resultado?.let { r ->
            val bgColor   = if (r.aceptada) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            val txtColor  = if (r.aceptada) Color(0xFF1B5E20) else Color(0xFFB71C1C)
            val mensaje   = if (r.aceptada) "✅  Cadena ACEPTADA" else "❌  Cadena RECHAZADA"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mensaje,
                    color = txtColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(20.dp))
        }

        // ── Log de transiciones ───────────────────────────
        Text(
            text = "Registro de transiciones:",
            fontSize = 13.sp,
            color = Color(0xFF546E7A),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(14.dp)
        ) {
            Text(
                text = resultado?.log ?: "(sin evaluar)",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = Color(0xFF263238),
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}