package at.fhj.swengb.apps.battleship.model

import javafx.application.Platform
import javafx.scene.control.Slider
import javafx.scene.layout.GridPane

case class Game(var field: Field,
                var enemyField: Field,
                updateCounters: (Field) => Unit,
                makeShoot: (Pos) => Unit,
                onGameOver: () => Unit) {

  var slider: Slider = _

  var fieldGridPane: GridPane = _

  var enemyFieldGridPane: GridPane = _

  var vesselDirection: Direction = Horizontal

  var vesselType: Class[_ <: Vessel] = classOf[BattleShip]

  def init(): Unit = {
    initField(field, fieldGridPane)
    initField(enemyField, enemyFieldGridPane)
  }

  def isVesselPlacementAllowed: Boolean = {
    val vesselCount = field.fleet.findByType(vesselType).size
    val allowedVesselCount = field.fleet.fleetConfig.vesselMap(vesselType)
    vesselCount < allowedVesselCount
  }

  def setField(field: Field, insteadOf: Field): Unit = insteadOf match {
    case f if f eq this.field => this.field = field; initField(field, fieldGridPane)
    case f if f eq enemyField => enemyField = field; initField(field, enemyFieldGridPane)
  }

  def resetField(field: Field): Unit = {
    val resetField = Field(field.width, field.height, field.fleet, field.fieldMode, field.isLocked)

    resetField.shoots = field.shoots
    resetField.hits = field.hits
    resetField.sunkShips = field.sunkShips

    field match {
      case f if f eq this.field => this.field = resetField; initField(resetField, fieldGridPane)
      case f if f eq enemyField => enemyField = resetField; initField(resetField, enemyFieldGridPane)
    }
  }

  def resetField(field: Field, insteadOf: Field): Unit = {
    field.shoots = insteadOf.shoots
    field.hits = insteadOf.hits
    field.sunkShips = insteadOf.sunkShips

    insteadOf match {
      case f if f eq this.field => this.field = field; initField(field, fieldGridPane)
      case f if f eq enemyField => enemyField = field; initField(field, enemyFieldGridPane)
    }
  }

  private def initField(field: Field, fieldGridPane: GridPane): Unit = {
    field.game = this
    field.gridPane = fieldGridPane

    Platform.runLater(new Runnable {
      override def run(): Unit = {
        fieldGridPane.getChildren.clear()
        field.cells.foreach { c => fieldGridPane.add(c, c.pos.x, c.pos.y); c.init() }
      }
    })
  }

}

