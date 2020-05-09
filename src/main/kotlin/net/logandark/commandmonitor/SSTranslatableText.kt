package net.logandark.commandmonitor

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.logandark.commandmonitor.ducks.TextSerializerDuck
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Language

/**
 * Like [TranslatableText], except it translates on the server before sending it
 * to any clients. Ideal for server-sided mods that don't want to require
 * clients to install their own lang files.
 */
class SSTranslatableText(
	key: String,
	vararg args: Any
) : TranslatableText(key, *args) {
	fun serialize(
		serializer: Text.Serializer,
		ctx: JsonSerializationContext
	): JsonObject {
		val jsonObject = JsonObject()
		val betterSerializer = serializer as TextSerializerDuck

		jsonObject.addProperty("text", "")

		if (!style.isEmpty)
			betterSerializer.callAddStyle(style, jsonObject, ctx)

		val extra = JsonArray()

		if (translations.isEmpty())
			jsonObject.addProperty("text", Language.getInstance().translate(key))
		else
			for (translation in translations)
				extra.add(serializer.serialize(translation, translation.javaClass, ctx))

		for (sibling in siblings)
			extra.add(serializer.serialize(sibling, sibling.javaClass, ctx))

		if (extra.size() > 0)
			jsonObject.add("extra", extra)

		return jsonObject
	}

	override fun copy(): SSTranslatableText {
		val objects = this.args.map {
			if (it is Text) {
				it.deepCopy()
			} else {
				it
			}
		}.toTypedArray()

		return SSTranslatableText(this.key, *objects)
	}
}