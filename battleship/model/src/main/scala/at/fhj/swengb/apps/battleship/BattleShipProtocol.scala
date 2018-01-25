package at.fhj.swengb.apps.battleship

import scala.collection.JavaConverters._
import at.fhj.swengb.apps.battleship.model._

object BattleShipProtocol {

  def convert(g: List[Game]): BattleShipProtobuf.Highscore = {
    val highscore = BattleShipProtobuf.Highscore.newBuilder()
    val games = g.map(convert).asJava

    highscore.addAllGames(games)
    highscore.build()
  }

  def convert(g: Game): BattleShipProtobuf.Game = {
    val game = BattleShipProtobuf.Game.newBuilder()

    game.setField(convert(g.field))
    game.setEnemyField(convert(g.enemyField))
    game.build()
  }

  def convert(f: Field): BattleShipProtobuf.Field = {
    val field = BattleShipProtobuf.Field.newBuilder()
    val vessels = f.fleet.vessels.map(convert).asJava
    val shoots = f.shoots.map(convert).asJava

    field.setWidth(f.width)
    field.setHeight(f.height)
    field.addAllVessels(vessels)
    field.addAllShoots(shoots)
    field.build()
  }

  def convert(v: Vessel): BattleShipProtobuf.Vessel = {
    val vessel = BattleShipProtobuf.Vessel.newBuilder()
    vessel.setName(v.name.value)
    vessel.setStartPos(convert(v.startPos))
    vessel.setDirection(convert(v.direction))
    vessel.setSize(v.size)
    vessel.build()
  }

  def convert(p: Pos): BattleShipProtobuf.Pos = {
    val pos = BattleShipProtobuf.Pos.newBuilder()
    pos.setX(p.x)
    pos.setY(p.y)
    pos.build()
  }

  def convert(d: Direction): BattleShipProtobuf.Vessel.Direction = d match {
    case Horizontal => BattleShipProtobuf.Vessel.Direction.HORIZONTAL
    case Vertical => BattleShipProtobuf.Vessel.Direction.VERTICAL
  }

  // CHANGING DIRECTION

  def convert(h: BattleShipProtobuf.Highscore): List[Game] = h.getGamesList.asScala.map(convert).toList

  def convert(g: BattleShipProtobuf.Game): Game = {
    val field = convert(g.getField)
    val enemyField = convert(g.getEnemyField)

    Game(field, enemyField, (_) => Unit, (_) => Unit, () => Unit)
  }

  def convert(f: BattleShipProtobuf.Field): Field = {
    val shoots = f.getShootsList.asScala.map(convert)
    val vessels = f.getVesselsList.asScala.map(convert).toSet
    val fleet = Fleet(vessels, FleetConfig.Standard)

    Field(f.getWidth, f.getHeight, fleet, PlayingMode, isLocked = false)
  }

  def convert(v: BattleShipProtobuf.Vessel): Vessel = {
    val name = NonEmptyString(v.getName)
    val startPos = convert(v.getStartPos)
    val direction = convert(v.getDirection)

    Vessel(name, startPos, direction, v.getSize)
  }

  def convert(p: BattleShipProtobuf.Pos): Pos = Pos(p.getX, p.getY)

  def convert(d: BattleShipProtobuf.Vessel.Direction): Direction = d match {
    case BattleShipProtobuf.Vessel.Direction.HORIZONTAL => Horizontal
    case BattleShipProtobuf.Vessel.Direction.VERTICAL => Vertical
  }

}
