/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import terrablender.worldgen.DataPackBiomeProvider;
import terrablender.api.WorldPresetUtils;
import terrablender.core.TerraBlender;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBNoiseBasedChunkGenerator;
import terrablender.worldgen.TBNoiseGeneratorSettings;

import java.util.Optional;
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
        // Do not merge if the chunk generator isn't ours or the new settings don't use a MultiNoiseBiomeSource
        if (!(currentSettings.overworld() instanceof TBNoiseBasedChunkGenerator) || !(newSettings.overworld().getBiomeSource() instanceof MultiNoiseBiomeSource))
        {
            return newSettings;
        }

        BiomeProvider dataPackBiomeProvider = new DataPackBiomeProvider(DATA_PACK_PROVIDER_LOCATION, TerraBlender.CONFIG.datapackRegionWeight, newSettings);
        BiomeProviders.register(DATA_PACK_PROVIDER_LOCATION, dataPackBiomeProvider);

        NoiseBasedChunkGenerator newOverworldChunkGenerator = (NoiseBasedChunkGenerator)newSettings.overworld();
        NoiseGeneratorSettings newNoiseGeneratorSettings = newOverworldChunkGenerator.settings.get();

        SurfaceRules.RuleSource mergedOverworldRuleSource = BiomeProviderUtils.createOverworldRules(dataPackBiomeProvider.getOverworldSurfaceRules().get());
        NoiseGeneratorSettings mergedNoiseGeneratorSettings = TBNoiseGeneratorSettings.overworld(newNoiseGeneratorSettings.noiseSettings(), mergedOverworldRuleSource);
        ChunkGenerator mergedChunkGenerator = WorldPresetUtils.chunkGenerator(registryAccess, currentSettings.seed(), () -> mergedNoiseGeneratorSettings);

        TerraBlender.LOGGER.info("Merged generation settings with datapack");
        return WorldPresetUtils.settings(registryAccess, currentSettings.seed(), currentSettings.generateFeatures(), currentSettings.generateBonusChest(), currentSettings.dimensions(), mergedChunkGenerator);
    }

    public static <T> DataResult replaceDatapackWorldGenSettings(Dynamic<T> dynamicWorldGenSettings)
    {
        DataResult<WorldGenSettings> directWorldGenSettingsResult = DIRECT_WGS_CODEC.parse(dynamicWorldGenSettings);
        DataResult<WorldGenSettings> dataPackedWorldGenSettingsResult = WorldGenSettings.CODEC.parse(dynamicWorldGenSettings);
        Optional<WorldGenSettings> directWorldGenSettingsOptional = directWorldGenSettingsResult.result();
        Optional<WorldGenSettings> dataPackedWorldGenSettingsOptional = dataPackedWorldGenSettingsResult.result();
        
        if (directWorldGenSettingsOptional.isPresent() && directWorldGenSettingsOptional.get().overworld() instanceof TBNoiseBasedChunkGenerator && (dataPackedWorldGenSettingsOptional.isEmpty() || !(dataPackedWorldGenSettingsOptional.get().overworld() instanceof TBNoiseBasedChunkGenerator)))
        {
            TerraBlender.LOGGER.info("Using merged world generation settings for datapack");

            // Register the datapack provider if we're dealing with a datapack
            if (dataPackedWorldGenSettingsOptional.isPresent())
            {
                BiomeProviders.register(DATA_PACK_PROVIDER_LOCATION, new DataPackBiomeProvider(DATA_PACK_PROVIDER_LOCATION, TerraBlender.CONFIG.datapackRegionWeight, directWorldGenSettingsOptional.get()));
            }

            return directWorldGenSettingsResult;
        }
        else
        {
            return dataPackedWorldGenSettingsResult;
        }
    }
}
