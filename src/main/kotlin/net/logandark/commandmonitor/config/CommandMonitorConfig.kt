package net.logandark.commandmonitor.config

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import net.logandark.commandmonitor.CommandMonitor
import net.logandark.config.Config

object CommandMonitorConfig : Config("fabric-command-monitor.json", 1), ModMenuApi {
	override fun getModId() = CommandMonitor.modid
	override fun getModConfigScreenFactory() = ConfigScreenFactory(::createConfigScreen)

	/**
	 * True if all ops should be treated as privileged. Does not affect the
	 * privilege system.
	 */
	val useOpsList = add(
		ConfigBoolOption(
			"use-ops-list",
			"default",
			false
		)
	)
}