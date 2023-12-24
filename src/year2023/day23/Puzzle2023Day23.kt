package year2023.day23

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day23()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day23 : Puzzle<Int, Int>("2023", "23", 94, -1) {
    override fun solvePart1(input: List<String>): Int {
        // Parse the grid from the input strings
        val grid = parseGridFromInput(input)

        // Use the findMax method to find the maximum value in the grid
        return grid.findMax { current ->
            // Determine the next points and costs based on the character in the grid
            when (val char = grid.grid[current.y][current.x]) {
                '>', '<', 'v', '^' -> listOf(current.move(char) to 1)
                else -> {
                    // For any other character, get valid neighbors and their costs
                    current.getNeighbours()
                        .filter { it.isValidPoint(grid) }
                        .map { it to 1 }
                }
            }
        }
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseGridFromInput(input: List<String>): Grid {
    val height = input.size
    val width = input[0].length
    val grid = Array(height) { CharArray(width) }

    for ((y, s) in input.withIndex()) {
        for ((x, c) in s.toCharArray().withIndex()) {
            grid[y][x] = c
        }
    }

    val start = Point2D(input[0].indexOf('.'), 0)
    val end = Point2D(input[height - 1].indexOf('.'), height - 1)
    return Grid(height, width, grid, start, end)
}

private data class Grid(
    val height: Int,
    val width: Int,
    val grid: Array<CharArray>,
    val start: Point2D,
    val end: Point2D
) {
    fun findMax(
        current: Point2D = start,
        visited: Array<BooleanArray> = Array(height) { BooleanArray(width) },
        distance: Int = 0,
        getNeighbours: (Point2D) -> List<Pair<Point2D, Int>>
    ): Int {
        // Return the distance if the end point is reached
        if (current == end) return distance

        // Mark the current point as visited
        visited[current.y][current.x] = true

        // Find the maximum distance among all valid neighbors
        val maxDistance = getNeighbours(current)
            .filterNot { (neighbour, _) -> visited[neighbour.y][neighbour.x] }
            .maxOfOrNull { (neighbour, weight) ->
                findMax(neighbour, visited, distance + weight, getNeighbours)
            } ?: 0

        // Mark the current point as unvisited for other paths
        visited[current.y][current.x] = false

        return maxDistance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid) return false

        if (height != other.height) return false
        if (width != other.width) return false
        if (!grid.contentDeepEquals(other.grid)) return false
        if (start != other.start) return false

        return true
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + grid.contentDeepHashCode()
        result = 31 * result + start.hashCode()
        return result
    }
}

private data class Point2D(val x: Int, val y: Int) {
    fun getNeighbours(): List<Point2D> {
        // Return the list of points around the current point
        return listOf(
            Point2D(x, y - 1), // North
            Point2D(x, y + 1), // South
            Point2D(x - 1, y), // West
            Point2D(x + 1, y) // East
        )
    }

    fun move(direction: Char): Point2D = when (direction) {
        // move in direction of slope, else return self
        '>' -> copy(x = x + 1)
        '<' -> copy(x = x - 1)
        'v' -> copy(y = y + 1)
        '^' -> copy(y = y - 1)
        else -> this
    }

    fun isValidPoint(grid: Grid): Boolean {
        return y in grid.grid.indices && x in grid.grid.first().indices && grid.grid[y][x] in ".<>^v"
    }
}