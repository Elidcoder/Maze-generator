import kotlin.random.Random

typealias Square = Int
typealias Connection = Pair<Square, Square>

const val startingSquare = 0
const val BRANCHPERCENT = 10

class Maze(val width: Int, val height: Int) {
    val mazeSize = width * height
    var startEnd = Pair(startingSquare, mazeSize)
    var connections = mutableListOf<Connection>()

    init {
        require(width > 0 && height > 0)
        {throw IllegalArgumentException("Maze width and height must be greater than or equal to zero")}
        generateMaze()
    }

    private fun generateMaze() {
        TODO()
    }
}
