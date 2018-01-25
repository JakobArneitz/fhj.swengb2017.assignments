package at.fhj.swengb.apps.battleship.model

object Player {

  def apply(name: String, isFleetPlacing: Boolean, vessels: Set[Vessel]): Player = {
    val player = new Player(name)
    player._isFleetPlacing = isFleetPlacing
    player.vessels = vessels
    player
  }

}

case class Player(name: String) {

  var vessels: Set[Vessel] = Set()

  private var _isFleetPlacing = true

  def isFleetPlacing: Boolean = _isFleetPlacing

  def endFleetPlacing(): Unit = _isFleetPlacing = false

}