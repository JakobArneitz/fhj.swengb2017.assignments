package at.fhj.swengb.apps.battleship.jfx

import at.fhj.swengb.apps.battleship.model._
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import at.fhj.swengb.apps.battleship.BattleShipProtocol._

object PlayerInitEventListener extends DataListener[PlayerObject] {
  override def onData(client: SocketIOClient, data: PlayerObject, ackSender: AckRequest): Unit = {
    BattleShipServer.players.addPlayer(data, client)
    BattleShipServer.players.broadcastPlayersConnected()
  }
}

object PlayerFinishedPlacingListener extends DataListener[PlayerObject] {
  override def onData(client: SocketIOClient, data: PlayerObject, ackSender: AckRequest): Unit = {
    println("finishedPlacingListener")
    BattleShipServer.players.updatePlayer(data, client)
    BattleShipServer.players.broadcastPlayerFinishedPlacing(client)
  }
}

object PlayerMadeShootListener extends DataListener[PosObject] {
  override def onData(client: SocketIOClient, data: PosObject, ackSender: AckRequest): Unit = {
    BattleShipServer.players.broadcastPlayerMadeShoot(client, data)
  }
}

object PlayerLoseListener extends DataListener[String] {
  override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    BattleShipServer.players.broadcastPlayerLosePlacing(client)
    BattleShipServer.players.saveScore()
  }
}

object BattleShipServer {

  var players: BattleShipServerPlayers = BattleShipServerPlayers()

  lazy val io: SocketIOServer = {
    val config = new Configuration

    config.setHostname("localhost")
    config.setPort(9001)

    new SocketIOServer(config)
  }

  def start(successHandler: () => Unit, errorHandler: () => Unit): Unit = {
    initSocket()
    try { io.start(); successHandler() }
    catch { case _: Exception => errorHandler() }
  }

  def stop(): Unit = io.stop()

  private def initSocket(): Unit = {
    io.addEventListener("player init", classOf[PlayerObject], PlayerInitEventListener)
    io.addEventListener("player finished placing", classOf[PlayerObject], PlayerFinishedPlacingListener)
    io.addEventListener("player made shoot", classOf[PosObject], PlayerMadeShootListener)
    io.addEventListener("player lose", classOf[String], PlayerLoseListener)
  }

}

case class BattleShipServerPlayers() {

  private var playerOne: Option[BattleShipServerPlayer] = None
  private var playerTwo: Option[BattleShipServerPlayer] = None

  def getPlayerOne: Option[BattleShipServerPlayer] = playerOne

  def getPlayerTwo: Option[BattleShipServerPlayer] = playerTwo

  def addPlayer(playerObject: PlayerObject, client: SocketIOClient): Unit = {
    val player = Helper.parsePlayerFromPlayerObject(playerObject)
    val bsPlayer = BattleShipServerPlayer(player, client)
    if (playerOne.isEmpty)
      playerOne = Some(bsPlayer)
    else
      playerTwo = Some(bsPlayer)
  }

  def updatePlayer(playerObject: PlayerObject, client: SocketIOClient): Unit = {
    val player = Helper.parsePlayerFromPlayerObject(playerObject)
    println(player.vessels)
    val bsPlayer = BattleShipServerPlayer(player, client)
    client match {
      case c if playerOne.isDefined && (playerOne.get.socket eq c) => playerOne = Some(bsPlayer)
      case c if playerTwo.isDefined && (playerTwo.get.socket eq c) => playerTwo = Some(bsPlayer)
      case _ => None
    }
  }

  def saveScore(): Unit = {
    val vessels = playerOne.get.info.vessels.toSet
    val enemyVessels = playerTwo.get.info.vessels.toSet
    val field = Field(10, 10, Fleet(vessels, FleetConfig.Standard), EditingMode, isLocked = true)
    val enemyField = Field(10, 10, Fleet(enemyVessels, FleetConfig.Standard), PlayingMode, isLocked = true)
    val game = Game(field, enemyField, (_) => Unit, (_) => Unit, () => Unit)
    val protobuf = convert(game)



  }

  def findPlayer(client: SocketIOClient): Option[BattleShipServerPlayer] = List(playerOne, playerTwo).find { p => p.isDefined && (p.get.socket eq client) }.flatten

  def findEnemy(client: SocketIOClient): Option[BattleShipServerPlayer] = client match {
    case c if playerOne.isDefined && (playerOne.get.socket eq c) => playerTwo
    case c if playerTwo.isDefined && (playerTwo.get.socket eq c) => playerOne
    case _ => None
  }

  def findEnemy(player: Player): Option[BattleShipServerPlayer] = player match {
    case p if playerOne.isDefined && playerOne.get.info.eq(p) => playerTwo
    case p if playerTwo.isDefined && playerTwo.get.info.eq(p) => playerOne
    case _ => None
  }

  def broadcastPlayersConnected(): Unit = if (playerOne.isDefined && playerTwo.isDefined) {
    playerOne.get.socket.sendEvent("player connected", Helper.toPlayerObject(playerTwo.get.info))
    playerTwo.get.socket.sendEvent("player connected", Helper.toPlayerObject(playerOne.get.info))
  }

  def broadcastPlayerFinishedPlacing(client: SocketIOClient): Unit = {
    val player = findPlayer(client)
    val enemyPlayer = findEnemy(client)

    if (player.isDefined) { player.get.info.endFleetPlacing() }
    if (player.isEmpty || enemyPlayer.isEmpty) { return }

    enemyPlayer.get.socket.sendEvent("player finished placing", Helper.toPlayerObject(player.get.info))

    if (!enemyPlayer.get.info.isFleetPlacing) {
      player.get.socket.sendEvent("info start game", Helper.toPlayerObject(enemyPlayer.get.info))
      enemyPlayer.get.socket.sendEvent("info start game", Helper.toPlayerObject(player.get.info))
      player.get.socket.sendEvent("info enemy makes shoot")
      enemyPlayer.get.socket.sendEvent("please make shoot")
    }
  }

  def broadcastPlayerMadeShoot(client: SocketIOClient, pos: PosObject): Unit = findEnemy(client).get.socket.sendEvent("player made shoot", pos)

  def broadcastPlayerLosePlacing(client: SocketIOClient): Unit = {
    val enemyPlayer = findEnemy(client)

    enemyPlayer.get.socket.sendEvent("player lose")
  }

}

case class BattleShipServerPlayer(info: Player, socket: SocketIOClient)