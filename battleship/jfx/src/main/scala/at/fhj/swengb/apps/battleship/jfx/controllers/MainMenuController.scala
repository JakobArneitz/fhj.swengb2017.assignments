package at.fhj.swengb.apps.battleship.jfx.controllers

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.layout.{AnchorPane, Pane, VBox}
import javafx.application.Platform

import at.fhj.swengb.apps.battleship.jfx.{BattleShipApp, BattleShipServer}

class MainMenuController extends Initializable {

  @FXML private var menuPane: VBox = _

  @FXML private var loadingPane: AnchorPane = _

  @FXML private var errorPane: Pane = _

  @FXML def pressedStartNewGame(): Unit = startGame()

  @FXML def pressedJoinGame(): Unit = joinGame()

  @FXML def pressedRetry(): Unit = startGame()

  override def initialize(url: URL, rb: ResourceBundle): Unit = {}

  private def startGame(): Unit = {
    menuPane.setVisible(false)
    errorPane.setVisible(false)
    loadingPane.setVisible(true)

    new Thread(new Runnable {
      override def run(): Unit = {
        Thread.sleep(2000)
        startServer()
      }
    }).start()
  }

  private def joinGame(): Unit = BattleShipApp.load(BattleShipApp.FXML_FIND_SERVER, "Es wird Zeit!")

  private def startServer(): Unit = BattleShipServer.start(onServerConnectionSuccess, onServerConnectionError)

  private def onServerConnectionSuccess(): Unit = {
    Platform.runLater(new Runnable() {
      override def run(): Unit = BattleShipApp.load(BattleShipApp.FXML_REGISTER, "Es wird Zeit!")
    })
  }

  private def onServerConnectionError(): Unit = {
    loadingPane.setVisible(false)
    errorPane.setVisible(true)
  }

}
