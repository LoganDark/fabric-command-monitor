package net.logandark.commandmonitor.ducks

import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.minecraft.text.Style

interface TextSerializerDuck {
	fun callAddStyle(style: Style, json: JsonObject, context: JsonSerializationContext)
}
