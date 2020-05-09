package net.logandark.commandmonitor.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SharedConstants.class)
public abstract class MixinSharedConstants {
	@Shadow
	public static boolean isDevelopment;

	@Inject(
		at = @At("RETURN"),
		method = "<clinit>"
	)
	private static void onClinit(CallbackInfo ci) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			isDevelopment = true;
		}
	}
}
