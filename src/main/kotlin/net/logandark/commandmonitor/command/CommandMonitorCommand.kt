package net.logandark.commandmonitor.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.logandark.commandmonitor.CommandMonitor
import net.logandark.commandmonitor.SSTranslatableText
import net.logandark.commandmonitor.config.CommandMonitorConfig
import net.logandark.commandmonitor.hook.CommandExecutionHandler
import net.logandark.commandmonitor.mixin.MixinServerCommandSource
import net.logandark.commandmonitor.permissions.Permissions
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object CommandMonitorCommand {
	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
		val root = CommandManager.literal("command-monitor")
		val chatLogs = CommandManager.literal("chat-logs")
		val chatLogsEnable = CommandManager.literal("enable")
		val chatLogsDisable = CommandManager.literal("disable")
		val commandBlockLogs = CommandManager.literal("command-block-logs")
		val commandBlockLogsEnable = CommandManager.literal("enable")
		val commandBlockLogsDisable = CommandManager.literal("disable")
		val useOpsList = CommandManager.literal("use-ops-list")
		val useOpsListEnable = CommandManager.literal("enable")
		val useOpsListDisable = CommandManager.literal("disable")
		val privilege = CommandManager.literal("privilege")
		val unprivilege = CommandManager.literal("unprivilege")
		val privileges = CommandManager.literal("privileges")
		val commandBlocks = CommandManager.literal("command-blocks")

		root.requires { ctx ->
			(ctx as MixinServerCommandSource).output == CommandMonitor.server
				|| CommandMonitor.isPrivileged(ctx.player.gameProfile)
		}

		forSelfOrSomeoneElse(chatLogs) { ctx, player ->
			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey(
						if (CommandMonitor.canSeeChatLogs(player.gameProfile))
							"command.chat-logs.enabled-currently"
						else
							"command.chat-logs.disabled-currently"
					),
					player.displayName
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(chatLogsEnable) { ctx, player ->
			Permissions.setCanSeeChatLogs(player.gameProfile, true)

			ctx.source.sendFeedback(
				SSTranslatableText(
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
				SSTranslatableText(
					CommandMonitor.translationKey("command.chat-logs.disabled"),
					player.displayName
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(commandBlockLogs) { ctx, player ->
			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey(
						if (CommandMonitor.canSeeCommandBlockLogs(player.gameProfile))
							"command.command-block-logs.enabled-currently"
						else
							"command.command-block-logs.disabled-currently"
					),
					player.displayName
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(commandBlockLogsEnable) { ctx, player ->
			Permissions.setCanSeeCommandBlockLogs(player.gameProfile, true)

			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey("command.command-block-logs.enabled"),
					player.displayName
				),
				false
			)

			1
		}

		forSelfOrSomeoneElse(commandBlockLogsDisable) { ctx, player ->
			Permissions.setCanSeeCommandBlockLogs(player.gameProfile, false)

			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey("command.command-block-logs.disabled"),
					player.displayName
				),
				false
			)

			1
		}

		useOpsList.executes { ctx ->
			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey(
						if (CommandMonitorConfig.useOpsList.get())
							"command.use-ops-list.enabled"
						else
							"command.use-ops-list.disabled"
					)
				),
				false
			)

			1
		}

		useOpsListEnable.executes { ctx ->
			CommandMonitorConfig.useOpsList.set(true)
			CommandMonitorConfig.save()

			val playerManager = CommandMonitor.server.playerManager
			for (player in playerManager.playerList) {
				if (!Permissions.isPrivileged(player.gameProfile) && playerManager.isOperator(player.gameProfile)) {
					playerManager.sendCommandTree(player)
				}
			}

			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey("command.use-ops-list.enabled")
				),
				false
			)

			1
		}

		useOpsListDisable.executes { ctx ->
			CommandMonitorConfig.useOpsList.set(false)
			CommandMonitorConfig.save()

			val playerManager = CommandMonitor.server.playerManager
			for (player in playerManager.playerList) {
				if (!Permissions.isPrivileged(player.gameProfile) && playerManager.isOperator(player.gameProfile)) {
					playerManager.sendCommandTree(player)
				}
			}

			ctx.source.sendFeedback(
				SSTranslatableText(
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
				SSTranslatableText(
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
				SSTranslatableText(
					CommandMonitor.translationKey("command.unprivileged"),
					player.displayName
				),
				false
			)

			1
		}

		privileges.executes { ctx ->
			ctx.source.sendFeedback(
				SSTranslatableText(
					CommandMonitor.translationKey("command.privileges")
				),
				false
			)

			val names = ArrayList<Text>()

			Permissions.values().stream().forEach { entry ->
				val profile = entry.profile
				val player = CommandMonitor.server.playerManager.getPlayer(profile.id)
				val text = player?.displayName?.copy() ?: LiteralText(profile.name)

				text.styled { style ->
					style.withHoverEvent(
						HoverEvent(
							HoverEvent.Action.SHOW_TEXT,
							LiteralText(profile.id.toString())
						)
					).withClickEvent(
						ClickEvent(
							ClickEvent.Action.COPY_TO_CLIPBOARD,
							profile.id.toString()
						)
					)
				}

				if (player != null) {
					names.add(text)
				} else {
					names.add(
						LiteralText("")
							.append(text)
							.append(LiteralText(" (offline)").styled { style ->
								style.withColor(Formatting.DARK_GRAY)
							})
					)
				}
			}

			names.sortBy(Text::asString)
			names.forEach { ctx.source.sendFeedback(it, false) }

			1
		}

		commandBlocks.executes { ctx ->
			CommandExecutionHandler.cbWaiting.add(ctx.source)
			1
		}

		dispatcher.register(
			root.then(
				chatLogs
					.then(chatLogsEnable)
					.then(chatLogsDisable)
			).then(
				commandBlockLogs
					.then(commandBlockLogsEnable)
					.then(commandBlockLogsDisable)
			).then(
				useOpsList
					.then(useOpsListEnable)
					.then(useOpsListDisable)
			)
				.then(privilege)
				.then(unprivilege)
				.then(privileges)
				.then(commandBlocks)
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
