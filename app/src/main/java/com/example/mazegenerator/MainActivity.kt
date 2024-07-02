package com.example.mazegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.navigation.*
import androidx.navigation.compose.*

import com.example.mazegenerator.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MazeGeneratorTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { DesignMaze(navController) }
                    composable(
                        "mazeScreen/{widthInt}/{heightInt}",
                        arguments = listOf(
                            navArgument("widthInt") { type = NavType.IntType },
                            navArgument("heightInt") { type = NavType.IntType }
                        )

                    ) { backStackEntry ->
                        val height = backStackEntry.arguments?.getInt("heightInt") ?: 0
                        val width = backStackEntry.arguments?.getInt("widthInt") ?: 0
                        MazeScreen(navController, width, height)
                    }
                }
            }
        }
    }
}


@Composable
fun DesignMaze(navController: NavHostController) {

    var height by remember { EmptyTextFieldValue() }
    var width by remember { EmptyTextFieldValue() }
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
                    val heightInt = height.text.toIntOrNull()
                    val widthInt = width.text.toIntOrNull()
                    if (heightInt != null && widthInt != null &&
                        verifyDimensions(
                            heightInt,
                            widthInt
                        )
                    ) {navController.navigate("mazeScreen/$widthInt/$heightInt")
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

@Composable
fun MazeScreen(navController: NavHostController, width: Int, height: Int) {
    val generatedMaze = Maze(width, height)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = 50f // Size of each cell in the maze
            val padding = 16f // Padding around the maze

            // Draw maze grid
            for (connection in generatedMaze.connections) {
                val startX = (connection.first % width) * cellSize + padding + cellSize / 2
                val startY = (connection.first / width) * cellSize + padding + cellSize / 2
                val endX = (connection.second % width) * cellSize + padding + cellSize / 2
                val endY = (connection.second / width) * cellSize + padding + cellSize / 2

                drawLine(
                    color = Color.Black,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
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
fun NumberInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String
) {
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

fun EmptyTextFieldValue(): MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
