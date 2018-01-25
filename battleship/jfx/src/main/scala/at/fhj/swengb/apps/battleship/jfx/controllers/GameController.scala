package at.fhj.swengb.apps.battleship.jfx.controllers

import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Slider}
import javafx.scene.layout.{AnchorPane, GridPane, Pane}
import javafx.scene.text.Text

import io.socket.client.{IO, Socket}
import io.socket.emitter.Emitter
import at.fhj.swengb.apps.battleship.BattleShipProtobuf
import at.fhj.swengb.apps.battleship.jfx._
//import at.fhj.swengb.apps.battleship.BattleShipProtocol.convert
import at.fhj.swengb.apps.battleship.model._

class GameController extends Initializable {

  lazy val game = BattleShipApp.game

  lazy val io: Socket = IO.socket("http://localhost:9001")

  @FXML var fieldPane: Pane = _

  @FXML var fieldGridPane: GridPane = _

  @FXML var enemyFieldPane: Pane = _

  @FXML var enemyFieldGridPane: GridPane = _

  @FXML var shipListPane: AnchorPane = _

  @FXML var slider: Slider = _

  @FXML var battleShipPane: Pane = _

  @FXML var battleShipCounter: Text = _

  @FXML var cruiserPane: Pane = _

  @FXML var cruiserCounter: Text = _

  @FXML var destroyerPane: Pane = _

  @FXML var destroyerCounter: Text = _

  @FXML var submarinePane: Pane = _

  @FXML var submarineCounter: Text = _

  @FXML var boatPane: Pane = _

  @FXML var boatCounter: Text = _

  @FXML var playerName: Text = _

  @FXML var enemyPlayerName: Text = _

  @FXML var enemyPlayerInfo: Text = _

  @FXML var enemyPlayerSubInfo: Text = _

  @FXML var finishPlacingBtn: Button = _

  @FXML var randomPlacingBtn: Button = _

  @FXML def newGame(): Unit = init()

  @FXML def loadGame(): Unit = {
//    val path = Paths.get("target/game.state")
//    val stream = Files.newInputStream(path)
//    val field = convert(BattleShipProtobuf.BattleShipGame.parseFrom(stream))
//    val game = Game(field, getCellWidth, getCellHeight, appendLog, slider)
//
//    init(game)
//
//    game.updateSteps(game.battleField.steps.length)
//
//    println("Game loaded")
  }

  @FXML def saveGame(): Unit = {
//    val game = convert(battleShipGame)
//    val path = Paths.get("target/game.state")
//    val stream = Files.newOutputStream(path)
//
//    game.writeTo(stream)
//    println("Game saved")
  }

  @FXML def onMouseDragged(): Unit = {
//    init(game)
//    game.updateSteps(slider.getValue.toInt)
//    println(slider.getValue)
  }

  @FXML def pressedBattleShipPane(): Unit = {
    setPanePressed(battleShipPane)
    game.vesselType = classOf[BattleShip]
  }

  @FXML def pressedCruiserPane(): Unit = {
    setPanePressed(cruiserPane)
    game.vesselType = classOf[Cruiser]
  }

  @FXML def pressedDestroyerPane(): Unit = {
    setPanePressed(destroyerPane)
    game.vesselType = classOf[Destroyer]
  }

  @FXML def pressedSubmarinePane(): Unit = {
    setPanePressed(submarinePane)
    game.vesselType = classOf[Submarine]
  }

  @FXML def pressedBoatPane(): Unit = {
    setPanePressed(boatPane)
    game.vesselType = classOf[Boat]
  }

  @FXML def pressedRandomPlacing(): Unit = {
    val field = Field(10, 10, Fleet(FleetConfig.Standard).withVessels, EditingMode, isLocked = false).withRandomFleetPlacing
    game.setField(field, game.field)
    updateCounters(field)
  }

  @FXML def pressedFinishPlacing(): Unit = {
    val player = BattleShipApp.player

    player.vessels = game.field.fleet.vessels
    player.endFleetPlacing()
    sendPlayerFinishedPlacing()
    game.field.setIsLocked(true)

    finishPlacingBtn.setVisible(false)
    randomPlacingBtn.setVisible(false)
    shipListPane.setVisible(false)
  }

  def updateCounters(field: Field): Unit = {
    def vesselsCount(vesselType: Class[_ <: Vessel]): Int = {
      val count = field.fleet.findByType(vesselType).size
      val allowed = field.fleet.fleetConfig.vesselMap(vesselType)
      allowed - count
    }

    battleShipCounter.setText(s"x ${vesselsCount(classOf[BattleShip])}")
    cruiserCounter.setText(s"x ${vesselsCount(classOf[Cruiser])}")
    destroyerCounter.setText(s"x ${vesselsCount(classOf[Destroyer])}")
    submarineCounter.setText(s"x ${vesselsCount(classOf[Submarine])}")
    boatCounter.setText(s"x ${vesselsCount(classOf[Boat])}")
  }

  def makeShoot(pos: Pos): Unit = {
    fieldPane.getStyleClass.add("playersTurn")
    enemyFieldPane.getStyleClass.removeAll("playersTurn")
    game.enemyField.setIsLocked(true)
    sendPlayerMadeShoot(pos)
  }

  def onGameOver(): Unit = sendPlayerLose()

  override def initialize(url: URL, rb: ResourceBundle): Unit = init()

  private def startClient(): Unit = {
    io
      .on("player connected", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onPlayerConnected(Helper.parsePlayerFromJson(args(0)))
      })
      .on("player finished placing", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onPlayerFinishedPlacing(Helper.parsePlayerFromJson(args(0)))
      })
      .on("player made shoot", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onPlayerMadeShoot(Helper.parsePosFromJson(args(0)))
      })
      .on("player lose", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onPlayerLose()
      })
      .on("please make shoot", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onPleaseMakeShoot()
      })
      .on("info start game", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onInfoStartGame(Helper.parsePlayerFromJson(args(0)))
      })
      .on("info enemy makes shoot", new Emitter.Listener {
        override def call(args: AnyRef*): Unit = onInfoEnemyMakesShoot()
      })
      .on(Socket.EVENT_CONNECT, new Emitter.Listener {
        override def call(args: AnyRef*): Unit = sendPlayerInit()
      })

    io.connect()
  }

  private def onPlayerConnected(player: Player): Unit = {
    val name = player.name.toUpperCase

    enemyPlayerName.setText(name)
    enemyPlayerName.setVisible(true)
    enemyPlayerSubInfo.setVisible(true)

    if (!player.isFleetPlacing) { onPlayerFinishedPlacing(player); return }

    enemyPlayerInfo.setText(s"$name HAS CONNECTED")
    enemyPlayerSubInfo.setText(s"$name IS PLACING FLEET")
  }

  private def onPlayerFinishedPlacing(player: Player): Unit = {
    val name = player.name.toUpperCase

    enemyPlayerInfo.setText(s"$name IS READY TO PLAY")
    enemyPlayerSubInfo.setText("FINISH PLACING YOUR FLEET AND START PLAYING")
  }

  private def onPlayerMadeShoot(pos: Pos): Unit = {
    game.field.shoot(pos)
    fieldPane.getStyleClass.removeAll("playersTurn")
    enemyFieldPane.getStyleClass.add("playersTurn")
    game.enemyField.setIsLocked(false)
  }

  private def onPlayerLose(): Unit = {
      println("GAME OVER MEN GAME OVER")
  }

  private def onPleaseMakeShoot(): Unit = {
    fieldPane.getStyleClass.removeAll("playersTurn")
    enemyFieldPane.getStyleClass.add("playersTurn")

    BattleShipApp.game.enemyField.setIsLocked(false)
  }

  private def onInfoStartGame(player: Player): Unit = {
    enemyPlayerInfo.setVisible(false)
    enemyPlayerSubInfo.setVisible(false)
    enemyFieldGridPane.setVisible(true)

    game.enemyField.setFleet(player.vessels)
    game.field.setIsLocked(true)
  }

  private def onInfoEnemyMakesShoot(): Unit = {
    fieldPane.getStyleClass.add("playersTurn")
    enemyFieldPane.getStyleClass.removeAll("playersTurn")

    BattleShipApp.game.enemyField.setIsLocked(true)
  }

  private def sendPlayerInit(): Unit = {
    val player = Helper.toJson(BattleShipApp.player)
    io.emit("player init", player)
  }

  private def sendPlayerFinishedPlacing(): Unit = {
    val player = Helper.toJson(BattleShipApp.player)
    io.emit("player finished placing", player)
  }

  private def sendPlayerMadeShoot(pos: Pos): Unit = {
    val p = Helper.toJson(pos)
    io.emit("player made shoot", p)
  }

  private def sendPlayerLose(): Unit = io.emit("player lose", "")

  private def init(): Unit = {
    BattleShipApp.game = createGame()
    BattleShipApp.game.init()
    playerName.setText(BattleShipApp.player.name)
    startClient()
  }

  private def createGame(): Game = {
    val field = Field(10, 10, Fleet(FleetConfig.Standard), EditingMode, isLocked = false)
    val enemyField = Field(10, 10, Fleet(FleetConfig.Standard).withVessels, PlayingMode, isLocked = false).withRandomFleetPlacing
    val game = Game(field, enemyField, updateCounters, makeShoot, onGameOver)

    game.slider = slider
    game.fieldGridPane = fieldGridPane
    game.enemyFieldGridPane = enemyFieldGridPane

    game
  }

  private def setPanePressed(pane: Pane): Unit = {
    battleShipPane.getStyleClass.removeAll("buttonPanePressed")
    cruiserPane.getStyleClass.removeAll("buttonPanePressed")
    destroyerPane.getStyleClass.removeAll("buttonPanePressed")
    submarinePane.getStyleClass.removeAll("buttonPanePressed")
    boatPane.getStyleClass.removeAll("buttonPanePressed")
    pane.getStyleClass.add("buttonPanePressed")
  }

}