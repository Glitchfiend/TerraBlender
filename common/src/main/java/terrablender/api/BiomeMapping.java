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
package terrablender.api;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.worldgen.RegionUtils;

import java.util.List;

public record BiomeMapping(Climate.ParameterPoint parameters, ResourceKey<Biome> biome)
{
    public static final Codec<BiomeMapping> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(BiomeMapping::parameters),
                    ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeMapping::biome)
            ).apply(instance, BiomeMapping::new));

    public static List<BiomeMapping> addBiomeSimilar(ResourceKey<Biome> similarVanillaBiome, ResourceKey<Biome> biome)
    {
        List<Climate.ParameterPoint> points = RegionUtils.getVanillaParameterPoints(similarVanillaBiome).stream().collect(ImmutableList.toImmutableList());
        return points.stream().map(point -> new BiomeMapping(point, biome)).collect(ImmutableList.toImmutableList());
    }
}
