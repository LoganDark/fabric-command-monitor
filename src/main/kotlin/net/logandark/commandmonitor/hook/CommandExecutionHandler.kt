package net.logandark.commandmonitor.hook

import net.logandark.commandmonitor.CommandMonitor
import net.logandark.commandmonitor.SSTranslatableText
import net.logandark.commandmonitor.data.CommandBlockEvent
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CommandExecutionHandler {
	/**
	 * Command block events last tick
	 */
	private val lastTickCbEvents = hashSetOf<CommandBlockEvent>()

	/**
	 * A set containing all the command blocks that have executed a command this
	 * tick
	 */
	private val cbEvents = hashSetOf<CommandBlockEvent>()

	/**
	 * List of ServerCommandSources waiting for a list of command blocks. This
	 * is here so that they are only returned a result at the end of a tick,
	 * ensuring the list of command blocks is complete
	 */
	val cbWaiting = mutableListOf<ServerCommandSource>()

	/**
	 * Returns `true` if the command should be executed, `false` otherwise. If
	 * in doubt, return true.
	 */
	fun preCommand(manager: CommandManager, source: ServerCommandSource, command: String): Boolean {
		// If this is a command block, add it to the set
		CommandBlockEvent.from(source, command)?.also { cbEvents.add(it) }

		return true
	}

	/**
	 * Returns a new return value to be used instead of the [output] value of
	 * the run command. If in doubt, return output.
	 */
	fun postCommand(manager: CommandManager, source: ServerCommandSource, command: String, output: Int): Int {
		return output
	}

	fun dealWithWaiting() {
		val waiting = mutableListOf<ServerCommandSource>()
		waiting.addAll(cbWaiting)
		cbWaiting.clear() // possible race condition here.... very unlikely

		for (source in waiting) {
			source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey("command.command-blocks.tick")
				),
				false
			)

			for (event in cbEvents) {
				source.sendFeedback(event.textEntry(), false)
			}
		}
	}

	fun cycleCbEvents() {
		lastTickCbEvents.clear()
		lastTickCbEvents.addAll(cbEvents)
		cbEvents.clear() // possible race condition here too, also very unlikely
	}

	/**
	 * Returns true if the passed command block event is a repeat of a command
	 * executed in the last tick
	 */
	fun isRepeat(commandBlockEvent: CommandBlockEvent): Boolean {
		return lastTickCbEvents.contains(commandBlockEvent)
	}
}
