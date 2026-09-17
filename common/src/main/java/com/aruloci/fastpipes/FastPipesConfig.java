package com.aruloci.fastpipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public record FastPipesConfig(double flowRateMultiplier, int maxFlowRate) {

    public static final FastPipesConfig DEFAULT = new FastPipesConfig(1.0, 0);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static volatile FastPipesConfig current = DEFAULT;
    private static volatile Path lastFile;

    public static FastPipesConfig current() {
        return current;
    }

    public float apply(float original) {
        double result = original * flowRateMultiplier;
        if (maxFlowRate > 0) {
            result = Math.min(result, maxFlowRate);
        }
        return (float) Math.max(1.0, result);
    }

    public static FastPipesConfig load(Path file) throws IOException {
        lastFile = file;
        current = read(file);
        return current;
    }

    public static FastPipesConfig reload() throws IOException {
        if (lastFile == null) {
            throw new IllegalStateException("Config has not been loaded yet");
        }
        return load(lastFile);
    }

    private static FastPipesConfig read(Path file) throws IOException {
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(DEFAULT));
            return DEFAULT;
        }
        try {
            FastPipesConfig parsed = GSON.fromJson(Files.readString(file), FastPipesConfig.class);
            return parsed == null ? DEFAULT : parsed.sanitized();
        } catch (JsonParseException e) {
            FastPipes.LOGGER.warn("{} is not valid JSON, using defaults: {}", file, e.getMessage());
            return DEFAULT;
        }
    }

    private FastPipesConfig sanitized() {
        double multiplier = flowRateMultiplier;
        if (!(multiplier > 0) || !Double.isFinite(multiplier)) {
            multiplier = DEFAULT.flowRateMultiplier;
        }
        return new FastPipesConfig(multiplier, Math.max(0, maxFlowRate));
    }
}
