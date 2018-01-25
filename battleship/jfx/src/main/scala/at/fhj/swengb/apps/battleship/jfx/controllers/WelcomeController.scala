package at.fhj.swengb.apps.battleship.jfx.controllers

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}

import at.fhj.swengb.apps.battleship.jfx.BattleShipApp

class WelcomeController extends Initializable{

  override def initialize(url: URL, rb: ResourceBundle): Unit = {}

  @FXML def pressedStart(): Unit = BattleShipApp.load(BattleShipApp.FXML_MAIN_MENU, "Es wird Zeit!")

}
