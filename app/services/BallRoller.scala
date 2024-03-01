package services

import akka.stream.Materializer
import models.{BallState, Directions, Solution, Spot}
import play.api.Logger

import javax.inject.Inject
import scala.annotation.tailrec
import scala.util.Random

class BallRoller @Inject()(implicit val materializer: Materializer) {

  val log = Logger(this.getClass.getName)

  def rollBall(size: Int): Solution = {

    // generate random start and stop positions and create a random board
    val startPosition = BallState((Random.nextInt(size), Random.nextInt(size)), Directions.Stopped)
    val destinationPosition = (Random.nextInt(size), Random.nextInt(size))
    val board: Seq[Spot] = generateBoard(size, 20, startPosition, destinationPosition)

    // call recursive makeMove function that returns a list of moves with a solution, if there is one.
    val solutionMoves = makeMove(startPosition, destinationPosition, board)

    // create Solution object for printing text response
    Solution(board, solutionMoves, startPosition, destinationPosition, size)
  }

  @tailrec
  private def makeMove(currentState: BallState, destination: (Int, Int), board: Seq[Spot], previousBallStates: Seq[BallState] = Seq.empty[BallState], unprocessedPaths: Seq[Seq[BallState]] = Seq.empty[Seq[BallState]]): Seq[BallState] = {

    if (currentState.direction == Directions.Stopped && currentState.position == destination) {
      log.warn(s"Destination found! previous ball states: ${previousBallStates.size}")
      previousBallStates :+ currentState
    } else if( currentState.direction == Directions.Stopped) {

      val nextStates: Seq[BallState] = getNextPossibleStates(currentState, board)
        .filterNot(possibleState => previousBallStates.contains(possibleState))

      if (nextStates.size == 1) {
        log.warn(s"Ball stopped with only one possible, untried direction. $currentState.")
        makeMove(nextStates.head, destination, board, previousBallStates :+ currentState, unprocessedPaths)
      }
      else if (nextStates.size > 1) {
        log.warn(s"Ball Stopped with Multiple possible, untried directions. $currentState.")
        val unprocessedBallStates: Seq[Seq[BallState]] = nextStates.tail
          .filterNot(state => unprocessedPaths.exists(path => path.last == state))
          .map(tail => previousBallStates :+ tail )
        makeMove(nextStates.head, destination, board, previousBallStates :+ currentState, unprocessedPaths ++ unprocessedBallStates)
      }
      else if (nextStates.size == 0 && unprocessedPaths.size > 0) {
        log.warn(s"Path failed, but we have previous untried paths. Switching to other path. $currentState.")
        val pastPreviousPath = unprocessedPaths.head
        val postPreviousState = pastPreviousPath.head
        makeMove(postPreviousState, destination, board, pastPreviousPath, unprocessedPaths.tail)
      }
      else {
        log.warn(s"Path failed with no untried paths left, returning empty. $currentState.")
        Seq.empty[BallState]
      }
    } else {
      log.warn(s"Continuing in current direction. $currentState.")
      val nextState = getNextState(currentState, board)
        .getOrElse(BallState(currentState.position, Directions.Stopped))
      makeMove(nextState, destination, board, previousBallStates :+ currentState, unprocessedPaths)
    }
  }

  // this uses getNextState to create a list of possible next states if it starts moving
  private def getNextPossibleStates(state: BallState, board: Seq[Spot]): Seq[BallState] =
    Seq(getNextState(BallState(state.position, Directions.Left), board),
      getNextState(BallState(state.position, Directions.Right), board),
      getNextState(BallState(state.position, Directions.Up), board),
      getNextState(BallState(state.position, Directions.Down), board))
      .flatten

  // generate a Seq of Spots that represent a random board with a start, destination, and walls.
  private def generateBoard(size: Int, wallPercent: Int, startPosition: BallState, destinationPosition: (Int, Int)): Seq[Spot] =
    (0 to size)
      .flatMap(row => (0 to size)
        .map(column => {
          val isWall = if (startPosition.position == (row, column) || destinationPosition == (row, column) || Random.nextInt(100) > wallPercent) {
            false
          } else {
            true
          }
          Spot(isWall, (row, column))
        }))

  // this returns the next ball state if it can keep moving or None if the next spot is a wall
  private def getNextState(ballState: BallState, board: Seq[Spot]): Option[BallState] = {
    val nextPositionInDirection = ballState match {
      case BallState(position, Directions.Up) => (position._1 - 1, position._2)
      case BallState(position, Directions.Down) => (position._1 + 1, position._2)
      case BallState(position, Directions.Right) => (position._1, position._2 + 1)
      case BallState(position, Directions.Left) => (position._1, position._2 - 1)
      case BallState(position, Directions.Stopped) => (position._1, position._2)
    }
    board
      .find(spot => spot.position == nextPositionInDirection && !spot.isWall)
      .map(spot => BallState(spot.position, ballState.direction))
  }

}
