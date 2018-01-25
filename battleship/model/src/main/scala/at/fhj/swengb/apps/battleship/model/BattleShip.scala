package at.fhj.swengb.apps.battleship.model

object BattleShip {
  val size = 6
}

object Cruiser {
  val size = 5
}

object Destroyer {
  val size = 4
}

object Submarine {
  val size = 3
}

object Boat {
  val size = 2
}

class BattleShip(shipName: String = "BattleShip", pos: Pos, direction: Direction) extends Vessel(NonEmptyString(shipName), pos, direction, BattleShip.size)

class Cruiser(shipName: String = "Cruiser", pos: Pos, direction: Direction) extends Vessel(NonEmptyString(shipName), pos, direction, Cruiser.size)

class Destroyer(shipName: String = "Destroyer", pos: Pos, direction: Direction) extends Vessel(NonEmptyString(shipName), pos, direction, Destroyer.size)

class Submarine(shipName: String = "Submarine", pos: Pos, direction: Direction) extends Vessel(NonEmptyString(shipName), pos, direction, Submarine.size)

class Boat(shipName: String = "Boat", pos: Pos, direction: Direction) extends Vessel(NonEmptyString(shipName), pos, direction, Boat.size)