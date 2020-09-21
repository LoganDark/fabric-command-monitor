package net.logandark.commandmonitor.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.logandark.commandmonitor.CommandMonitor
import net.logandark.config.Config
import net.minecraft.text.TranslatableText

class ConfigBoolOption(
	name: String,
	category: String,
	defaultValue: Boolean,
	tweakEntry: (AbstractConfigListEntry<Boolean>.() -> Unit)? = null
) : Config.ConfigOption<Boolean>(
	CommandMonitor.identifier(name),
	category,
	defaultValue,
	tweakEntry
) {
	override fun makeEntry(entryBuilder: ConfigEntryBuilder) =
		entryBuilder
			.startBooleanToggle(TranslatableText(translationKey), get())
			.setDefaultValue(defaultValue)
			.setSaveConsumer(this::set)
			.build()

	override fun serialize() = JsonPrimitive(get())

	override fun deserialize(jsonElement: JsonElement) =
		if (jsonElement is JsonPrimitive && jsonElement.isBoolean)
			jsonElement.asBoolean
		else
			error("Invalid JsonElement to deserialize")
}
