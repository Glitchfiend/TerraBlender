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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

import java.util.List;
import java.util.Map;

public class RegionUtils
{
    private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> VANILLA_POINTS;

    private static Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> biomeParameterPointCache = Maps.newHashMap();

    public static List<Climate.ParameterPoint> getVanillaParameterPoints(ResourceKey<Biome> biome)
    {
        if (biomeParameterPointCache.containsKey(biome))
            return biomeParameterPointCache.get(biome);

        List<Climate.ParameterPoint> points = VANILLA_POINTS.stream().filter(pair -> pair.getSecond() == biome).map(pair -> pair.getFirst()).collect(ImmutableList.toImmutableList());
        biomeParameterPointCache.put(biome, points);
        return points;
    }

    static
    {
        ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = new ImmutableList.Builder();
        (new OverworldBiomeBuilder()).addBiomes(pair -> builder.add(pair));
        VANILLA_POINTS = builder.build();
    }
}
