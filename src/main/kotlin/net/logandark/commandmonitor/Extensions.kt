package net.logandark.commandmonitor

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos

fun BlockPos.text(): MutableText = LiteralText("")
	.append("(")
	.append(LiteralText(x.toString()).styled { it.withColor(Formatting.AQUA) })
	.append(", ")
	.append(LiteralText(y.toString()).styled { it.withColor(Formatting.AQUA) })
	.append(", ")
	.append(LiteralText(z.toString()).styled { it.withColor(Formatting.AQUA) })
	.append(")")
	.styled {
		it.withHoverEvent(
			HoverEvent(
				HoverEvent.Action.SHOW_TEXT,
				SSTranslatableText(
					CommandMonitor.translationKey("log.click_to_suggest")
				)
			)
		).withClickEvent(
			ClickEvent(
				ClickEvent.Action.SUGGEST_COMMAND,
				"/tp %d %d %d".format(x, y, z)
			)
		)
	}
