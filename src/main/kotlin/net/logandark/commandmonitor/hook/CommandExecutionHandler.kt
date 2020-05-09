package net.logandark.commandmonitor.hook

import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CommandExecutionHandler {
	/**
	 * Returns `true` if the command should be executed, `false` otherwise. If
	 * in doubt, return true.
	 */
	fun preCommand(manager: CommandManager, source: ServerCommandSource, command: String): Boolean {
		return true
	}

	/**
	 * Returns a new return value to be used instead of the [output] value of
	 * the run command. If in doubt, return output.
	 */
	fun postCommand(manager: CommandManager, source: ServerCommandSource, command: String, output: Int): Int {
		return output
	}
}