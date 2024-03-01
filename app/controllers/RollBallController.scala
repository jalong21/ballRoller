package controllers

import models.Solution
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.BallRoller

import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class RollBallController @Inject()(cc: ControllerComponents,
                                   player: BallRoller) extends AbstractController(cc) {

  def rollBall(boardSize: Int) = Action {
    request => Try(player.RollBall(boardSize)) match {
          case Success(solution) => Ok(Solution.drawBoard(solution))
          case Failure(exception) => InternalServerError(exception.getMessage)
        }
  }
}