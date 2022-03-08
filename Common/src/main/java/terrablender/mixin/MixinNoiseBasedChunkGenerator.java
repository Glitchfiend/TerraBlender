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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
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
import terrablender.api.Regions;
import terrablender.util.RegistryUtils;
import terrablender.worldgen.IExtendedBiomeSource;
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
            IExtendedBiomeSource biomeSourceEx = (IExtendedBiomeSource)biomeSource;
            Climate.ParameterList parameters = multiNoiseBiomeSource.parameters;
            IExtendedParameterList parametersEx = (IExtendedParameterList)parameters;

            final RegionType regionType;
            if (settings.value().defaultBlock().getBlock() == Blocks.NETHERRACK && settings.value().defaultFluid().getBlock() == Blocks.LAVA)
                regionType = RegionType.NETHER;
            else
                regionType = RegionType.OVERWORLD;

            // TODO: Figure out how to manage region sizes/make them configurable
            // TODO: Fix the config file in general
            // TODO: Surface rules

            // Initialize the parameter list for TerraBlender
            parametersEx.initializeForTerraBlender(regionType, RegionSize.MEDIUM, seed);

            // Append modded biomes to the biome source biome list
            RegistryUtils.addRegistryAccessCaptureOneShotListener(registryAccess -> {
                Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
                ImmutableList.Builder<Holder<Biome>> builder = ImmutableList.builder();
                Regions.get(regionType).forEach(region -> region.addBiomes(biomeRegistry, pair -> builder.add(biomeRegistry.getOrCreateHolder(pair.getSecond()))));
                biomeSourceEx.appendDeferredBiomesList(builder.build());
            });
        }
    }
}
