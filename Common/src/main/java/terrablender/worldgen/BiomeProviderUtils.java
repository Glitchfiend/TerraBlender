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

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.apache.logging.log4j.util.TriConsumer;
import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import terrablender.api.GenerationSettings;
import terrablender.core.TerraBlender;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
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

    public static <T> List<Integer> getUniquenessValues(List<Pair<TBClimate.ParameterPoint, T>> parameters)
    {
        List<Integer> uniquenesses = parameters.stream().filter(value -> value.getFirst().uniqueness().min() == value.getFirst().uniqueness().max()).map(value -> (int)value.getFirst().uniqueness().min()).collect(ImmutableSet.toImmutableSet()).stream().sorted().collect(ImmutableList.toImmutableList());

        if (uniquenesses.isEmpty())
        {
            TerraBlender.LOGGER.error("No uniqueness values found in parameter values. Things may not work well!");

            // Fall back on our list from BiomeProviders
            return BiomeProviders.get().stream().map(provider -> provider.getIndex()).collect(ImmutableSet.toImmutableSet()).stream().sorted().collect(ImmutableList.toImmutableList());
        }

        if (uniquenesses.get(0) != 0)
            throw new IllegalStateException("Uniqueness values must start at 0");

        if (uniquenesses.size() > 0)
        {
            // Ensure the uniquenesses are consecutive
            for (int i = 1; i < uniquenesses.size(); i++)
            {
                if (uniquenesses.get(i - 1) + 1 != uniquenesses.get(i))
                {
                    throw new IllegalStateException("Uniqueness values must be consecutive.");
                }
            }
        }

        return uniquenesses;
    }

    public static List<TBClimate.ParameterPoint> getAllSpawnTargets()
    {
        ImmutableList.Builder<TBClimate.ParameterPoint> builder = new ImmutableList.Builder<>();
        BiomeProviders.get().forEach(provider -> builder.addAll(provider.getSpawnTargets()));
        return builder.build();
    }

    public static void addAllOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        addBiomesWithVerification(registry, mapper, BiomeProvider::getOverworldWeight, BiomeProvider::addOverworldBiomes);
    }

    public static void addAllNetherBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        addBiomesWithVerification(registry, mapper, BiomeProvider::getNetherWeight, BiomeProvider::addNetherBiomes);
    }

    private static void addBiomesWithVerification(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Function<BiomeProvider, Integer> weight, TriConsumer<BiomeProvider, Registry<Biome>, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>>> add)
    {
        Set<Integer> unusedIndices = Sets.newHashSet();

        BiomeProviders.get().forEach(provider -> {
            // Add to the list of indices if weighted more than 0.
            if (weight.apply(provider) > 0)
                unusedIndices.add(provider.getIndex());
        });

        Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> verificationMapper = pair -> {
            if (!unusedIndices.isEmpty())
            {
                Climate.Parameter uniqueness = pair.getFirst().uniqueness();

                // Remove indices that have been utilised
                if (uniqueness.min() == uniqueness.max())
                    unusedIndices.remove((int)uniqueness.min());
            }

            mapper.accept(pair);
        };

        BiomeProviders.get().forEach(provider -> {
            add.accept(provider, registry, verificationMapper);
        });

        if (unusedIndices.size() > 0)
            throw new RuntimeException("Biome indices have been registered but haven't been utilised: " + unusedIndices + ". Either utilise the uniqueness assigned to your biome provider or set your provider's weight to 0.");
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
