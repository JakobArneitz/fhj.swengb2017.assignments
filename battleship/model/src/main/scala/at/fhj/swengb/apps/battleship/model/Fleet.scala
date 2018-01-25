package at.fhj.swengb.apps.battleship.model

import scala.util.Random

object Fleet {

  def apply(fleetConfig: FleetConfig): Fleet = Fleet(Set[Vessel](), fleetConfig)

}

case class Fleet(vessels: Set[Vessel], fleetConfig: FleetConfig) {

  def occupiedPositions: Set[Pos] = vessels.flatMap(v => v.occupiedPos)

  def findByPos(pos: Pos): Option[Vessel] = vessels.find(v => v.occupiedPos.contains(pos))

  def findByName(name: String): Option[Vessel] = vessels.find(v => v.name == NonEmptyString(name))

  def findByType(vesselType: Class[_ <: Vessel]): Set[Vessel] = vessels.filter(v => v.size == Vessel.getSizeByType(vesselType))

  def withVessels: Fleet = {
    val vessels = for { ((k, v), i) <- fleetConfig.vesselMap.zipWithIndex; a <- 0 until v } yield {
      val direction = if (Random.nextBoolean()) Horizontal else Vertical
      val name = s"${k.getSimpleName} #${a + 1}"
      Vessel(k, NonEmptyString(name), Pos(0, 0), direction)
    }

    Fleet(vessels.toSet[Vessel], fleetConfig)
  }

}