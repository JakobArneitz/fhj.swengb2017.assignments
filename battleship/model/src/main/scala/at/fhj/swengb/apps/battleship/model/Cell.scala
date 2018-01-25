package at.fhj.swengb.apps.battleship.model

import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color

sealed abstract class Cell(val field: Field,
                           val pos: Pos,
                           val vessel: Option[Vessel] = None,
                           val width: Double,
                           val height: Double
                          ) extends Rectangle(width, height) {

  def init(): Unit
  def setCellColor(color: Color): Unit

}

case class PlayableCell(override val field: Field,
                        override val pos: Pos,
                        override val vessel: Option[Vessel] = None,
                        override val width: Double,
                        override val height: Double
                       ) extends Cell(field, pos, vessel, width, height) {

  private val COLOR_WATER = Color.BLACK
  private val COLOR_WATER_HIT = Color.color(.3, .3, .3)
  private val COLOR_SHIP_HIT = Color.color(.82, .10, .10)
  private val COLOR_MOUSE_OVER = Color.color(.52, .80, .90)

  private var cellColor: Color = _

  private val game = field.game

  def init(): Unit = {
    if (!field.shoots.contains(pos)) { setCellColor(COLOR_WATER); return }

    val color = if (vessel.isDefined) COLOR_SHIP_HIT else COLOR_WATER_HIT

    setCellColor(color)
  }

  def setCellColor(color: Color): Unit = {
    cellColor = color
    setFill(color)
  }

  def shoot(): Unit = {
    if (field.shoots.contains(pos)) { return }

    val color = if (vessel.isDefined) COLOR_SHIP_HIT else COLOR_WATER_HIT

    setCellColor(color)
    field.addShoot(pos)
    game.makeShoot(pos)
  }

  setOnMouseClicked { _ => if (!field.isLocked) shoot() }

  setOnMouseEntered { _ => if (!field.isLocked) setFill(COLOR_MOUSE_OVER) }

  setOnMouseExited { _ => if (!field.isLocked) setFill(cellColor) }

}

case class EditableCell(override val field: Field,
                        override val pos: Pos,
                        override val vessel: Option[Vessel] = None,
                        override val width: Double,
                        override val height: Double) extends Cell(field, pos, vessel, width, height) {

  private val COLOR_WATER = Color.BLACK
  private val COLOR_WATER_HIT = Color.color(.3, .3, .3)
  private val COLOR_SHIP = Color.WHITE
  private val COLOR_SHIP_HIT = Color.color(.82, .10, .10)
  private val COLOR_MOUSE_OVER_WRONG = Color.color(.82, .10, .10)

  private var cellColor: Color = COLOR_WATER

  private val game = field.game

  def init(): Unit = {
    val shootExist = field.shoots.contains(pos)

    val color = vessel match {
      case Some(_) if shootExist => COLOR_SHIP_HIT
      case Some(_) if !shootExist => COLOR_SHIP
      case None if shootExist => COLOR_WATER_HIT
      case None if !shootExist => COLOR_WATER
    }

    setCellColor(color)
    game.updateCounters(field)
  }

  def setCellColor(color: Color): Unit = {
    cellColor = color
    setFill(color)
  }

  setOnMouseClicked { e => if (!field.isLocked) onMouseClicked(e) }

  setOnMouseEntered { _ => if (!field.isLocked) onMouseEntered() }

  setOnMouseExited { _ => if (!field.isLocked) onMouseExited() }

  private def onMouseClicked(e: MouseEvent): Unit = e.getButton match {
    case MouseButton.SECONDARY if game.isVesselPlacementAllowed => hideShip(); toggleVesselDirection(); showShip()
    case MouseButton.PRIMARY if getVesselCells.isDefined && game.isVesselPlacementAllowed =>
      val vesselNumber = field.fleet.findByType(game.vesselType).size + 1
      val vesselName = s"${game.vesselType.getSimpleName} #$vesselNumber"
      val vessel = Vessel(game.vesselType, NonEmptyString(vesselName), pos, game.vesselDirection)
      val fleet = Fleet(field.fleet.vessels + vessel, field.fleet.fleetConfig)
      field.setFleet(fleet)
      game.updateCounters(game.field)
    case _ =>
  }

  private def onMouseEntered(): Unit = { if (game.isVesselPlacementAllowed) showShip(); showCursor() }

  private def onMouseExited(): Unit = { hideShip(); hideCursor() }

  private def showShip(): Unit = getVesselCells.getOrElse(List()).foreach(_.setFill(COLOR_SHIP))

  private def hideShip(): Unit = getVesselCells.getOrElse(List()).foreach(_.setFill(cellColor))

  private def showCursor(): Unit = if (getVesselCells.isEmpty || !game.isVesselPlacementAllowed) setFill(COLOR_MOUSE_OVER_WRONG)

  private def hideCursor(): Unit = setFill(cellColor)

  private def toggleVesselDirection(): Unit = game.vesselDirection match {
    case Horizontal => game.vesselDirection = Vertical
    case Vertical => game.vesselDirection = Horizontal
  }

  private def getVesselCells: Option[List[Cell]] = {
    val size = Vessel.getSizeByType(game.vesselType)

    def getEmptyCell(pos: Pos) = field.cells.find { c => c.pos == pos && c.vessel.isEmpty }

    def getNextCell(i: Int) = game.vesselDirection match {
      case Horizontal => getEmptyCell(Pos(pos.x + i, pos.y))
      case Vertical => getEmptyCell(Pos(pos.x, pos.y + i))
    }

    def loop(i: Int, cells: List[Cell]): Option[List[Cell]] = i match {
      case `size` => Some(cells)
      case _ => getNextCell(i) match {
        case Some(c) => loop(i + 1, cells :+ c)
        case _ => None
      }
    }

    loop(0, List(this))
  }

}