/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.mixin.access;

import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BiomeManager.class)
public interface BiomeManagerAccess
{
    @Accessor
    long getBiomeZoomSeed();

    @Invoker(value = "getFiddledDistance")
    static double getFiddledDistance(long seed, int p_186681_, int p_186682_, int p_186683_, double p_186684_, double p_186685_, double p_186686_)
    {
        throw new Error("Mixin did not apply");
    }
}
