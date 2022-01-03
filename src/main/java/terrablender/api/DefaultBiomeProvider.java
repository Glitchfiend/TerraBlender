/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
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

    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> vanillaMapper = (pair) -> {
            mapper.accept(Pair.of(BiomeProviderUtils.convertParameterPoint(pair.getFirst(), this.getUniquenessParameter()), pair.getSecond()));
        };

        (new OverworldBiomeBuilder()).addBiomes(vanillaMapper);
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
