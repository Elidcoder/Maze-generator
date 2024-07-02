package com.example.mazegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*

import com.example.mazegenerator.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var height by remember { EmptyTextFieldValue() }
            var width by remember { EmptyTextFieldValue() }
            MazeGeneratorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LargeTitle("Enter maze parameters")
                        Spacer(modifier = Modifier.height(32.dp))
                        NumberInputField(
                            value = height,
                            onValueChange = { height = it },
                            label = "Maze Height"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NumberInputField(
                            value = width,
                            onValueChange = { width = it },
                            label = "Maze Width"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (verifyDimensions(
                                        height.text.toIntOrNull(),
                                        width.text.toIntOrNull()
                                    )
                                ) {/*Go to next page to generate maze*/
                                }
                            },
                            enabled = verifyDimensions(height.text.toIntOrNull(), width.text.toIntOrNull()),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Generate maze")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LargeTitle(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun NumberInputField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(0.5F),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )
}

fun EmptyTextFieldValue():MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
