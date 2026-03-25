/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.mixin;

import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator
{
    @Inject(method = "validate", at = @At("HEAD"), cancellable = true, remap = false)
    public void validate(CallbackInfo ci)
    {
        // Do not perform validation - this causes featuresPerStep to be resolved before modded biomes have been
        // injected.
        ci.cancel();
    }
}
