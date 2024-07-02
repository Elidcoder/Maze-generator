package com.example.mazegenerator

import kotlin.random.Random

typealias Square = Int
typealias Connection = Pair<Square, Square>

const val startingSquare = 0
const val MAXHEIGHT = 100
const val MAXWIDTH = 100
const val BRANCHPERCENT = 10

fun verifyDimensions(height: Int?, width: Int?): Boolean{
    return height != null && height in 1..MAXHEIGHT && width != null && width in 1.. MAXWIDTH
}

class Maze(private val width: Int, private val height: Int) {
    private val mazeSize = width * height
    private var connections = mutableListOf<Connection>()

    init {
        require(verifyDimensions(height, width))
        {throw IllegalArgumentException("Maze dimensions ($width, $height) are invalid")}
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

            return neighbours.mapNotNull {if (visited.contains(it)) null else Connection(currentSquare, it) } as MutableList<Connection>
        }

        fun visitSquare(targetConnection: Connection, branch: Path) {
            connections.add(targetConnection)
            visited.add(targetConnection.second)
            branch.addSquare(targetConnection.second)
            if (targetConnection.second == mazeSize) {
                branch.backtrack()
            }
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
                    visitSquare(targetConnection, branch)
                }
            }
            targetConnection = possibleNeighbours[Random.nextInt(possibleNeighbours.size)]
            visitSquare(targetConnection, branch)
        }
    }
}
