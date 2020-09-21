package net.logandark.commandmonitor.mixin;

import com.google.common.collect.ImmutableMap;
import net.logandark.commandmonitor.CommandMonitor;
import net.logandark.commandmonitor.LanguageHack;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Language.class)
public class MixinLanguage {
	@Inject(
		method = "create",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void fabric_command_monitor_onCreate(CallbackInfoReturnable<Language> cir, ImmutableMap.Builder<String, String> builder) {
		LanguageHack.INSTANCE.activate(CommandMonitor.modid, builder::put);
	}
}
