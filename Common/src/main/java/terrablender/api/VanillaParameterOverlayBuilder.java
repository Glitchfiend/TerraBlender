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

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/***
 * This builder is designed for adding {@link Climate.ParameterPoint} to {@link net.minecraft.resources.ResourceKey<Biome>} mappings to be layered on top of Vanilla's default mappings.
 * Accordingly, any modded points will take precedence over those added by Vanilla.
 */
public class VanillaParameterOverlayBuilder
{
    private Map<Climate.ParameterPoint, ResourceKey<Biome>> mappings = Maps.newHashMap();

    /**
     * Adds a {@link Climate.ParameterPoint} to {@link net.minecraft.resources.ResourceKey<Biome>} mapping.
     * @param point the parameter point.
     * @param biome the biome associated with the parameter point.
     */
    public void add(Climate.ParameterPoint point, ResourceKey<Biome> biome)
    {
        this.mappings.put(point, biome);
    }

    /**
     * Builds a list of {@link Climate.ParameterPoint} and {@link ResourceKey <Biome>} pairs.
     * @return the built list.
     */
    public List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> build()
    {
        Set<Climate.ParameterPoint> standalonePoints = Sets.newHashSet(this.mappings.keySet());
        SetMultimap<Adjacency, Climate.ParameterPoint> adjacentPoints = HashMultimap.create();

        permuteMappings((a, b) ->
        {
            if (a.equals(b))
                return;

            for (Adjacency adjacency : Adjacency.values())
            {
                if (adjacency.isAdjacent(a, b))
                {
                    adjacentPoints.put(adjacency, a);
                    adjacentPoints.put(adjacency, b);
                    standalonePoints.remove(a);
                    standalonePoints.remove(b);
                }
            }
        });

        ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> outBuilder = ImmutableList.builder();

        // Add the original mappings
        outBuilder.addAll(this.mappings.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList());

        // Add the standalone points
        if (!standalonePoints.isEmpty())
        {
            outBuilder.addAll(standalonePoints.stream().flatMap(point -> inversePoints(List.of(point)).stream().map(inversePoint -> Pair.of(inversePoint, Region.DEFERRED_PLACEHOLDER))).toList());
        }

        // Add the adjacent points
        for (Adjacency adjacency : adjacentPoints.keySet())
        {
            Set<Climate.ParameterPoint> values = adjacentPoints.get(adjacency);

            if (values.isEmpty())
                continue;

            outBuilder.addAll(inversePoints(List.copyOf(values)).stream().map(point -> Pair.of(point, Region.DEFERRED_PLACEHOLDER)).toList());
        }

        return outBuilder.build();
    }

    private void permuteMappings(BiConsumer<Climate.ParameterPoint, Climate.ParameterPoint> consumer)
    {
        var entries = this.mappings.entrySet().stream().toList();

        if (entries.size() == 0)
        {
            throw new RuntimeException("Need at least one entry to permute!");
        }
        else if (entries.size() == 1)
        {
            var point = entries.get(0).getKey();
            consumer.accept(point, point);
        }
        else
        {
            for (int i = 0; i < entries.size(); i++)
            {
                Climate.ParameterPoint pointA = entries.get(i).getKey();
                for (int j = i; j < entries.size(); j++)
                {
                    consumer.accept(pointA, entries.get(j).getKey());
                }
            }
        }
    }

    private static List<Climate.ParameterPoint> inversePoints(List<Climate.ParameterPoint> values)
    {
        List<Climate.Parameter> temperatures = Lists.newArrayList();
        List<Climate.Parameter> humidities = Lists.newArrayList();
        List<Climate.Parameter> continentalnesses = Lists.newArrayList();
        List<Climate.Parameter> erosions = Lists.newArrayList();
        List<Climate.Parameter> depths = Lists.newArrayList();
        List<Climate.Parameter> weirdnesses = Lists.newArrayList();

        for (Climate.ParameterPoint point : values)
        {
            temperatures.add(point.temperature());
            humidities.add(point.humidity());
            continentalnesses.add(point.continentalness());
            erosions.add(point.erosion());
            depths.add(point.depth());
            weirdnesses.add(point.weirdness());
        }

        temperatures.sort(Comparator.comparing(Climate.Parameter::min));
        humidities.sort(Comparator.comparing(Climate.Parameter::min));
        continentalnesses.sort(Comparator.comparing(Climate.Parameter::min));
        erosions.sort(Comparator.comparing(Climate.Parameter::min));
        depths.sort(Comparator.comparing(Climate.Parameter::min));
        weirdnesses.sort(Comparator.comparing(Climate.Parameter::min));

        return new ParameterUtils.ParameterPointListBuilder()
                .temperature(inverseParameters(temperatures).toArray(Climate.Parameter[]::new))
                .humidity(inverseParameters(humidities).toArray(Climate.Parameter[]::new))
                .continentalness(inverseParameters(continentalnesses).toArray(Climate.Parameter[]::new))
                .erosion(inverseParameters(erosions).toArray(Climate.Parameter[]::new))
                .depth(inverseParameters(depths).toArray(Climate.Parameter[]::new))
                .weirdness(inverseParameters(weirdnesses).toArray(Climate.Parameter[]::new))
                .build();
    }

    private static List<Climate.Parameter> inverseParameters(List<Climate.Parameter> values)
    {
        if (values.isEmpty())
            return List.of(Climate.Parameter.span(-1.0F, 1.0F));

        List<Climate.Parameter> out = Lists.newArrayList();
        float prevMax = -1.0F;

        for (Climate.Parameter value : values)
        {
            float min = Climate.unquantizeCoord(value.min());
            float max = Climate.unquantizeCoord(value.max());
            if (min - prevMax > 0.0F)
            {
                out.add(Climate.Parameter.span(prevMax, min));
            }
            prevMax = max;
        }

        if (prevMax < 1.0F)
        {
            out.add(Climate.Parameter.span(prevMax, 1.0F));
        }

        return out;
    }

    private enum Adjacency
    {
        TEMPERATURE(Climate.ParameterPoint::temperature),
        HUMIDITY(Climate.ParameterPoint::humidity),
        CONTINENTALNESS(Climate.ParameterPoint::continentalness),
        EROSION(Climate.ParameterPoint::erosion),
        DEPTH(Climate.ParameterPoint::depth),
        WEIRDNESS(Climate.ParameterPoint::weirdness);

        Function<Climate.ParameterPoint, Climate.Parameter> getter;

        Adjacency(Function<Climate.ParameterPoint, Climate.Parameter> getter)
        {
            this.getter = getter;
        }

        public Climate.Parameter getParameter(Climate.ParameterPoint point)
        {
            return this.getter.apply(point);
        }

        public boolean isAdjacent(Climate.ParameterPoint a, Climate.ParameterPoint b)
        {
            for (Adjacency adjacency : Adjacency.values())
            {
                Climate.Parameter paramA = adjacency.getParameter(a);
                Climate.Parameter paramB = adjacency.getParameter(b);

                if (adjacency == this && paramA.equals(paramB))
                {
                    return false;
                }
                else if (adjacency != this && !paramA.equals(paramB))
                {
                    return false;
                }
            }

            return true;
        }
    }
}
