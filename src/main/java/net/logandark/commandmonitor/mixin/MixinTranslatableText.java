package net.logandark.commandmonitor.mixin;

import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TranslatableText.class)
public interface MixinTranslatableText {
	@Invoker
	void callUpdateTranslations();
}
