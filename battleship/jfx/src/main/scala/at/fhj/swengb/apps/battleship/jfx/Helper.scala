package at.fhj.swengb.apps.battleship.jfx

import org.json.{JSONArray, JSONObject}
import at.fhj.swengb.apps.battleship.model._

object Helper {

  def parsePlayerFromJson(args: AnyRef): Player = {
    val json = args.asInstanceOf[JSONObject]
    val name = json.getString("name")
    val isFleetPlacing = json.getBoolean("isFleetPlacing")
    var vessels: Set[Vessel] = parseVesselsFromString(json.getString("vessels"))

    Player(name, isFleetPlacing, vessels)
  }

  def parsePlayerFromPlayerObject(playerObject: PlayerObject): Player = {
    val vessels = Helper.parseVesselsFromString(playerObject.getVessels)
    Player(playerObject.getName, playerObject.getIsFleetPlacing, vessels)
  }

  def parsePosFromJson(args: AnyRef): Pos = {
    val json = args.asInstanceOf[JSONObject]
    Pos(json.getInt("x"), json.getInt("y"))
  }

  def parsePosFromJson2(json: JSONObject): Pos = Pos(json.getInt("x"), json.getInt("y"))

  def parseVesselsFromString(str: String): Set[Vessel] = {
    val vesselsJson = new JSONArray(str)
    var vessels: Set[Vessel] = Set()

    for (i <- 0 until vesselsJson.length()) {
      val vesselJson = vesselsJson.getJSONObject(i)
      val name = vesselJson.getString("name")
      val startPos = parsePosFromJson2(vesselJson.getJSONObject("startPos"))
      val direction = parseDirectionFromString(vesselJson.getString("direction"))
      val size = vesselJson.getInt("size")
      val vessel = Vessel(NonEmptyString(name), startPos, direction, size)
      vessels = vessels + vessel
    }

    vessels
  }

  def parseDirectionFromString(str: String): Direction = str match {
    case "HORIZONTAL" => Horizontal
    case "VERTICAL" => Vertical
  }

  def toPlayerObject(player: Player): PlayerObject = {
    val playerObject = new PlayerObject()
    val vessels = toString(player.vessels)

    playerObject.setName(player.name)
    playerObject.setIsFleetPlacing(player.isFleetPlacing)
    playerObject.setVessels(vessels)
    playerObject
  }

  def toString(vessels: Set[Vessel]): String = {
    val vesselsJson = new JSONArray()

    vessels.foreach { v => vesselsJson.put(toJson(v)) }

    vesselsJson.toString
  }

  def toJson(vessel: Vessel): JSONObject = {
    val vesselJson = new JSONObject()

    vesselJson.put("name", vessel.name.value)
    vesselJson.put("startPos", toJson(vessel.startPos))
    vesselJson.put("direction", toJson(vessel.direction))
    vesselJson.put("size", vessel.size)
  }

  def toJson(direction: Direction): String = direction match {
    case Horizontal => "HORIZONTAL"
    case Vertical => "VERTICAL"
  }

  def toJson(player: Player): JSONObject = {
    val json = new JSONObject()
    val vessels = new JSONArray()

    player.vessels.foreach { v => vessels.put(toJson(v)) }

    json.put("name", player.name)
    json.put("isFleetPlacing", player.isFleetPlacing)
    json.put("vessels", vessels.toString)
    json
  }

  def toJson(pos: Pos): JSONObject = {
    val p = new JSONObject()
    p.put("x", pos.x)
    p.put("y", pos.y)
    p
  }

}
