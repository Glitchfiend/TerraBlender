/**
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
package terrablender.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import terrablender.worldgen.*;
import terrablender.api.WorldPresetUtils;
import terrablender.core.TerraBlender;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DataPackManager
{
    public static final ResourceLocation DATA_PACK_PROVIDER_LOCATION = new ResourceLocation("datapack:biome_provider");

    private static final Codec<WorldGenSettings> DIRECT_WGS_CODEC = RecordCodecBuilder.<WorldGenSettings>create((p_64626_) -> {
        return p_64626_.group(Codec.LONG.fieldOf("seed").stable().forGetter(WorldGenSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(WorldGenSettings::generateFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(WorldGenSettings::generateBonusChest), MappedRegistry.directCodec(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), LevelStem.CODEC).xmap(LevelStem::sortMap, Function.identity()).fieldOf("dimensions").forGetter(WorldGenSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((p_158959_) -> {
            return p_158959_.legacyCustomOptions;
        })).apply(p_64626_, p_64626_.stable(WorldGenSettings::new));
    }).comapFlatMap(WorldGenSettings::guardExperimental, Function.identity());

    public static WorldGenSettings mergeWorldGenSettings(RegistryAccess registryAccess, WorldGenSettings currentSettings, WorldGenSettings newSettings)
    {
        if (!shouldAttemptMerge(newSettings))
            return newSettings;

        boolean shouldMergeOverworld = shouldMergeStem(LevelStem.OVERWORLD, newSettings);
        boolean shouldMergeNether = shouldMergeStem(LevelStem.NETHER, newSettings);
        int overworldWeight = shouldMergeOverworld ? TerraBlender.CONFIG.datapackOverworldRegionWeight : 0;
        int netherWeight = shouldMergeNether && TerraBlender.CONFIG.replaceDefaultNether ? TerraBlender.CONFIG.datapackNetherRegionWeight : 0;

        BiomeProvider dataPackBiomeProvider = new DataPackBiomeProvider(DATA_PACK_PROVIDER_LOCATION, overworldWeight, netherWeight, newSettings);
        BiomeProviders.register(DATA_PACK_PROVIDER_LOCATION, dataPackBiomeProvider);

        Registry<DimensionType> dimensionTypeRegistry = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        MappedRegistry<LevelStem> dimensions = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());

        // Construct a new dimensions registry
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : newSettings.dimensions().entrySet())
        {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();

            if (key == LevelStem.OVERWORLD && shouldMergeOverworld)
            {
                stem = new LevelStem(
                    () -> dimensionTypeRegistry.getOrThrow(DimensionType.OVERWORLD_LOCATION),
                    createdMergedChunkGenerator(LevelStem.OVERWORLD, registryAccess, currentSettings, newSettings, dataPackBiomeProvider, provider -> BiomeProviderUtils.createOverworldRules(provider.getOverworldSurfaceRules().get()), TBNoiseGeneratorSettings::overworld, TBMultiNoiseBiomeSource.Preset.OVERWORLD)
                );
            }
            else if (key == LevelStem.NETHER && shouldMergeNether)
            {
                stem = new LevelStem(
                    () -> dimensionTypeRegistry.getOrThrow(DimensionType.NETHER_LOCATION),
                    createdMergedChunkGenerator(LevelStem.NETHER, registryAccess, currentSettings, newSettings, dataPackBiomeProvider, provider -> BiomeProviderUtils.createNetherRules(provider.getNetherSurfaceRules().get()), TBNoiseGeneratorSettings::nether, TBMultiNoiseBiomeSource.Preset.NETHER)
                );
            }

            dimensions.register(key, stem, Lifecycle.stable());
        }

        TerraBlender.LOGGER.info("Merged generation settings with datapack");
        return new WorldGenSettings(currentSettings.seed(), currentSettings.generateFeatures(), currentSettings.generateBonusChest(), dimensions);
    }

    public static <T> DataResult replaceDatapackWorldGenSettings(Dynamic<T> dynamicWorldGenSettings)
    {
        DataResult<WorldGenSettings> directWorldGenSettingsResult = DIRECT_WGS_CODEC.parse(dynamicWorldGenSettings);
        DataResult<WorldGenSettings> dataPackedWorldGenSettingsResult = WorldGenSettings.CODEC.parse(dynamicWorldGenSettings);
        Optional<WorldGenSettings> directWorldGenSettingsOptional = directWorldGenSettingsResult.result();
        Optional<WorldGenSettings> dataPackedWorldGenSettingsOptional = dataPackedWorldGenSettingsResult.result();

        if (directWorldGenSettingsOptional.isPresent() && (dataPackedWorldGenSettingsOptional.isEmpty() || shouldAttemptMerge(dataPackedWorldGenSettingsOptional.get())))
        {
            if (dataPackedWorldGenSettingsOptional.isPresent())
            {
                TerraBlender.LOGGER.info("Using merged world generation settings");
                WorldGenSettings datapackSettings = dataPackedWorldGenSettingsOptional.get();
                int overworldWeight = shouldMergeStem(LevelStem.OVERWORLD, datapackSettings) ? TerraBlender.CONFIG.datapackOverworldRegionWeight : 0;
                int netherWeight = shouldMergeStem(LevelStem.NETHER, datapackSettings) && TerraBlender.CONFIG.replaceDefaultNether ? TerraBlender.CONFIG.datapackNetherRegionWeight : 0;
                BiomeProviders.register(new DataPackBiomeProvider(DATA_PACK_PROVIDER_LOCATION, overworldWeight, netherWeight, directWorldGenSettingsOptional.get()));
            }
            else TerraBlender.LOGGER.info("Using direct world generation settings without merging");

            return directWorldGenSettingsResult;
        }
        else
        {
            return dataPackedWorldGenSettingsResult;
        }
    }

    private static ChunkGenerator createdMergedChunkGenerator(ResourceKey<LevelStem> key, RegistryAccess registryAccess, WorldGenSettings currentSettings, WorldGenSettings newSettings, BiomeProvider biomeProvider, Function<BiomeProvider, SurfaceRules.RuleSource> getSurfaceRules, BiFunction<NoiseSettings, SurfaceRules.RuleSource, NoiseGeneratorSettings> createNoiseGeneratorSettings, TBMultiNoiseBiomeSource.Preset preset)
    {
        ChunkGenerator newChunkGenerator = chunkGeneratorForStem(key, newSettings);

        if (newChunkGenerator == null)
            throw new IllegalStateException("Attempted to merge chunk generator for missing level stem");

        // We can't merge new chunk generators that aren't NoiseBasedChunkGenerators
        if (!(newChunkGenerator instanceof NoiseBasedChunkGenerator))
            return newChunkGenerator;

        NoiseBasedChunkGenerator newNoiseBasedChunkGenerator = (NoiseBasedChunkGenerator)newChunkGenerator;
        NoiseGeneratorSettings newNoiseGeneratorSettings = newNoiseBasedChunkGenerator.settings.get();

        // Create the merged surface rules
        SurfaceRules.RuleSource surfaceRules = getSurfaceRules.apply(biomeProvider);

        // Create the merged noise generator settings
        NoiseGeneratorSettings mergedNoiseGeneratorSettings = createNoiseGeneratorSettings.apply(newNoiseGeneratorSettings.noiseSettings(), surfaceRules);

        // Finally, create the new chunk generator.
        return new TBNoiseBasedChunkGenerator(registryAccess.registryOrThrow(Registry.NOISE_REGISTRY), preset.biomeSource(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), false), currentSettings.seed(), () -> mergedNoiseGeneratorSettings);
    }

    private static boolean shouldAttemptMerge(WorldGenSettings settings)
    {
        return shouldMergeStem(LevelStem.OVERWORLD, settings) || shouldMergeStem(LevelStem.NETHER, settings);
    }

    private static boolean shouldMergeStem(ResourceKey<LevelStem> key, WorldGenSettings settings)
    {
        ChunkGenerator generator = chunkGeneratorForStem(key, settings);
        return generator != null && (generator.getBiomeSource() instanceof MultiNoiseBiomeSource) && !(generator.getBiomeSource() instanceof TBMultiNoiseBiomeSource);
    }

    private static ChunkGenerator chunkGeneratorForStem(ResourceKey<LevelStem> key, WorldGenSettings settings)
    {
        LevelStem stem = settings.dimensions().get(key);
        return stem == null ? null : stem.generator();
    }
}
