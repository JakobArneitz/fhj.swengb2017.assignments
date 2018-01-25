package at.fhj.swengb.apps.battleship.model

object Vessel {

  private val vesselSizes = Map(
    classOf[BattleShip] -> BattleShip.size,
    classOf[Cruiser] -> Cruiser.size,
    classOf[Destroyer] -> Destroyer.size,
    classOf[Submarine] -> Submarine.size,
    classOf[Boat] -> Boat.size
  )

  def getTypeBySize(s: Int): Class[_ <: Vessel] = vesselSizes.find(_._2 == s).get._1

  def getSizeByType(t: Class[_ <: Vessel]): Int = vesselSizes.find(_._1 == t).get._2

  def apply(vesselType: Class[_ <: Vessel], name: NonEmptyString, startPos: Pos, direction: Direction): Vessel = vesselType match {
    case x if x == classOf[BattleShip] => new BattleShip(name.value, startPos, direction)
    case x if x == classOf[Cruiser] => new Cruiser(name.value, startPos, direction)
    case x if x == classOf[Destroyer] => new Destroyer(name.value, startPos, direction)
    case x if x == classOf[Submarine] => new Submarine(name.value, startPos, direction)
    case x if x == classOf[Boat] => new Boat(name.value, startPos, direction)
  }

}

case class Vessel(name: NonEmptyString, startPos: Pos, direction: Direction, size: Int) {

  final val occupiedPos: Set[Pos] = direction match {
    case Horizontal => (startPos.x until (startPos.x + size)).map(x => Pos(x, startPos.y)).toSet
    case Vertical => (startPos.y until (startPos.y + size)).map(y => Pos(startPos.x, y)).toSet
  }

}

