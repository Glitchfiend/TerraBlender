/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.worldgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import terrablender.api.BiomeStructures;
import terrablender.data.TBCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TBStructureSettings extends StructureSettings
{
    public TBStructureSettings(Optional<StrongholdConfiguration> strongholdConfiguration, Map<StructureFeature<?>, StructureFeatureConfiguration> structureConfig)
    {
        super(strongholdConfiguration, structureConfig);

        HashMap<StructureFeature<?>, ImmutableMultimap.Builder<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> structureBiomes = new HashMap<>();
        BiomeStructures.StructureMapper mapper = (configuredStructureFeature, biome) ->
        {
            structureBiomes.computeIfAbsent(configuredStructureFeature.feature, (feature) -> ImmutableMultimap.builder()).put(configuredStructureFeature, biome);
        };

        StructureFeatures.registerStructures(mapper);
        BiomeStructures.registerStructures(mapper);

        this.configuredStructures = structureBiomes.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> entry.getValue().build()));
    }

    public TBStructureSettings(boolean enableStrongholds)
    {
        this(enableStrongholds ? Optional.of(DEFAULT_STRONGHOLD) : Optional.empty(), Maps.newHashMap(DEFAULTS));
    }
}
