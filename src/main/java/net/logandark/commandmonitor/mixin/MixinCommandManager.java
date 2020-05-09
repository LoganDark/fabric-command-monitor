package net.logandark.commandmonitor.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.logandark.commandmonitor.command.CommandMonitorCommand;
import net.logandark.commandmonitor.CommandMonitor;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
	@Shadow
	@Final
	private CommandDispatcher<ServerCommandSource> dispatcher;

	/**
	 * Positive if we should not override execute, i.e. let a command execute
	 * normally. This is set inside of our mixin so that it doesn't cause
	 * infinite recursion. This is decremented every time it is read
	 */
	@Unique
	private int fabric_command_monitor_skipTimes;

	@Inject(
		at = @At("RETURN"),
		method = "<init>"
	)
	private void fabric_command_monitor_onInit(boolean isDedicatedServer, CallbackInfo ci) {
		fabric_command_monitor_skipTimes = 0;

		CommandMonitorCommand.INSTANCE.register(dispatcher);
	}

	/**
	 * This runs every time any command is executed, by anything :D
	 * <p>
	 * Including command blocks and console and etc.
	 * <p>
	 * This handles allowing commands to be cancelled and also making sure that
	 * {@link CommandMonitor} knows about any commands being executed.
	 *
	 * @param commandSource The source executing this command
	 * @param command       The command being executed (sans-`/`)
	 * @param cir           Something we can use to modify return values
	 */
	@Inject(
		at = @At("HEAD"),
		method = "execute",
		cancellable = true
	)
	private void fabric_command_monitor_onExecute(
		ServerCommandSource commandSource,
		String command,
		CallbackInfoReturnable<Integer> cir
	) {
		// allow for this method to call `execute` without triggering itself
		// infinitely
		if (fabric_command_monitor_skipTimes > 0) {
			fabric_command_monitor_skipTimes--;
			return;
		}

		CommandManager commandManager = (CommandManager) (Object) this;

		// preCommand returns true if we should execute the command, but false
		// if we shouldn't, so respect that
		if (CommandMonitor.INSTANCE.preCommand(commandManager, commandSource, command)) {
			// skip this time so the inner `execute` call doesn't cause infinite
			// recursion... but if there is another `execute` call inside that,
			// catch it :D
			fabric_command_monitor_skipTimes++;

			cir.setReturnValue(
				CommandMonitor.INSTANCE.postCommand(
					commandManager,
					commandSource,
					command,
					commandManager.execute(commandSource, command)
				)
			);
		} else {
			// generic non-ok return value (positive values are good, zero bad)
			cir.setReturnValue(0);
		}
	}
}
