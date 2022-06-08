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
package terrablender.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import terrablender.DimensionTypeTags;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.core.TerraBlender;
import terrablender.worldgen.IExtendedBiomeSource;
import terrablender.worldgen.IExtendedChunkGenerator;
import terrablender.worldgen.IExtendedParameterList;

public class LevelUtils {
    public static void initializeBiomes(Holder<DimensionType> dimensionType, ResourceKey<LevelStem> levelResourceKey, ChunkGenerator chunkGenerator, long seed) {
        // Only apply to NoiseBasedChunkGenerator with MultiNoiseBiomeSources
        if (!(chunkGenerator instanceof NoiseBasedChunkGenerator) || !(chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return;
        }

        final RegionType regionType;
        if (dimensionType.is(DimensionTypeTags.NETHER_REGIONS)) regionType = RegionType.NETHER;
        else if (dimensionType.is(DimensionTypeTags.OVERWORLD_REGIONS)) regionType = RegionType.OVERWORLD;
        else regionType = null;

        NoiseBasedChunkGenerator noiseBasedChunkGenerator = (NoiseBasedChunkGenerator) chunkGenerator;
        MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource) chunkGenerator.getBiomeSource();
        IExtendedBiomeSource biomeSourceEx = (IExtendedBiomeSource) biomeSource;

        // Don't continue if region type is uninitialized
        if (regionType == null) {
            // We don't have any biomes to append to the list.
            biomeSourceEx.appendDeferredBiomesList(ImmutableList.of());
            return;
        }


        Climate.ParameterList parameters = biomeSource.parameters;
        IExtendedParameterList parametersEx = (IExtendedParameterList) parameters;

        // Initialize the parameter list for TerraBlender
        parametersEx.initializeForTerraBlender(regionType, seed);

        // Append modded biomes to the biome source biome list
        RegistryUtils.doWithRegistryAccess(registryAccess -> {
            Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
            ImmutableList.Builder<Holder<Biome>> builder = ImmutableList.builder();
            Regions.get(regionType).forEach(region -> region.addBiomes(biomeRegistry, pair -> builder.add(biomeRegistry.getHolderOrThrow(pair.getSecond()))));
            biomeSourceEx.appendDeferredBiomesList(builder.build());
        });

        ((IExtendedChunkGenerator) chunkGenerator).appendFeaturesPerStep();

        TerraBlender.LOGGER.info(String.format("Initialized terrablender biomes for Level Stem: %s", levelResourceKey.location()));

    }
}
