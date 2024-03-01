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
    val destinationPosition = getDestinationPosition(startPosition.position, size)
    val board: Seq[Spot] = generateBoard(size, 20, startPosition, destinationPosition)

    log.warn(s"Board generated. Starting to find solution!")
    // call recursive makeMove function that returns a list of moves with a solution, if there is one.
    val solutionMoves = makeMove(startPosition, destinationPosition, board)

    // create Solution object for printing text response
    Solution(board, solutionMoves, startPosition, destinationPosition, size)
  }

  // this is using tail recursion to avoid stack overflow errors
  // every path must return either a call to itself or the return value in the signature.
  // every new call should be after a change in the currentState: BallState
  @tailrec
  private def makeMove(currentState: BallState, destination: (Int, Int), board: Seq[Spot], previousBallStates: Seq[BallState] = Seq.empty[BallState], unprocessedPaths: Seq[Seq[BallState]] = Seq.empty[Seq[BallState]]): Seq[BallState] = {

    // we will do different things based on our current direction and position
    (currentState.direction, currentState.position) match {
      case (Directions.Stopped, position) if position == destination =>
        log.warn(s"Destination found! previous ball states: ${previousBallStates.size}")
        previousBallStates :+ currentState
      case (direction, _) if direction != Directions.Stopped =>
        log.warn(s"Continuing in current direction. $currentState.")
        val nextState = getNextState(currentState, board)
          .getOrElse(BallState(currentState.position, Directions.Stopped))
        makeMove(nextState, destination, board, previousBallStates :+ currentState, unprocessedPaths)
      case (Directions.Stopped, _) => {
        log.warn(s"Ball is stopped, deciding where to go next")
        val nextStates: Seq[BallState] = getNextPossibleStates(currentState, board)
          .filterNot(possibleState => previousBallStates.contains(possibleState))

        (nextStates.size, unprocessedPaths.size) match {
          case (1, _) =>
            log.warn(s"Ball stopped with only one possible, untried direction. $currentState.")
            makeMove(nextStates.head, destination, board, previousBallStates :+ currentState, unprocessedPaths)
          case (nextStatesCount, _) if nextStatesCount > 1 =>
            log.warn(s"Ball Stopped with ${nextStates.size} possible, untried directions. $currentState.")
            val sortedNextStates = sortNextStatesByDirection(destination, nextStates, currentState)
            val unprocessedBallStates: Seq[Seq[BallState]] = sortedNextStates.tail
              .filterNot(state => unprocessedPaths.exists(path => path.last == state))
              .map(tail => previousBallStates :+ tail)
            makeMove(sortedNextStates.head, destination, board, previousBallStates :+ currentState, unprocessedPaths ++ unprocessedBallStates)
          case (0, unprocessedPathsCount) if unprocessedPathsCount > 0 =>
            log.warn(s"Path failed, but we have previous untried paths. Switching to other path. $currentState.")
            val pastPreviousPath = unprocessedPaths.head
            val postPreviousState = pastPreviousPath.head
            makeMove(postPreviousState, destination, board, pastPreviousPath, unprocessedPaths.tail)
          case _ =>
            log.warn(s"Path failed with no untried paths left, returning empty. $currentState.")
            Seq.empty[BallState]
        }
      }
    }
  }

  private def sortNextStatesByDirection(destination: (Int, Int), nextStates: Seq[BallState], currentState: BallState) = {
    nextStates.sortWith((state1, _) => {
      val distanceToX = Math.abs(destination._1 - currentState.position._1)
      val distanceToY = Math.abs(destination._2 - currentState.position._2)
      val nextStateDistanceToX = Math.abs(destination._1 - state1.position._1)
      val nextStateDistanceToY = Math.abs(destination._2 - state1.position._2)

      log.warn(s"destinationPosition: $destination, currentPosition: ${currentState.position}, checkingPosition: ${state1.position}, direction: ${state1.direction}")

      if ((distanceToY - nextStateDistanceToY > 0) ||
        (distanceToX - nextStateDistanceToX > 0)) {
        log.warn(s"${state1.direction} is towards")
        true
      }
      else {
        log.warn(s"${state1.direction} is away")
        false
      }
    })
  }

  // this uses getNextState to create a list of possible next states if it starts moving
  private def getNextPossibleStates(state: BallState, board: Seq[Spot]): Seq[BallState] =
    Seq(getNextState(BallState(state.position, Directions.Left), board),
      getNextState(BallState(state.position, Directions.Right), board),
      getNextState(BallState(state.position, Directions.Up), board),
      getNextState(BallState(state.position, Directions.Down), board))
      .flatten

  // ensure that the destination position is not the starting position
  private def getDestinationPosition(startingPosition: (Int, Int), size: Int) = {
    var destination = (Random.nextInt(size), Random.nextInt(size))
    while(destination == startingPosition) {
      destination = (Random.nextInt(size), Random.nextInt(size))
    }
    destination
  }

  // generate a Seq of Spots that represent a random board with a start, destination, and walls.
  private def generateBoard(size: Int, wallPercent: Int, startPosition: BallState, destinationPosition: (Int, Int)): Seq[Spot] =
    (0 to size - 1)
      .flatMap(column => (0 to size -1)
        .map(row => {
          val isWall = if (startPosition.position == (row, column) || destinationPosition == (row, column) || Random.nextInt(100) > wallPercent) {
            false
          } else {
            true
          }
          log.warn(s"making spot: X=$row, Y=$column, isWall=$isWall")
          Spot(isWall, (row, column))
        }))

  // this returns the next ball state if it can keep moving or None if the next spot is a wall
  private def getNextState(ballState: BallState, board: Seq[Spot]): Option[BallState] = {
    val nextPositionInDirection = ballState match {
      case BallState(position, Directions.Up) => (position._1, position._2 - 1)
      case BallState(position, Directions.Down) => (position._1, position._2 + 1)
      case BallState(position, Directions.Right) => (position._1 + 1, position._2)
      case BallState(position, Directions.Left) => (position._1 - 1, position._2)
      case BallState(position, Directions.Stopped) => (position._1, position._2)
    }
    board
      .find(spot => spot.position == nextPositionInDirection && !spot.isWall)
      .map(spot => BallState(spot.position, ballState.direction))
  }

}
