package net.logandark.commandmonitor

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Language
import org.apache.logging.log4j.core.util.Closer
import java.nio.file.Files

object LanguageHack {
	fun activate(modid: String, consumer: (String, String) -> Any?) {
		val inputStream = Files.newInputStream(
			FabricLoader.getInstance()
				.getModContainer(modid).get()
				.getPath("assets/$modid/lang/en_us.json")
		)

		try {
			Language.load(inputStream) { k, v -> consumer(k, v) }
		} finally {
			Closer.closeSilently(inputStream)
		}
	}
}
