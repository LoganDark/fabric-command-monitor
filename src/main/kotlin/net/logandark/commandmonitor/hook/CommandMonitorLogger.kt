package net.logandark.commandmonitor.hook

import net.logandark.commandmonitor.CommandMonitor
import net.logandark.commandmonitor.SSTranslatableText
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.UUID

object CommandMonitorLogger {
	private val titleComponent: Text =
		LiteralText("[")
			.styled { it.withColor(Formatting.DARK_AQUA) }
			.append(
				SSTranslatableText(
					CommandMonitor.translationKey("log.name")
				).styled { it.withColor(Formatting.AQUA) }
			)
			.append(LiteralText("]"))

	/**
	 * Logs a message to console and all players authorized to see those logs.
	 */
	fun log(text: Text, console: Boolean, predicate: (ServerPlayerEntity) -> Boolean) {
		val server = CommandMonitor.server

		if (console)
			server.sendSystemMessage(text, UUID.randomUUID())

		server.playerManager.playerList.forEach {
			if (predicate(it))
				it.commandSource.sendFeedback(
					LiteralText("")
						.append(titleComponent.shallowCopy())
						.append(" ")
						.append(text),
					false
				)
		}
	}
}
