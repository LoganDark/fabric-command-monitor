package net.logandark.commandmonitor.mixin;

import net.logandark.commandmonitor.hook.CommandExecutionHandler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(method = "tickWorlds", at = @At("TAIL"))
	private void fabric_command_monitor_onTickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		CommandExecutionHandler.INSTANCE.dealWithWaiting();
		CommandExecutionHandler.INSTANCE.cycleCbEvents();
	}
}
