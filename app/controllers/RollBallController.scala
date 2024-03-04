package controllers

import models.Solution
import play.api.mvc._
import services.BallRoller

import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class RollBallController @Inject()(cc: ControllerComponents,
                                   player: BallRoller) extends AbstractController(cc) {

  def rollBall(boardSize: Option[Int], continueToSolution: Option[Boolean], percentWalls: Option[Int]) = Action {
    _ => Try(player.rollBall(boardSize.getOrElse(5), continueToSolution.getOrElse(true), percentWalls.getOrElse(20))) match {
          case Success(solution) => Ok(Solution.drawBoard(solution))
          case Failure(exception) => InternalServerError(exception.getMessage)
        }
  }
}