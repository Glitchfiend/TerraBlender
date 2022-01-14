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
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.core.TerraBlender;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A source of modded biome parameters and other biome-related data.
 * Each mod should implement one or more biome providers.
 */
public abstract class BiomeProvider
{
    private final ResourceLocation name;
    private final int overworldWeight;
    private final int netherWeight;

    public BiomeProvider(ResourceLocation name, int overworldWeight)
    {
        this(name, overworldWeight, 0);
    }

    public BiomeProvider(ResourceLocation name, int overworldWeight, int netherWeight)
    {
        this.name = name;
        this.overworldWeight = overworldWeight;
        this.netherWeight = netherWeight;
    }

    /**
     * A place to register biome parameter mappings for the overworld. This may be used in conjunction with {@link #addBiome(Consumer, Climate.Parameter, Climate.Parameter, Climate.Parameter, Climate.Parameter, Climate.Parameter, Climate.Parameter, float, ResourceKey) addBiome}.
     * Mojang's approach to biome parameter mappings may be found in {@link net.minecraft.world.level.biome.OverworldBiomeBuilder OverworldBiomeBuilder}.
     * @param registry the biome registry.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     */
    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper) {}

    /**
     * A place to register biome parameter mappings for the nether. Functions the same as the overworld equivalent {@link #addOverworldBiomes(Registry, Consumer) addOverworldBiomes}.
     * @param registry the biome registry.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     */
    public void addNetherBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper) {}

    /**
     * Gets a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoints} suitable for spawning the player in.
     * @return a list of parameter points.
     */
    public List<TBClimate.ParameterPoint> getSpawnTargets()
    {
        return ImmutableList.of();
    }

    /**
     * Get any custom surface rules for the overworld. If unspecified, the {@link terrablender.api.GenerationSettings#getDefaultOverworldSurfaceRules() default surface rules} will be used.
     * @return the overworld surface rules.
     */
    public Optional<SurfaceRules.RuleSource> getOverworldSurfaceRules()
    {
        return Optional.empty();
    }

    /**
     * Get any custom surface rules for the nether. If unspecified, the {@link GenerationSettings#getDefaultNetherSurfaceRules() default surface rules} will be used.
     * @return the nether surface rules.
     */
    public Optional<SurfaceRules.RuleSource> getNetherSurfaceRules()
    {
        return Optional.empty();
    }

    /**
     * Get the name for this biome provider.
     * @return the biome provider name.
     */
    public ResourceLocation getName()
    {
        return this.name;
    }

    /**
     * Get the weight of this biome provider's regions in the overworld.
     * @return the overworld region weight.
     */
    public int getOverworldWeight()
    {
        return this.overworldWeight;
    }

    /**
     * Get the weight of this biome provider's regions in the nether.
     * @return the nether region weight.
     */
    public int getNetherWeight()
    {
        return this.netherWeight;
    }

    /**
     * Get the index specific to this biome provider.
     * @return the biome provider's index.
     */
    public final int getIndex()
    {
        return BiomeProviders.getIndex(this.getName());
    }

    /**
     * Get the uniqueness parameter specific to this biome provider.
     * @return the biome provider's uniqueness parameter.
     */
    protected final Climate.Parameter getUniquenessParameter()
    {
        return BiomeProviderUtils.getUniquenessParameter(this.getIndex());
    }

    /**
     * Adds a biome using the provided mapper.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     * @param temperature the temperature parameter value.
     * @param humidity the humidity parameter value.
     * @param continentalness the continentalness parameter value.
     * @param erosion the erosion parameter value.
     * @param weirdness the weirdness parameter value
     * @param depth the depth parameter value.
     * @param offset the offset parameter value.
     * @param biome the biome to be added.
     */
    protected final void addBiome(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, Climate.Parameter depth, float offset, ResourceKey<Biome> biome)
    {
        addBiome(mapper, TBClimate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, getUniquenessParameter(), offset), biome);
    }

    /**
     * Adds a biome using the provided mapper.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     * @param temperature the temperature value.
     * @param humidity the humidity value.
     * @param continentalness the continentalness value.
     * @param erosion the erosion value.
     * @param weirdness the weirdness value
     * @param depth the depth value.
     * @param offset the offset parameter value.
     * @param biome the biome to be added.
     */
    protected final void addBiome(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, ParameterUtils.Temperature temperature, ParameterUtils.Humidity humidity, ParameterUtils.Continentalness continentalness, ParameterUtils.Erosion erosion, ParameterUtils.Weirdness weirdness, ParameterUtils.Depth depth, float offset, ResourceKey<Biome> biome)
    {
        addBiome(mapper, TBClimate.parameters(temperature.parameter(), humidity.parameter(), continentalness.parameter(), erosion.parameter(), depth.parameter(), weirdness.parameter(), getUniquenessParameter(), offset), biome);
    }

    /**
     * Adds a biome using the provided mapper.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     * @param parameters the parameters corresponding to the biome.
     * @param biome the biome to be added.
     */
    protected final void addBiome(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, TBClimate.ParameterPoint parameters, ResourceKey<Biome> biome)
    {
        mapper.accept(Pair.of(parameters, biome));
    }

    /**
     * Adds a biome using climate parameters similar to those of a given Vanilla biome.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     * @param similarVanillaBiome the Vanilla biome that is similar to the one to be added.
     * @param biome the biome to be added.
     */
    protected final void addBiomeSimilar(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, ResourceKey<Biome> similarVanillaBiome, ResourceKey<Biome> biome)
    {
        List<TBClimate.ParameterPoint> points = BiomeProviderUtils.getVanillaParameterPoints(similarVanillaBiome).stream().map(point -> ParameterUtils.convertParameterPoint(point, getUniquenessParameter())).collect(ImmutableList.toImmutableList());
        points.forEach(point -> addBiome(mapper, point, biome));
    }

    /**
     * Adds all Vanilla overworld biomes with any modifications made.
     * @param mapper the mapper used to construct a list of {@link terrablender.worldgen.TBClimate.ParameterPoint ParameterPoint} to biome mappings.
     * @param onModify a consumer which can be used to modify the Vanilla overworld parameters.
     */
    protected final void addModifiedVanillaOverworldBiomes(Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper, Consumer<ModifiedVanillaOverworldBuilder> onModify)
    {
        ModifiedVanillaOverworldBuilder builder = new ModifiedVanillaOverworldBuilder();
        onModify.accept(builder);
        builder.build(this.getUniquenessParameter()).forEach(mapper::accept);
    }
}
