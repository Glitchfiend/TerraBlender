/*
 * Copyright (C) Glitchfiend
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.api;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import terrablender.core.TerraBlender;
import terrablender.worldgen.TBMultiNoiseBiomeSource;
import terrablender.worldgen.TBNoiseBasedChunkGenerator;
import terrablender.worldgen.TBNoiseGeneratorSettings;

import java.util.function.Supplier;

public class WorldPresetUtils
{
    /**
     * Creates a new chunk generator for the overworld.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @return an overworld chunk generator.
     */
    public static ChunkGenerator overworldChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.OVERWORLD));
    }

    /**
     * Creates a new overworld chunk generator with large biomes.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @return a large biomes chunk generator.
     */
    public static ChunkGenerator largeBiomesChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.LARGE_BIOMES));
    }

    /**
     * Creates a new overworld chunk generator with amplified generation.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @return an amplified chunk generator.
     */
    public static ChunkGenerator amplifiedChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.AMPLIFIED));
    }

    /**
     * Creates a new chunk generator for the nether.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @return a nether chunk generator.
     */
    public static ChunkGenerator netherChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        Supplier<NoiseGeneratorSettings> noiseGeneratorSettingsSupplier = () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.NETHER);
        return new TBNoiseBasedChunkGenerator(dynamicRegistries.registryOrThrow(Registry.NOISE_REGISTRY), TBMultiNoiseBiomeSource.Preset.NETHER.biomeSource(dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY), false), seed, noiseGeneratorSettingsSupplier);
    }

    /**
     * Creates a new {@link WorldGenSettings} for the overworld.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @param generateFeatures whether to generate structures.
     * @param bonusChest whether to generate a bonus chest.
     * @param dimensions the dimension registry.
     * @param chunkGenerator the chunk generator.
     * @return a new {@link WorldGenSettings}.
     */
    public static WorldGenSettings settings(RegistryAccess dynamicRegistries, long seed, boolean generateFeatures, boolean bonusChest, MappedRegistry<LevelStem> dimensions, ChunkGenerator chunkGenerator)
    {
        Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        return new WorldGenSettings(seed, generateFeatures, bonusChest, WorldGenSettings.withOverworld(dimensionTypeRegistry, dimensions, chunkGenerator));
    }

    /**
     * Creates a new {@link ChunkGenerator}.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @param noiseGeneratorSettingsSupplier a supplier of {@link NoiseGeneratorSettings}.
     * @return a new {@link ChunkGenerator}.
     */
    public static ChunkGenerator chunkGenerator(RegistryAccess dynamicRegistries, long seed, Supplier<NoiseGeneratorSettings> noiseGeneratorSettingsSupplier)
    {
        return new TBNoiseBasedChunkGenerator(dynamicRegistries.registryOrThrow(Registry.NOISE_REGISTRY), TBMultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY), false), seed, noiseGeneratorSettingsSupplier);
    }

    /**
     * Creates the {@link MappedRegistry} comprising all the dimensions to be used.
     * @param dynamicRegistries the registry access.
     * @param seed the world seed.
     * @return the dimension registry.
     */
    public static MappedRegistry<LevelStem> dimensions(RegistryAccess dynamicRegistries, long seed)
    {
        MappedRegistry<LevelStem> dimensions = DimensionType.defaultDimensions(dynamicRegistries, seed);
        Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);

        if (TerraBlender.CONFIG.replaceDefaultNether)
        {
            MappedRegistry<LevelStem> newDimensions = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
            dimensions.entrySet().stream().filter(entry -> entry.getKey() != LevelStem.NETHER).forEach(entry -> newDimensions.register(entry.getKey(), entry.getValue(), Lifecycle.stable()));

            newDimensions.register(LevelStem.NETHER, new LevelStem(
                () -> dimensionTypeRegistry.getOrThrow(DimensionType.NETHER_LOCATION),
                WorldPresetUtils.netherChunkGenerator(dynamicRegistries, seed)
            ), Lifecycle.stable());

            dimensions = newDimensions;
        }

        return dimensions;
    }
}
