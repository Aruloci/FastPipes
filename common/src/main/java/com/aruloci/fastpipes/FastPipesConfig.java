package com.aruloci.fastpipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Server-side settings controlling Create pipe throughput.
 *
 * @param flowRateMultiplier factor applied to Create's own {@code pumpRPM / 2} mB/tick
 * @param maxFlowRate        upper bound in mB/tick after scaling; {@code 0} means no cap
 */
public record FastPipesConfig(double flowRateMultiplier, int maxFlowRate) {

    public static final FastPipesConfig DEFAULT = new FastPipesConfig(1.0, 0);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static volatile FastPipesConfig current = DEFAULT;
    private static volatile Path lastFile;

    /** The active config; never null. Read by the mixin every tick, so keep it a plain field read. */
    public static FastPipesConfig current() {
        return current;
    }

    /** Scales Create's computed transfer speed (mB/tick). Result is never below 1. */
    public float apply(float original) {
        double result = original * flowRateMultiplier;
        if (maxFlowRate > 0) {
            result = Math.min(result, maxFlowRate);
        }
        return (float) Math.max(1.0, result);
    }

    /**
     * Reads the config from {@code file}, writing defaults there first if it does not exist.
     * A malformed file is left untouched and defaults are returned so the admin can fix it.
     */
    public static FastPipesConfig load(Path file) throws IOException {
        lastFile = file;
        current = read(file);
        return current;
    }

    /** Re-reads the file given to the last {@link #load(Path)} call. */
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

    /** Replaces out-of-range values with their defaults. Missing JSON fields arrive here as 0. */
    private FastPipesConfig sanitized() {
        double multiplier = flowRateMultiplier > 0 && Double.isFinite(flowRateMultiplier)
            ? flowRateMultiplier
            : DEFAULT.flowRateMultiplier;
        int cap = Math.max(0, maxFlowRate);
        return new FastPipesConfig(multiplier, cap);
    }
}
