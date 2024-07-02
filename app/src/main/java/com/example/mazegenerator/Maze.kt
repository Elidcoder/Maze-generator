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
        val visited = mutableSetOf<Square>()
        val branches = mutableListOf(Path(startingSquare))
        var possibleNeighbours: List<Connection>
        var branch: Path
        var targetConnection: Connection

        fun getUnvisitedConnections(currentSquare: Square, visited: Set<Int>): MutableList<Connection> {
            val neighbours = mutableSetOf(currentSquare + 1, currentSquare - 1, currentSquare - width, currentSquare + width)

            return neighbours.mapNotNull {if (visited.contains(it)) null else Connection(currentSquare, it)} as MutableList<Connection>
        }

        fun visitSquare(targetConnection: Connection) {
            connections.add(targetConnection)
            visited.add(targetConnection.second)
        }

        while (visited.size < mazeSize) {
            branch = branches[Random.nextInt(branches.size)]
            possibleNeighbours = getUnvisitedConnections(branch.currentSquare(), visited)
            if (possibleNeighbours.isEmpty()) {
                if (branch.hasElements()){
                    branch.backtrack()
                }
                else{
                    branches.remove(branch)
                }
                continue
            }
            else if (possibleNeighbours.size > 1) {
                if (Random.nextInt(100) < BRANCHPERCENT) {
                    targetConnection = possibleNeighbours[Random.nextInt(possibleNeighbours.size)]
                    possibleNeighbours.remove(targetConnection)
                    branches.add(branch.createBranchTo(targetConnection.second))
                    visitSquare(targetConnection)
                }
            }
            targetConnection = possibleNeighbours[Random.nextInt(possibleNeighbours.size)]
            visitSquare(targetConnection)
        }
    }
}
