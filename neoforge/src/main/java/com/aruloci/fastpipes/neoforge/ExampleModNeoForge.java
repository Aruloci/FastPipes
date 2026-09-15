package com.aruloci.fastpipes.neoforge;

import net.neoforged.fml.common.Mod;

import com.aruloci.fastpipes.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
