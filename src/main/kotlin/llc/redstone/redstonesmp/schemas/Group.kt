package llc.redstone.redstonesmp.schemas

import com.google.gson.JsonObject
import java.util.UUID

data class Group(
    var name: String,
    var owner: String,
    var players: List<String>//uuids
) {
    fun setOwner(newOwner: UUID) = apply { owner = newOwner.toString() }
    fun addPlayer(player: UUID) = apply { players += player.toString() }
    fun removePlayer(player: UUID) = apply { players -= player.toString() }
    fun setPlayers(newPlayers: List<UUID>) = apply { players = newPlayers.map { it.toString() } }
    fun setName(newName: String) = apply { name = newName }

    companion object {
        fun decode(json: JsonObject): Group {
            return Group(
                json["name"].asString,
                json["owner"].asString,
                json["players"].asJsonArray.map { it.asString }
            )
        }
    }
}
