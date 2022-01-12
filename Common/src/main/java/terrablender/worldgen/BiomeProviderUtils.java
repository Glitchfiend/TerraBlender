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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import terrablender.api.GenerationSettings;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class BiomeProviderUtils
{
    private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> VANILLA_POINTS;

    private static Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> biomeParameterPointCache = Maps.newHashMap();
    private static Map<Integer, Float> uniquenessMidPointCache = Maps.newHashMap();
    private static Map<Integer, Climate.Parameter> uniquenessParameterCache = Maps.newHashMap();

    public static SurfaceRules.RuleSource createOverworldRules(SurfaceRules.RuleSource base)
    {
        return createNamespacedRuleSource(base, provider -> provider.getOverworldSurfaceRules());
    }

    public static SurfaceRules.RuleSource createOverworldRules()
    {
        return createOverworldRules(GenerationSettings.getDefaultOverworldSurfaceRules());
    }

    public static SurfaceRules.RuleSource createNetherRules(SurfaceRules.RuleSource base)
    {
        return createNamespacedRuleSource(base, provider -> provider.getNetherSurfaceRules());
    }

    public static SurfaceRules.RuleSource createNetherRules()
    {
        return createNetherRules(GenerationSettings.getDefaultNetherSurfaceRules());
    }

    private static SurfaceRules.RuleSource createNamespacedRuleSource(SurfaceRules.RuleSource base, Function<BiomeProvider, Optional<SurfaceRules.RuleSource>> source)
    {
        return new NamespacedSurfaceRuleSource(base, ImmutableMap.copyOf(collectRuleSources(source)));
    }

    public static Climate.Parameter getUniquenessParameter(int index)
    {
        if (uniquenessParameterCache.containsKey(index))
            return uniquenessParameterCache.get(index);

        Climate.Parameter parameter = Climate.Parameter.point(Climate.unquantizeCoord(index));
        uniquenessParameterCache.put(index, parameter);
        return parameter;
    }

    public static List<Climate.ParameterPoint> getVanillaParameterPoints(ResourceKey<Biome> biome)
    {
        if (biomeParameterPointCache.containsKey(biome))
            return biomeParameterPointCache.get(biome);

        List<Climate.ParameterPoint> points = VANILLA_POINTS.stream().filter(pair -> pair.getSecond() == biome).map(pair -> pair.getFirst()).collect(ImmutableList.toImmutableList());
        biomeParameterPointCache.put(biome, points);
        return points;
    }

    public static List<TBClimate.ParameterPoint> getAllSpawnTargets()
    {
        ImmutableList.Builder<TBClimate.ParameterPoint> builder = new ImmutableList.Builder<>();
        BiomeProviders.get().forEach(provider -> builder.addAll(provider.getSpawnTargets()));
        return builder.build();
    }

    public static void addAllOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        BiomeProviders.get().forEach(provider -> provider.addOverworldBiomes(registry, mapper));
    }

    public static void addAllNetherBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        BiomeProviders.get().forEach(provider -> provider.addNetherBiomes(registry, mapper));
    }

    private static Map<String, SurfaceRules.RuleSource> collectRuleSources(Function<BiomeProvider, Optional<SurfaceRules.RuleSource>> rulesSource)
    {
        ImmutableMap.Builder<String, SurfaceRules.RuleSource> builder = new ImmutableMap.Builder();

        for (BiomeProvider provider : BiomeProviders.get())
        {
            Optional<SurfaceRules.RuleSource> rules = rulesSource.apply(provider);
            if (rules.isPresent()) builder.put(provider.getName().getNamespace(), rules.get());
        }

        return builder.build();
    }

    private static void onIndexReset()
    {
        uniquenessMidPointCache.clear();
        uniquenessParameterCache.clear();
    }

    static
    {
        BiomeProviders.addIndexResetListener(BiomeProviderUtils::onIndexReset);

        ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = new ImmutableList.Builder();
        (new OverworldBiomeBuilder()).addBiomes(pair -> builder.add(pair));
        VANILLA_POINTS = builder.build();
    }
}
