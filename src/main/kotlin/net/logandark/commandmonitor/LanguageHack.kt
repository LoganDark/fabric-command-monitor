package net.logandark.commandmonitor

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.fabricmc.loader.api.FabricLoader
import net.logandark.commandmonitor.mixin.MixinLanguage
import net.minecraft.util.JsonHelper
import net.minecraft.util.Language
import org.apache.logging.log4j.core.util.Closer
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object LanguageHack {
	fun activate(modid: String) {
		val language = Language.getInstance() as MixinLanguage
		val inputStream = FabricLoader.getInstance()
			.getModContainer(modid).get()
			.getPath("assets/$modid/lang/en_us.json")
			.toFile().inputStream()

		try {
			val jsonObject = Gson().fromJson(
				InputStreamReader(inputStream, StandardCharsets.UTF_8),
				JsonObject::class.java
			)

			jsonObject.entrySet().forEach { entry ->
				val string = language.field_11489
					.matcher(JsonHelper.asString(entry.value, entry.key))
					.replaceAll("%$1s")
				language.translations[entry.key] = string
			}
		} finally {
			Closer.closeSilently(inputStream)
		}
	}
}