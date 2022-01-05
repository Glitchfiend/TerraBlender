/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;

import java.util.Optional;
import java.util.function.Consumer;

public class DefaultBiomeProvider extends BiomeProvider
{
    public DefaultBiomeProvider(ResourceLocation location, int weight)
    {
        super(location, weight);
    }

    @Override
    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> vanillaMapper = (pair) -> {
            mapper.accept(Pair.of(BiomeProviderUtils.convertParameterPoint(pair.getFirst(), this.getUniquenessParameter()), pair.getSecond()));
        };

        (new OverworldBiomeBuilder()).addBiomes(vanillaMapper);
    }

    @Override
    public void addNetherBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        this.addBiome(mapper, Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), 0.0F, Biomes.NETHER_WASTES);
        this.addBiome(mapper, Climate.Parameter.point(0.0F), Climate.Parameter.point(-0.5F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), 0.0F, Biomes.SOUL_SAND_VALLEY);
        this.addBiome(mapper, Climate.Parameter.point(0.4F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), 0.0F, Biomes.CRIMSON_FOREST);
        this.addBiome(mapper, Climate.Parameter.point(0.0F), Climate.Parameter.point(0.5F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), 0.375F, Biomes.WARPED_FOREST);
        this.addBiome(mapper, Climate.Parameter.point(-0.5F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), Climate.Parameter.point(0.0F), 0.175F, Biomes.BASALT_DELTAS);
    }

    @Override
    public Optional<SurfaceRules.RuleSource> getOverworldSurfaceRules()
    {
        return Optional.of(SurfaceRuleData.overworld());
    }

    @Override
    public Optional<SurfaceRules.RuleSource> getNetherSurfaceRules()
    {
        return Optional.of(SurfaceRuleData.nether());
    }
}
