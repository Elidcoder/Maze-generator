package com.example.mazegenerator

import kotlin.random.Random

typealias Square = Int
typealias Connection = Pair<Square, Square>

const val startingSquare = 0
const val MAXHEIGHT = 50
const val MAXWIDTH = 25
const val BRANCHPERCENT = 10

fun verifyDimensions(height: Int?, width: Int?): Boolean{
    return height != null && height in 1..MAXHEIGHT && width != null && width in 1.. MAXWIDTH
}

class Maze(val width: Int, val height: Int) {
    private val mazeSize = width * height
    var connections = mutableListOf<Connection>()

    init {
        require(verifyDimensions(height, width))
        {throw IllegalArgumentException("Maze dimensions ($width, $height) are invalid")}
        generateMaze()
    }

    fun getAdjacentSquares(square: Square): MutableList<Square> {
        val availableSquares = mutableListOf<Square>()
        if (square > 0){
            if (square > (width - 1)){
                availableSquares.add(square - width)
            }
            availableSquares.add(square - 1)
        }

        if (square < (mazeSize - 1)){
            if (square < (mazeSize - width)){
                availableSquares.add(square + width)
            }
            availableSquares.add(square + 1)
        }
        return availableSquares
    }

    private fun generateMaze() {
        val visited = mutableSetOf<Square>()
        val branches = mutableListOf(Path(startingSquare))
        var possibleNeighbours: List<Connection>
        var branch: Path
        var targetConnection: Connection

        fun getUnvisitedConnections(currentSquare: Square, visited: Set<Int>): MutableList<Connection> {
            val neighbours = getAdjacentSquares(currentSquare)

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
            if (!branch.hasElements()){
                branches.remove(branch)
                continue
            }
            possibleNeighbours = getUnvisitedConnections(branch.currentSquare(), visited)
            if (possibleNeighbours.isEmpty()) {
                branch.backtrack()
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
