package net.logandark.commandmonitor

import com.mojang.authlib.GameProfile
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.logandark.commandmonitor.config.CommandMonitorConfig
import net.logandark.commandmonitor.hook.CommandExecutionHandler
import net.logandark.commandmonitor.hook.CommandMonitorLogger
import net.logandark.commandmonitor.permissions.Permissions
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier

// For support join https://discord.gg/v6v4pMv

@Suppress("unused")
object CommandMonitor : ModInitializer {
	const val modid = "fabric-command-monitor"
	lateinit var server: MinecraftServer

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		println("Hello Fabric world!")

		// Cycle config to ensure the latest values are read & any errors are
		// fixed and written to file.
		CommandMonitorConfig.load()
		CommandMonitorConfig.save()

		// Load permissions file to ensure that saved changes are not lost.
		Permissions.load()

		// The registration of CommandMonitorCommand is handled by
		// MixinCommandManager.

		// And activate the language hack for localizations on server.
		if (FabricLoader.getInstance().environmentType == EnvType.SERVER)
			LanguageHack.activate(modid)
	}

	/**
	 * Creates an [Identifier] with [modid] as its namespace and [path] as its
	 * path.
	 */
	fun identifier(path: String) = Identifier(modid, path)

	/**
	 * Returns a translation key in the form of [modid].[path]
	 */
	fun translationKey(path: String) = "$modid.$path"

	/**
	 * Whether to override the config option and use the ops list for this
	 * profile
	 *
	 * Right now, it only returns true if you are the owner of a singleplayer
	 * server
	 */
	private fun useOpsListFor(profile: GameProfile): Boolean {
		return if (CommandMonitorConfig.useOpsList.get())
			true
		else
			CommandMonitor.server.isSinglePlayer && CommandMonitor.server.isOwner(profile)
	}

	/**
	 * Returns `true` if [profile] is allowed to see command monitor logs in
	 * their chat.
	 */
	fun canSeeChatLogs(profile: GameProfile): Boolean {
		return Permissions.canSeeChatLogs(profile)
	}

	/**
	 * Returns `true` if [profile] is allowed to use command monitor commands.
	 */
	fun isPrivileged(profile: GameProfile): Boolean {
		if (Permissions.isPrivileged(profile)) return true

		return useOpsListFor(profile) && server.playerManager.isOperator(profile)
	}

	/**
	 * Returns `true` if the command should be executed, `false` otherwise. If
	 * in doubt, return true.
	 */
	fun preCommand(manager: CommandManager, source: ServerCommandSource, command: String): Boolean {
		val authorized = CommandExecutionHandler.preCommand(manager, source, command)

		CommandMonitorLogger.log(
			SSTranslatableText(
				translationKey(if (authorized) "log.run" else "log.attempt"),
				source.displayName,
				LiteralText(command).styled {
					it.hoverEvent = HoverEvent(
						HoverEvent.Action.SHOW_TEXT,
						SSTranslatableText(translationKey("log.click_to_copy"))
					)

					it.clickEvent = ClickEvent(
						ClickEvent.Action.COPY_TO_CLIPBOARD,
						command
					)
				}
			)
		)

		return authorized
	}

	/**
	 * Returns a new return value to be used instead of the [output] value of
	 * the run command. If in doubt, return output.
	 */
	fun postCommand(manager: CommandManager, source: ServerCommandSource, command: String, output: Int): Int {
		return CommandExecutionHandler.postCommand(manager, source, command, output)
	}
}