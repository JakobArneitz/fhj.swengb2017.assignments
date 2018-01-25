package at.fhj.swengb.apps.battleship.model

object FleetConfig {

  val Standard: FleetConfig = FleetConfig(Map(
    classOf[BattleShip] -> 1,
    classOf[Cruiser] -> 1,
    classOf[Destroyer] -> 2,
    classOf[Submarine] -> 2,
    classOf[Boat] -> 3))

}

case class FleetConfig(vesselMap: Map[Class[_ <: Vessel], Int]) {}