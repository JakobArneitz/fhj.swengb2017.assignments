package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import at.fhj.swengb.apps.battleship.model._

import scala.util.{Failure, Success, Try}

object BattleShipApp {

  val FXML_FIND_SERVER = "find-server.fxml"
  val FXML_GAME = "game.fxml"
  val FXML_MAIN_MENU = "main-menu.fxml"
  val FXML_REGISTER = "register.fxml"
  val FXML_WELCOME = "welcome.fxml"

  var stage: Stage = _

  var game: Game = _

  var player: Player = _

  def load(fxml: String, title: String): Unit = {
    val path = "/at/fhj/swengb/apps/battleship/jfx/fxml/"
    val loaded = Try(FXMLLoader.load[Parent](getClass.getResource(path + fxml)))
    loaded match {
      case Success(root) =>
        stage.setScene(new Scene(root))
        stage.show()
        stage.setTitle(title)
        stage.setResizable(false)
      case Failure(e) => e.printStackTrace()
    }
  }

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[BattleShipApp], args: _*)
  }

}

class BattleShipApp extends Application {

  override def start(stage: Stage): Unit = {
    BattleShipApp.stage = stage
    BattleShipApp.load(BattleShipApp.FXML_WELCOME, "Es wird Zeit!")
  }

}
