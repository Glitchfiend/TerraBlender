/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
    public static ChunkGenerator overworldChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.OVERWORLD));
    }

    public static ChunkGenerator largeBiomesChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.LARGE_BIOMES));
    }

    public static ChunkGenerator amplifiedChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        return chunkGenerator(dynamicRegistries, seed, () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.AMPLIFIED));
    }

    public static ChunkGenerator netherChunkGenerator(RegistryAccess dynamicRegistries, long seed)
    {
        Supplier<NoiseGeneratorSettings> noiseGeneratorSettingsSupplier = () -> dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(TBNoiseGeneratorSettings.NETHER);
        return new TBNoiseBasedChunkGenerator(dynamicRegistries.registryOrThrow(Registry.NOISE_REGISTRY), TBMultiNoiseBiomeSource.Preset.NETHER.biomeSource(dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY), false), seed, noiseGeneratorSettingsSupplier);
    }

    public static WorldGenSettings settings(RegistryAccess dynamicRegistries, long seed, boolean generateFeatures, boolean bonusChest, MappedRegistry<LevelStem> dimensions, ChunkGenerator chunkGenerator)
    {
        Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        return new WorldGenSettings(seed, generateFeatures, bonusChest, WorldGenSettings.withOverworld(dimensionTypeRegistry, dimensions, chunkGenerator));
    }

    public static ChunkGenerator chunkGenerator(RegistryAccess dynamicRegistries, long seed, Supplier<NoiseGeneratorSettings> noiseGeneratorSettingsSupplier)
    {
        return new TBNoiseBasedChunkGenerator(dynamicRegistries.registryOrThrow(Registry.NOISE_REGISTRY), TBMultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY), false), seed, noiseGeneratorSettingsSupplier);
    }

    public static MappedRegistry<LevelStem> dimensions(RegistryAccess dynamicRegistries, long seed)
    {
        MappedRegistry<LevelStem> defaultDimensions = DimensionType.defaultDimensions(dynamicRegistries, seed);
        Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);

        if (TerraBlender.CONFIG.replaceDefaultNether)
        {
            defaultDimensions.register(LevelStem.NETHER, new LevelStem(
                () -> dimensionTypeRegistry.getOrThrow(DimensionType.NETHER_LOCATION),
                WorldPresetUtils.netherChunkGenerator(dynamicRegistries, seed)
            ), Lifecycle.stable());
        }

        return defaultDimensions;
    }
}
