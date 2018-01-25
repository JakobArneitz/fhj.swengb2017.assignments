package at.fhj.swengb.apps.battleship.model

import javafx.scene.layout.GridPane
import at.fhj.swengb.apps.battleship.AlertBox
import scala.util.Random

case class Field(width: Int, height: Int, fleet: Fleet, fieldMode: Mode, isLocked: Boolean) {

  var game: Game = _

  var gridPane: GridPane = _

  var shoots: List[Pos] = List()

  var hits: Map[Vessel, Set[Pos]] = Map()

  var sunkShips: Set[Vessel] = Set()

  lazy val cells: Seq[Cell] = for {x <- 0 until width; y <- 0 until height; pos = Pos(x, y)} yield {
    val cellWidth = gridPane.getColumnConstraints.get(x).getPrefWidth
    val cellHeight = gridPane.getRowConstraints.get(y).getPrefHeight

    if (fieldMode.isEditing)
      EditableCell(this, Pos(x, y), fleet.findByPos(pos), cellWidth, cellHeight)
    else
      PlayableCell(this, Pos(x, y), fleet.findByPos(pos), cellWidth, cellHeight)
  }

  def setFleet(fleet: Fleet): Unit = {
    val field = Field(width, height, fleet, fieldMode, isLocked)
    game.resetField(field, this)
  }

  def setFleet(vessels: Set[Vessel]): Unit = {
    val fleet = Fleet(vessels, FleetConfig.Standard)
    val field = Field(width, height, fleet, fieldMode, isLocked)

    game.resetField(field, this)
  }

  def setIsLocked(isLocked: Boolean): Unit = {
    val field = Field(width, height, fleet, fieldMode, isLocked)
    game.resetField(field, this)
  }

  def addShoot(pos: Pos): Unit = if (!shoots.contains(pos)) shoots = shoots :+ pos

  def shoot(pos: Pos): Unit = {
    addShoot(pos)
    updateGameState(pos)
    game.resetField(this)
  }

  def updateSteps(takeSteps: Int): Unit = shoots.take(takeSteps).foreach {
    step => cells(step.x * width + step.y).getOnMouseClicked.handle(null)
  }

  def updateSlider(): Unit = {
    val x = shoots.length
    game.slider.setValue(x)
  }

  def updateGameState(pos: Pos): Unit = {
    val someVessel = fleet.findByPos(pos)

    if (someVessel.isEmpty) { AlertBox.sploosh(); return }

    val vessel = someVessel.get

    println("Vessel " + vessel.name.value + " was hit at position " + pos)

    if (hits.contains(vessel)) {
      val oldPos: Set[Pos] = hits(vessel)

      hits = hits.updated(vessel, oldPos + pos)
      hits(vessel).foreach(p => println(p.toString))

      if (oldPos.contains(pos))
        println("Position was triggered two times.")

      if (vessel.occupiedPos == hits(vessel)) {
        sunkShips = sunkShips + vessel

        println(s"Ship ${vessel.name.value} was destroyed.")
        AlertBox.explode()

        if (fleet.vessels == sunkShips) {
          println("G A M E totally  O V E R")
          game.onGameOver()
        }

      } else
        AlertBox.shoot()
    } else {
      println("First time hit")
      hits = hits.updated(vessel, Set(pos))
    }
  }

  def withRandomFleetPlacing: Field = {
    def loop(vesselsToPlace: Set[Vessel], workingBattleField: Field): Field = {
      if (vesselsToPlace.isEmpty)
        return workingBattleField

      val v = vesselsToPlace.head

      loop(vesselsToPlace.tail, workingBattleField.addAtRandomPosition(v))
    }

    loop(fleet.vessels, copy(fleet = fleet.copy(vessels = Set())))
  }

  private def addAtRandomPosition(v: Vessel): Field = {
    def loop(pos: Set[Pos], currBf: Field, found: Boolean): Field = {
      if (found || pos.isEmpty)
        return currBf

      val p = pos.toSeq(Random.nextInt(pos.size))
      val vessel = v.copy(startPos = p)

      if (vessel.occupiedPos.subsetOf(availablePos))
        loop(pos - p, currBf.copy(fleet = currBf.fleet.copy(vessels = currBf.fleet.vessels + vessel)), found = true)
      else
        loop(pos - p, currBf, found = false)
    }

    loop(availablePos, this, found = false)
  }

  private def allPos: Set[Pos] = (for {x <- 0 until width; y <- 0 until height} yield Pos(x, y)).toSet

  private def availablePos: Set[Pos] = allPos -- fleet.occupiedPositions

}