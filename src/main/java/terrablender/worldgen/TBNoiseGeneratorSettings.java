/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.worldgen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import terrablender.api.GenerationSettings;
import terrablender.core.TerraBlender;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class TBNoiseGeneratorSettings
{
    public static final Codec<NoiseGeneratorSettings> DIRECT_CODEC = RecordCodecBuilder.create((p_64475_) -> {
        return p_64475_.group(TBStructureSettings.CODEC.fieldOf("structures").forGetter(NoiseGeneratorSettings::structureSettings), NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings), BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::getDefaultBlock), BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::getDefaultFluid), SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(NoiseGeneratorSettings::surfaceRule), Codec.INT.fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration), Codec.BOOL.fieldOf("aquifers_enabled").forGetter(NoiseGeneratorSettings::isAquifersEnabled), Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(NoiseGeneratorSettings::isNoiseCavesEnabled), Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(NoiseGeneratorSettings::isOreVeinsEnabled), Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(NoiseGeneratorSettings::isNoodleCavesEnabled), Codec.BOOL.fieldOf("legacy_random_source").forGetter(NoiseGeneratorSettings::useLegacyRandomSource)).apply(p_64475_, NoiseGeneratorSettings::new);
    });
    public static final Codec<Supplier<NoiseGeneratorSettings>> CODEC = RegistryFileCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DIRECT_CODEC);

    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(TerraBlender.MOD_ID, "overworld"));
    public static final ResourceKey<NoiseGeneratorSettings> LARGE_BIOMES = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(TerraBlender.MOD_ID, "large_biomes"));
    public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(TerraBlender.MOD_ID, "amplified"));
    public static final ResourceKey<NoiseGeneratorSettings> NETHER = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(TerraBlender.MOD_ID, "nether"));

    public static NoiseSettings overworldNoiseSettings(boolean amplified, boolean largeBiomes)
    {
        return NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-0.078125D, 2, 8), new NoiseSlider(0.1171875D, 3, 0),
                1, 2, false, amplified, largeBiomes,
                TerrainProvider.overworld(amplified));
    }

    public static NoiseGeneratorSettings overworld(boolean amplified, boolean largeBiomes)
    {
        return overworld(overworldNoiseSettings(amplified, largeBiomes), BiomeProviderUtils.createOverworldRules());
    }

    public static NoiseGeneratorSettings overworld(NoiseSettings noiseSettings, SurfaceRules.RuleSource ruleSource)
    {
        return new NoiseGeneratorSettings(
                new TBStructureSettings(true),
                noiseSettings,
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                ruleSource,
                63, false, true, true, true, true, false);
    }

    public static NoiseSettings netherNoiseSettings()
    {
        return NoiseSettings.create(0, 128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, false, false, false, TerrainProvider.nether());
    }

    public static NoiseGeneratorSettings nether()
    {
        return nether(netherNoiseSettings(), BiomeProviderUtils.createNetherRules());
    }

    public static NoiseGeneratorSettings nether(NoiseSettings noiseSettings, SurfaceRules.RuleSource ruleSource)
    {
        Map<StructureFeature<?>, StructureFeatureConfiguration> map = Maps.newHashMap(StructureSettings.DEFAULTS);
        map.put(StructureFeature.RUINED_PORTAL, new StructureFeatureConfiguration(25, 10, 34222645));
        return new NoiseGeneratorSettings(new StructureSettings(Optional.empty(), map), noiseSettings, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), ruleSource, 32, false, false, false, false, false, true);
    }
}
