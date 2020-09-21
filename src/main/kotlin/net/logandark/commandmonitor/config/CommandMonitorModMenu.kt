package net.logandark.commandmonitor.config

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import net.logandark.commandmonitor.CommandMonitor

object CommandMonitorModMenu : ModMenuApi {
	override fun getModId() = CommandMonitor.modid
	override fun getModConfigScreenFactory() = ConfigScreenFactory(CommandMonitorConfig::createConfigScreen)
}
