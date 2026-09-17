package com.aruloci.fastpipes;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastPipes {
    public static final String MOD_ID = "create_fast_pipes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void onServerStarting(Path configDir) {
        Path file = configDir.resolve(MOD_ID + ".json");
        try {
            LOGGER.info("Loaded {}: {}", file.getFileName(), FastPipesConfig.load(file));
        } catch (IOException e) {
            LOGGER.error("Could not read {}, using defaults", file, e);
        }
    }
}
