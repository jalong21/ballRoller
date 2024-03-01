package models

import play.api.libs.json.{Json, Writes}

import scala.collection.mutable

object Directions extends Enumeration {
  type Direction = Value

  val Up, Down, Left, Right, Stopped = Value
}

case class BallState(position: (Int, Int), direction: Directions.Direction)

case class Spot(isWall: Boolean, position: (Int, Int))
object Spot {
  implicit val jsonWrites: Writes[Spot] = Json.writes[Spot]
}

case class Game(board: Seq[Spot], startPosition: (Int, Int), destinationPosition: (Int, Int))
object Game {
  implicit val jsonWrites: Writes[Game] = Json.writes[Game]
}

case class Solution(board: Seq[Spot], travelPath: Seq[BallState], startState: BallState, destination: (Int, Int), mapSize: Int)
object Solution {
  implicit val jsonWrites: Writes[Game] = Json.writes[Game]

  def drawBoard(solution: Solution): String = {
    val stringBuilder = new StringBuilder("\n\n Ball Rolling Game")
    stringBuilder.append(s"\n starting position: ${solution.startState.position}")
    stringBuilder.append(s"\n Destination: ${solution.destination}}")

    if (solution.travelPath.isEmpty) {
      stringBuilder.append(s"\n Solution Impossible :(")
    }

    stringBuilder.append(s"\n Map: \n")
    (0 to solution.mapSize)
      .flatMap(row => {
        (0 to solution.mapSize)
          .map(column => {
            val position = (row, column)
            if (position == solution.startState.position){
              stringBuilder.append("| S ")
            } else if (position == solution.destination) {
              stringBuilder.append("| D ")
            } else if (
              solution.travelPath
                .exists(ballState => ballState.position == position)
            ) {
              stringBuilder.append("| * ")
            } else if (solution.board
              .find(ballState => ballState.position == position)
            .map(_.isWall)
            .getOrElse(false)) {
              stringBuilder.append("||||")
            } else {
              stringBuilder.append("|   ")
            }
          })
        stringBuilder.append("|\n")
      })

    solution.travelPath.foreach( ballState => stringBuilder.append(s"\n \n $ballState"))
    stringBuilder.toString()
  }
}
