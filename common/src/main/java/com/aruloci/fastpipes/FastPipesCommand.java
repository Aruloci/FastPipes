package com.aruloci.fastpipes;

import static net.minecraft.commands.Commands.literal;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/** {@code /createfastpipes} shows the active settings; {@code /createfastpipes reload} re-reads the file. */
public final class FastPipesCommand {

    private FastPipesCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("createfastpipes")
            .requires(source -> source.hasPermission(2))
            .executes(FastPipesCommand::show)
            .then(literal("reload").executes(FastPipesCommand::reload)));
    }

    private static int show(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal(describe(FastPipesConfig.current())), false);
        return 1;
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        try {
            FastPipesConfig config = FastPipesConfig.reload();
            ctx.getSource().sendSuccess(() -> Component.literal("Reloaded: " + describe(config)), true);
            return 1;
        } catch (IOException | IllegalStateException e) {
            ctx.getSource().sendFailure(Component.literal("Reload failed: " + e.getMessage()));
            return 0;
        }
    }

    private static String describe(FastPipesConfig config) {
        String cap = config.maxFlowRate() > 0 ? config.maxFlowRate() + " mB/t" : "unlimited";
        return "flowRateMultiplier = " + config.flowRateMultiplier() + ", maxFlowRate = " + cap;
    }
}
