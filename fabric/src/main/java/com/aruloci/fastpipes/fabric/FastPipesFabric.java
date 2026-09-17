package com.aruloci.fastpipes.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import com.aruloci.fastpipes.FastPipes;
import com.aruloci.fastpipes.FastPipesCommand;

public class FastPipesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(
            server -> FastPipes.onServerStarting(FabricLoader.getInstance().getConfigDir()));
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> FastPipesCommand.register(dispatcher));
    }
}
