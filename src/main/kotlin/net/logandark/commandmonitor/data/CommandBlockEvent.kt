package net.logandark.commandmonitor.data

import net.logandark.commandmonitor.CommandMonitor
import net.logandark.commandmonitor.SSTranslatableText
import net.logandark.commandmonitor.mixin.MixinServerCommandSource
import net.logandark.commandmonitor.text
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.CommandBlockExecutor
import java.util.Objects

data class CommandBlockEvent(
	val world: ServerWorld,
	val pos: BlockPos,
	val name: Text,
	val command: String,
	val executor: CommandBlockExecutor
) {
	companion object {
		fun from(source: ServerCommandSource, command: String): CommandBlockEvent? =
			((source as MixinServerCommandSource).output as? CommandBlockExecutor)?.let {
				CommandBlockEvent(it.world, BlockPos(it.pos), source.displayName, command, it)
			}
	}

	override fun hashCode(): Int = Objects.hash(world, pos.x, pos.y, pos.z)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as CommandBlockEvent

		if (world != other.world) return false
		if (pos != other.pos) return false
		if (name != other.name) return false
		if (command != other.command) return false
		if (executor != other.executor) return false

		return true
	}

	fun textEntry(): Text {
		val id = Registry.DIMENSION_TYPE.getId(world.dimension.type)?.toString()

		return LiteralText("")
			.append(LiteralText("")
				.styled { it.color = Formatting.GRAY }
				.append(LiteralText(id ?: "unknown").styled {
					it.color = Formatting.DARK_GRAY.takeIf { id == null }
				})
				.append(pos.text())
			)
			.append(" ")
			.append(LiteralText(command)
				.styled {
					it.color = Formatting.AQUA

					it.hoverEvent = HoverEvent(
						HoverEvent.Action.SHOW_TEXT,
						SSTranslatableText(
							CommandMonitor.translationKey("log.click_to_copy")
						)
					)

					it.clickEvent = ClickEvent(
						ClickEvent.Action.COPY_TO_CLIPBOARD,
						command
					)
				})
	}

	fun textRef(): Text {
		val id = Registry.DIMENSION_TYPE.getId(world.dimension.type)?.toString()
		val text = LiteralText("")

		text.append("@")
		text.append(LiteralText(id ?: "unknown").styled {
			it.color = id?.let { Formatting.GRAY } ?: Formatting.DARK_GRAY
		})
		text.append(pos.text().styled {
			it.color = Formatting.GRAY
		})

		if (executor.customName != null) {
			text.append(" \"")
			text.append(executor.customName)
			text.append("\"")
		}

		return text
	}
}
