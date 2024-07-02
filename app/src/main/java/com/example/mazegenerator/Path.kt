import java.util.ArrayDeque

class Path(previousPath: List<Square>){

    constructor(startingSquare: Square): this(listOf(startingSquare))

    private val path = ArrayDeque<Square>(previousPath)

    fun currentSquare(): Square =
        if (hasElements())
            path.peek() as Square
        else
            throw ArrayIndexOutOfBoundsException("Tried to peek on empty path")

    fun backtrack(): Square =
        if (hasElements())
            path.pop()
        else
            throw ArrayIndexOutOfBoundsException("Tried to pops on empty path")

    fun addSquare(square: Square) = path.add(square)

    fun hasElements(): Boolean = path.isNotEmpty()

    fun createBranchTo(newSquare: Square): Path {
        val branch = Path(path.toList())
        branch.addSquare(newSquare)
        return branch
    }
}
