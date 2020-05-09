package net.logandark.commandmonitor.permissions

import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import net.logandark.commandmonitor.mixin.MixinServerConfigList
import net.minecraft.server.ServerConfigEntry
import net.minecraft.server.ServerConfigList
import java.io.File

object Permissions : ServerConfigList<GameProfile, PermissionsEntry>(
	File("command-monitor-ops.json")
) {
	override fun toString(profile: GameProfile): String = profile.id.toString()
	override fun fromJson(serialized: JsonObject): ServerConfigEntry<GameProfile> = PermissionsEntry(serialized)

	override fun save() {
		@Suppress("CAST_NEVER_SUCCEEDS")
		(this as MixinServerConfigList).callRemoveInvalidEntries()
		super.save()
	}

	private fun modifyEntry(profile: GameProfile, func: (PermissionsEntry) -> Unit) {
		get(profile)?.let {
			func(it)
			save()
		} ?: run {
			val entry = PermissionsEntry(profile)
			func(entry)
			add(entry)
		}
	}

	/**
	 * Returns true if this profile can see chat logs.
	 */
	fun canSeeChatLogs(profile: GameProfile): Boolean = get(profile)?.chatLogs ?: false

	/**
	 * Sets this profile's ability to see chat logs.
	 */
	fun setCanSeeChatLogs(profile: GameProfile, value: Boolean) = modifyEntry(profile) { it.chatLogs = value }

	/**
	 * Returns true if this profile is explicitly allowed via the permissions
	 * system. Separate from the ops list.
	 */
	fun isPrivileged(profile: GameProfile): Boolean = get(profile)?.isPrivileged ?: false

	/**
	 * Sets this profile's privilege status.
	 */
	fun setPrivileged(profile: GameProfile, value: Boolean) = modifyEntry(profile) { it.isPrivileged = value }
}