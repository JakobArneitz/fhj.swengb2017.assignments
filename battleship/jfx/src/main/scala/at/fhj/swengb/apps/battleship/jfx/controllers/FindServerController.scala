package at.fhj.swengb.apps.battleship.jfx.controllers

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TextField

import at.fhj.swengb.apps.battleship.jfx.BattleShipApp
import at.fhj.swengb.apps.battleship.model._

class FindServerController extends Initializable{

  override def initialize(url: URL, rb: ResourceBundle): Unit = {}

  @FXML private var enterNameTextField: TextField = _

  @FXML def pressedConnect(): Unit = {
    BattleShipApp.player = Player(enterNameTextField.getText)
    BattleShipApp.load(BattleShipApp.FXML_GAME, "Es wird Zeit!")
  }

  @FXML def pressedRetry(): Unit = {}

}
