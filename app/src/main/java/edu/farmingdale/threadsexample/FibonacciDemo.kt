package edu.farmingdale.threadsexample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FibonacciDemoWithCoroutine(modifier: Modifier = Modifier) {
    var answer by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("40") }
    var isCalculating by remember { mutableStateOf(false) }

    // Get the coroutine scope for this composable
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Number?") },
                singleLine = true,
                enabled = !isCalculating,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Button(
                onClick = {
                    val num = textInput.toLongOrNull() ?: 0
                    isCalculating = true
                    // Launch the calculation in a coroutine
                    scope.launch {
                        // Use withContext to switch to IO dispatcher for CPU-intensive work
                        val fibNumber = withContext(Dispatchers.IO) {
                            fibonacci(num)
                        }
                        // Update the UI with the result
                        answer = NumberFormat.getNumberInstance(Locale.US).format(fibNumber)
                        isCalculating = false
                    }
                },
                enabled = !isCalculating,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Fibonacci")
            }
        }

        if (isCalculating) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Text(
            text = "Result: $answer",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

// Keep the original fibonacci function
fun fibonacci(n: Long): Long {
    return if (n <= 1) n else fibonacci(n - 1) + fibonacci(n - 2)
}