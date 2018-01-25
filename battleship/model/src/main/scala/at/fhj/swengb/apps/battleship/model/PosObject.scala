package at.fhj.swengb.apps.battleship.model

class PosObject() {
  private var x: Int = 0
  private var y: Int = 0

  def PosObject(x: Int, y: Int) {
    this.x = x
    this.y = y
  }

  def getPos: Pos = Pos(x, y)

  def getX: Int = x
  def getY: Int = y

  def setX(x: Int): Unit = this.x = x
  def setY(y: Int): Unit = this.y = y
}