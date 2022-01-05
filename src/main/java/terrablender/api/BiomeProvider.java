/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class BiomeProvider extends WeightedEntry.IntrusiveBase
{
    private final ResourceLocation name;

    public BiomeProvider(ResourceLocation name, int weight)
    {
        super(weight);
        this.name = name;
    }

    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper) {}
    public void addNetherBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper) {}

    public List<TBClimate.ParameterPoint> getSpawnTargets()
    {
        return ImmutableList.of();
    }

    public Optional<SurfaceRules.RuleSource> getOverworldSurfaceRules()
    {
        return Optional.empty();
    }

    public Optional<SurfaceRules.RuleSource> getNetherSurfaceRules()
    {
        return Optional.empty();
    }

    public ResourceLocation getName()
    {
        return this.name;
    }

    public final int getIndex()
    {
        return BiomeProviders.getIndex(this.getName());
    }

    protected final Climate.Parameter getUniquenessParameter()
    {
        return BiomeProviderUtils.getUniquenessParameter(this.getIndex());
    }

    protected final void addBiome(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, Climate.Parameter depth, float offset, ResourceKey<Biome> biome)
    {
        mapper.accept(Pair.of(TBClimate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, getUniquenessParameter(), offset), biome));
    }
}
