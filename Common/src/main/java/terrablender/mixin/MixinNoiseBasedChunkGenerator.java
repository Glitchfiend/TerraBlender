/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.api.RegionSize;
import terrablender.api.RegionType;
import terrablender.worldgen.IExtendedParameterList;

@Mixin(NoiseBasedChunkGenerator.class)
public class MixinNoiseBasedChunkGenerator
{
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Registry<StructureSet> structures, Registry<NormalNoise.NoiseParameters> noises, BiomeSource biomeSource, long seed, Holder<NoiseGeneratorSettings> settings, CallbackInfo ci)
    {
        if (biomeSource instanceof MultiNoiseBiomeSource)
        {
            MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)biomeSource;
            Climate.ParameterList parameters = multiNoiseBiomeSource.parameters;
            IExtendedParameterList parametersEx = (IExtendedParameterList)parameters;

            RegionType regionType = RegionType.OVERWORLD;

            if (settings.value().defaultBlock().getBlock() == Blocks.NETHERRACK && settings.value().defaultFluid().getBlock() == Blocks.LAVA)
                regionType = RegionType.NETHER;

            // TODO: MultiNoiseBiomeSource possibleValues
            // TODO: Figure out how to manage region sizes/make them configurable
            // TODO: Fix the config file in general
            parametersEx.initializeForTerraBlender(regionType, RegionSize.MEDIUM, seed);
        }
    }
}
