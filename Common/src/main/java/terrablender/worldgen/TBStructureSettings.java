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
