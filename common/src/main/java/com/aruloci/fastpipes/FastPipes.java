package com.aruloci.fastpipes;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FastPipes {
    public static final String MOD_ID = "create_fast_pipes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String CONFIG_FILE_NAME = MOD_ID + ".json";

    private FastPipes() {
    }

    /** Called by each platform when a server (dedicated or integrated) is about to start. */
    public static void onServerStarting(Path configDir) {
        Path file = configDir.resolve(CONFIG_FILE_NAME);
        try {
            FastPipesConfig config = FastPipesConfig.load(file);
            LOGGER.info("Loaded {}: {}", file.getFileName(), config);
        } catch (IOException e) {
            LOGGER.error("Could not read {}; using defaults", file, e);
        }
    }
}
