package at.fhj.swengb.apps.battleship.model

class PlayerObject() {
  private var name: String = _
  private var isFleetPlacing: Boolean = _
  private var vessels: String = _

  def PlayerObject(name: String, isFleetPlacing: Boolean, vessels: String) {
    this.name = name
    this.isFleetPlacing = isFleetPlacing
    this.vessels = vessels
  }

  def getName: String = name
  def getIsFleetPlacing: Boolean = isFleetPlacing
  def getVessels: String = vessels

  def setName(name: String): Unit = this.name = name
  def setIsFleetPlacing(isFleetPlacing: Boolean): Unit = this.isFleetPlacing = isFleetPlacing
  def setVessels(vessels: String): Unit = this.vessels = vessels
}