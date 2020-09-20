package net.logandark.commandmonitor.permissions

import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import net.minecraft.server.ServerConfigEntry
import java.util.UUID

class PermissionsEntry(
	val profile: GameProfile
) : ServerConfigEntry<GameProfile>(profile) {
	companion object {
		/**
		 * Returns a new [GameProfile] retrieved from the specified
		 * [JsonObject].
		 */
		fun getProfileFromJson(serialized: JsonObject): GameProfile {
			val uuid = UUID.fromString(serialized.get("uuid").asString)
			val name = serialized.get("name").asString

			return GameProfile(uuid, name)
		}

		/**
		 * Writes the specified [GameProfile] to the provided [JsonObject].
		 */
		fun writeProfileToJson(profile: GameProfile, jsonObject: JsonObject) {
			jsonObject.addProperty("uuid", profile.id.toString())
			jsonObject.addProperty("name", profile.name)
		}
	}

	/**
	 * If the user is privileged, they can access all Command Monitor commands.
	 */
	var isPrivileged: Boolean = false

	/**
	 * If this user can see every command in their chat.
	 */
	var chatLogs: Boolean = false

	/**
	 * If this user is doomed to see command block logs in their chat
	 */
	var commandBlockLogs: Boolean = false

	constructor(serialized: JsonObject) : this(getProfileFromJson(serialized)) {
		deserializeFrom(serialized)
	}

	/**
	 * Writes this entry's data into the provided [JsonObject].
	 */
	override fun serialize(jsonObject: JsonObject) {
		writeProfileToJson(profile, jsonObject)
		jsonObject.addProperty("isPrivileged", isPrivileged)
		jsonObject.addProperty("chatLogs", chatLogs)
		jsonObject.addProperty("commandBlockLogs", commandBlockLogs)
	}

	/**
	 * Reads the provided [JsonObject]'s data into fields.
	 *
	 * This does not deserialize the [GameProfile], you should have provided
	 * that to the constructor.
	 */
	private fun deserializeFrom(serialized: JsonObject) {
		isPrivileged = serialized.get("isPrivileged").asBoolean
		chatLogs = serialized.get("chatLogs").asBoolean
		commandBlockLogs = serialized.get("commandBlockLogs")?.asBoolean ?: false
	}

	/**
	 * Returns true if this entry can be omitted from the serialized data.
	 */
	override fun isInvalid() = !isPrivileged && !chatLogs && !commandBlockLogs
}
