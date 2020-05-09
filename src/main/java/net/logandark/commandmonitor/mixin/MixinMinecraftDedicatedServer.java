package net.logandark.commandmonitor.mixin;

import net.logandark.commandmonitor.CommandMonitor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
class MixinMinecraftDedicatedServer {
	@Inject(
		at = @At("HEAD"),
		method = "setupServer"
	)
	private void onSetup(CallbackInfoReturnable<Boolean> cir) {
		CommandMonitor.INSTANCE.setServer((MinecraftServer) (Object) this);
	}
}
