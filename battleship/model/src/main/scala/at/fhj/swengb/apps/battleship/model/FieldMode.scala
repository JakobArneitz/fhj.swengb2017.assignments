package at.fhj.swengb.apps.battleship.model

sealed abstract class Mode {
  def isEditing: Boolean
}

case object EditingMode extends Mode {
  override def isEditing: Boolean = true
}

case object PlayingMode extends Mode {
  override def isEditing: Boolean = false
}
