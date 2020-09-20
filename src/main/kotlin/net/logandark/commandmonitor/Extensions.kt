package net.logandark.commandmonitor

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos

fun BlockPos.text(): Text = LiteralText("")
	.append("(")
	.append(LiteralText(x.toString()).styled { it.color = Formatting.AQUA })
	.append(", ")
	.append(LiteralText(y.toString()).styled { it.color = Formatting.AQUA })
	.append(", ")
	.append(LiteralText(z.toString()).styled { it.color = Formatting.AQUA })
	.append(")")
	.styled {
		it.hoverEvent = HoverEvent(
			HoverEvent.Action.SHOW_TEXT,
			SSTranslatableText(
				CommandMonitor.translationKey("log.click_to_suggest")
			)
		)

		it.clickEvent = ClickEvent(
			ClickEvent.Action.SUGGEST_COMMAND,
			"/tp %d %d %d".format(x, y, z)
		)
	}
