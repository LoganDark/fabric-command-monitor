package net.logandark.commandmonitor.hook

import net.logandark.commandmonitor.CommandMonitor
import net.logandark.commandmonitor.SSTranslatableText
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object CommandMonitorLogger {
	private val titleComponent: Text =
		LiteralText("[")
			.styled {
				it.color = Formatting.DARK_AQUA
			}
			.append(
				SSTranslatableText(
					CommandMonitor.translationKey("log.name")
				).styled {
					it.color = Formatting.AQUA
				}
			)
			.append(LiteralText("]"))

	/**
	 * Logs a message to console and all players authorized to see those logs.
	 */
	fun log(text: Text) {
		val server = CommandMonitor.server

		// first and foremost, always send it to console
		server.sendMessage(text)

		// second, send it to all authorized players
		server.playerManager.playerList.forEach {
			if (CommandMonitor.canSeeChatLogs(it.gameProfile))
				it.commandSource.sendFeedback(
					LiteralText("")
						.append(titleComponent.deepCopy())
						.append(" ")
						.append(text),
					false
				)
		}
	}
}