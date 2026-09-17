package com.aruloci.fastpipes.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.aruloci.fastpipes.FastPipesConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(targets = "com.simibubi.create.content.fluids.FluidNetwork", remap = false)
public class FluidNetworkMixin {

    // transferSpeed is only recomputed when a network resets, so scale it where tick() reads it
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/fluids/FluidNetwork;transferSpeed:I",
            opcode = Opcodes.GETFIELD,
            remap = false
        )
    )
    private int fastpipes$scaleTransferSpeed(int original) {
        return (int) FastPipesConfig.current().apply(original);
    }
}
