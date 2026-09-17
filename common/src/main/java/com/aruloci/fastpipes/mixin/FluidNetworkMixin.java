package com.aruloci.fastpipes.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.aruloci.fastpipes.FastPipesConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

/**
 * Create stores a pipe network's throughput in {@code FluidNetwork.transferSpeed}
 * ({@code max(1, pumpPressure / 2)} mB/tick, computed only when the network forms or resets).
 * {@code tick()} reads that field exactly once, {@code int flowSpeed = transferSpeed;}, right
 * before draining the source. Scaling the value at that read means a config reload takes effect
 * on the next tick for every existing network, and Create's own bookkeeping stays untouched.
 * Targeted by name so {@code common} needs no Create jar on its classpath.
 */
@Mixin(targets = "com.simibubi.create.content.fluids.FluidNetwork", remap = false)
abstract class FluidNetworkMixin {

    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/fluids/FluidNetwork;transferSpeed:I",
            opcode = Opcodes.GETFIELD,
            remap = false
        )
    )
    private int createFastPipes$scaleTransferSpeed(int original) {
        return (int) FastPipesConfig.current().apply(original);
    }
}
