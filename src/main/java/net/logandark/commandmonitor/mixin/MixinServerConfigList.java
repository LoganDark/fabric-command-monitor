package net.logandark.commandmonitor.mixin;

import net.minecraft.server.ServerConfigList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerConfigList.class)
public interface MixinServerConfigList {
	@Invoker
	void callRemoveInvalidEntries();
}
