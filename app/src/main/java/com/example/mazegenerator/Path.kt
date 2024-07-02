import java.util.ArrayDeque

class Path(previousPath: List<Square>){

    constructor(startingSquare: Square): this(listOf(startingSquare))

    private val path = ArrayDeque<Square>(previousPath)

    fun currentSquare(): Square {
        TODO()
    }

    fun backtrack(): Square {
        TODO()
    }

    fun addSquare(square: Square) {
        TODO()
    }

    fun hasElements(): Boolean {
        TODO()
    }

    fun createBranchTo(newSquare: Square): Path {
        TODO()
    }
}
