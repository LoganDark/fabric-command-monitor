package net.logandark.commandmonitor.mixin;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(TranslatableText.class)
public interface MixinTranslatableText {
	@Invoker("updateTranslations")
	void fabric_command_monitor_updateTranslations();

	@Accessor("translations")
	List<StringVisitable> fabric_command_monitor_getTranslations();
}
