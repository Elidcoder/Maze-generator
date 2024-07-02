package com.example.mazegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
    val paint = Paint()
    paint.color = Color.Black
    paint.strokeWidth = 2f

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                val cellSize = 50f // Adjust as needed
                val padding = 16f // Adjust padding as needed
                val mazeWidth = generatedMaze.width
                val mazeHeight = generatedMaze.height

                // Draw outer borders of the maze
                canvas.drawRect(
                    left = padding,
                    top = padding,
                    right = padding + mazeWidth * cellSize,
                    bottom = padding + mazeHeight * cellSize,
                    paint = Paint().apply {
                        strokeWidth = 6F
                        color = Color.Black
                        style = PaintingStyle.Stroke
                    }
                )

                // Draw walls where there are no connections
                for (y in 0 until mazeHeight) {
                    for (x in 0 until mazeWidth) {
                        val square = y * mazeWidth + x
                        val rightSquare = square + 1
                        val bottomSquare = square + mazeWidth

                        // Draw right wall if no connection to the right
                        if (x != (mazeWidth - 1)) {
                            if (!(generatedMaze.connections.contains(Connection(square, rightSquare)) || generatedMaze.connections.contains(Connection(rightSquare, square)))) {
                                canvas.drawLine(
                                    p1 = Offset(padding + (x + 1) * cellSize, padding + y * cellSize),
                                    p2 = Offset(padding + (x + 1) * cellSize, padding + (y + 1) * cellSize),
                                    paint = paint,
                                )
                            }
                        }

                        // Draw bottom wall if no connection to the bottom
                        if (y != (mazeHeight - 1)) {
                            if (!(generatedMaze.connections.contains(Connection(square, bottomSquare)) || generatedMaze.connections.contains(Connection(bottomSquare, square)))) {
                                canvas.drawLine(
                                    p1 = Offset(padding + x * cellSize, padding + (y + 1) * cellSize),
                                    p2 = Offset(padding + (x + 1) * cellSize, padding + (y + 1) * cellSize),
                                    paint = paint
                                )
                            }
                        }

                    }
                }
            }
        }

        // Back button
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart)
                .background(Color.Transparent).border(1.dp, Color.Blue)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Back")
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
