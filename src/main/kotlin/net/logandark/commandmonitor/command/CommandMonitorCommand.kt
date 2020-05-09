package net.logandark.commandmonitor.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.logandark.commandmonitor.CommandMonitor
import net.logandark.commandmonitor.config.CommandMonitorConfig
import net.logandark.commandmonitor.mixin.MixinServerCommandSource
import net.logandark.commandmonitor.permissions.Permissions
import net.minecraft.command.arguments.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting

object CommandMonitorCommand {
	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
		val root = CommandManager.literal("command-monitor")
		val chatLogs = CommandManager.literal("chat-logs")
		val chatLogsEnable = CommandManager.literal("enable")
		val chatLogsDisable = CommandManager.literal("disable")
		val useOpsList = CommandManager.literal("use-ops-list")
		val useOpsListEnable = CommandManager.literal("enable")
		val useOpsListDisable = CommandManager.literal("disable")
		val privilege = CommandManager.literal("privilege")
		val unprivilege = CommandManager.literal("unprivilege")
		val privileges = CommandManager.literal("privileges")

		root.requires { ctx ->
			(ctx as MixinServerCommandSource).output == CommandMonitor.server
				|| CommandMonitor.isPrivileged(ctx.player.gameProfile)
		}

		forSelfOrSomeoneElse(chatLogsEnable) { ctx, player ->
			Permissions.setCanSeeChatLogs(player.gameProfile, true)

			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.chat-logs.enabled"),
					player.displayName
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(chatLogsDisable) { ctx, player ->
			Permissions.setCanSeeChatLogs(player.gameProfile, false)

			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.chat-logs.disabled"),
					player.displayName
				),
				false
			)

			1
		}

		useOpsListEnable.executes { ctx ->
			CommandMonitorConfig.useOpsList.set(true)
			CommandMonitorConfig.save()

			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.use-ops-list.enabled")
				),
				false
			)

			1
		}

		useOpsListDisable.executes { ctx ->
			CommandMonitorConfig.useOpsList.set(false)
			CommandMonitorConfig.save()

			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.use-ops-list.disabled")
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(privilege) { ctx, player ->
			Permissions.setPrivileged(player.gameProfile, true)
			CommandMonitor.server.playerManager.sendCommandTree(player)

			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.privileged"),
					player.displayName
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(unprivilege) { ctx, player ->
			Permissions.setPrivileged(player.gameProfile, false)
			CommandMonitor.server.playerManager.sendCommandTree(player)

			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.unprivileged"),
					player.displayName
				),
				false
			)

			1
		}

		privileges.executes { ctx ->
			ctx.source.sendFeedback(
				TranslatableText(
					CommandMonitor.translationKey("command.privileges")
				),
				false
			)

			val names = ArrayList<Text>()

			Permissions.values().stream().forEach { entry ->
				val profile = entry.profile
				val player = CommandMonitor.server.playerManager.getPlayer(profile.id)
				val text = player?.displayName ?: LiteralText(profile.name)

				text.styled { style ->
					style.hoverEvent = HoverEvent(
						HoverEvent.Action.SHOW_TEXT,
						LiteralText(profile.id.toString())
					)

					style.clickEvent = ClickEvent(
						ClickEvent.Action.COPY_TO_CLIPBOARD,
						profile.id.toString()
					)
				}

				if (player != null) {
					names.add(text)
				} else {
					names.add(
						LiteralText("")
							.append(text)
							.append(LiteralText(" (offline)").styled { style ->
								style.color = Formatting.DARK_GRAY
							})
					)
				}
			}

			names.sortBy(Text::asString)
			names.forEach { ctx.source.sendFeedback(it, false) }

			1
		}

		dispatcher.register(
			root.then(
				chatLogs
					.then(chatLogsEnable)
					.then(chatLogsDisable)
			).then(
				useOpsList
					.then(useOpsListEnable)
					.then(useOpsListDisable)
			)
				.then(privilege)
				.then(unprivilege)
				.then(privileges)
		)
	}

	private fun <T : ArgumentBuilder<ServerCommandSource, T>> forSelfOrSomeoneElse(
		builder: T,
		callback: (CommandContext<ServerCommandSource>, ServerPlayerEntity) -> Int
	) {
		builder.executes { ctx ->
			callback(ctx, ctx.source.player)
		}

		val playerArg = CommandManager.argument("player", EntityArgumentType.player())

		playerArg.executes { ctx ->
			callback(ctx, EntityArgumentType.getPlayer(ctx, "player"))
		}

		builder.then(playerArg)
	}
}