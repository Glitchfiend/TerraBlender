/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import terrablender.worldgen.TBMultiNoiseBiomeSource;
import terrablender.worldgen.TBNoiseBasedChunkGenerator;
import terrablender.worldgen.TBNoiseGeneratorSettings;

import java.util.function.Supplier;

public class WorldPresetUtils
{
    public static WorldGenSettings settings(RegistryAccess dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest, MappedRegistry<LevelStem> dimensions, ChunkGenerator chunkGenerator)
    {
        Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        return new WorldGenSettings(seed, generateStructures, bonusChest, WorldGenSettings.withOverworld(dimensionTypeRegistry, dimensions, chunkGenerator));
    }

    public static ChunkGenerator chunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.OVERWORLD));
    }

    public static ChunkGenerator chunkGenerator(RegistryAccess dynamicRegistries, long seed, Supplier<NoiseGeneratorSettings> noiseGeneratorSettingsSupplier)
    {
        return new TBNoiseBasedChunkGenerator(dynamicRegistries.registryOrThrow(Registry.NOISE_REGISTRY), TBMultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY), false), seed, noiseGeneratorSettingsSupplier);
    }
}
