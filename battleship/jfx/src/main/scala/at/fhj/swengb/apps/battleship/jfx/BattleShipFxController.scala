package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.{Parent, Scene}
import javafx.scene.control.{Slider, TextArea}
import javafx.scene.layout.GridPane
import javafx.stage.Stage

import at.fhj.swengb.apps.battleship.BattleShipProtocol.convert
import at.fhj.swengb.apps.battleship.BattleShipProtobuf
import at.fhj.swengb.apps.battleship.model.{BattleField, BattleShipGame, Fleet, FleetConfig}

import scala.util.{Failure, Success, Try}

class BattleShipFxController extends Initializable {






  var battleShipGame: BattleShipGame = _

  @FXML private var battleGroundGridPane: GridPane = _

  /**
    * A text area box to place the history of the game
    */
  @FXML private var log: TextArea = _

  /**
    * A Slider to place steps of the game
    */
  @FXML private var slider: Slider = _

  @FXML def newGame(): Unit = initGame()

  @FXML def loadGame(): Unit = {
    val path = Paths.get("target/game.state")
    val stream = Files.newInputStream(path)
    val field = convert(BattleShipProtobuf.BattleShipGame.parseFrom(stream))
    val game = BattleShipGame(field, getCellWidth, getCellHeight, appendLog, slider)

    init(game)

    game.updateSteps(game.battleField.steps.length)

    println("Game loaded")
  }

  @FXML def saveGame(): Unit = {
    val game = convert(battleShipGame)
    val path = Paths.get("target/game.state")
    val stream = Files.newOutputStream(path)

    game.writeTo(stream)
    println("Game saved")
  }

  @FXML def onMouseDragged(): Unit = {
    init(battleShipGame)
    battleShipGame.updateSteps(slider.getValue.toInt)
    println(slider.getValue)
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = initGame()

  private def getCellHeight(y: Int): Double = battleGroundGridPane.getRowConstraints.get(y).getPrefHeight

  private def getCellWidth(x: Int): Double = battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  def appendLog(message: String): Unit = log.appendText(message + "\n")

  /**
    * Create a new game.
    *
    * This means
    *
    * - resetting all cells to 'empty' state
    * - placing your ships at random on the battleground
    *
    */
  def init(game : BattleShipGame) : Unit = {
    battleShipGame = game
    battleGroundGridPane.getChildren.clear()
    for (c <- game.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
    }
    game.getCells().foreach(c => c.init)
  }


  private def initGame(): Unit = {
    val game: BattleShipGame = createGame()
    init(game)
    // appendLog("NEW GAME")
  }

  private def createGame(): BattleShipGame = {
    val field = BattleField(10, 10, Fleet(FleetConfig.Standard))

    val battleField: BattleField = BattleField.placeRandomly(field)

    BattleShipGame(battleField, getCellWidth, getCellHeight, appendLog, slider)
  }

}