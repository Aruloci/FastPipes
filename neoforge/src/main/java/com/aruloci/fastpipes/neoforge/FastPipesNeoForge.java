package com.aruloci.fastpipes.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import com.aruloci.fastpipes.FastPipes;
import com.aruloci.fastpipes.FastPipesCommand;

@Mod(FastPipes.MOD_ID)
public class FastPipesNeoForge {
    public FastPipesNeoForge() {
        NeoForge.EVENT_BUS.addListener(ServerAboutToStartEvent.class,
            event -> FastPipes.onServerStarting(FMLPaths.CONFIGDIR.get()));
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class,
            event -> FastPipesCommand.register(event.getDispatcher()));
    }
}
