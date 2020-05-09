package net.logandark.commandmonitor.config

import net.logandark.config.Config

object CommandMonitorConfig : Config("fabric-command-monitor.json", 1) {
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