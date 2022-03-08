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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.worldgen.RegionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * A source of modded biome parameters and other biome-related data.
 * Each mod should implement one or more regions.
 */
public abstract class Region
{
    private final ResourceLocation name;
    private RegionType type;
    private int weight;

    public Region(ResourceLocation name, RegionType type, int weight)
    {
        this.name = name;
        this.type = type;
        this.weight = weight;
    }

    /**
     * Get the name for this region.
     * @return the region name.
     */
    public ResourceLocation getName()
    {
        return this.name;
    }

    /**
     * Get the type of this region.
     * @return the region type.
     */
    public RegionType getType()
    {
        return this.type;
    }

    /**
     * Get the weight of this region.
     * @return the region weight.
     */
    public int getWeight()
    {
        return this.weight;
    }

    /**
     * A place to register biome parameter mappings. This may be used in conjunction with {@link #addBiome(Consumer, Climate.Parameter, Climate.Parameter, Climate.Parameter, Climate.Parameter, Climate.Parameter, Climate.Parameter, float, ResourceKey) addBiome}.
     * In the case of the overworld, Mojang's approach to biome parameter mappings may be found in {@link net.minecraft.world.level.biome.OverworldBiomeBuilder OverworldBiomeBuilder}.
     * @param mapper the mapper used to construct a list of {@link Climate.ParameterPoint ParameterPoint} to biome mappings.
     */
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {}

    /**
     * Adds a biome using the provided mapper.
     * @param mapper the mapper used to construct a list of {@link Climate.ParameterPoint ParameterPoint} to biome mappings.
     * @param temperature the temperature parameter value.
     * @param humidity the humidity parameter value.
     * @param continentalness the continentalness parameter value.
     * @param erosion the erosion parameter value.
     * @param weirdness the weirdness parameter value
     * @param depth the depth parameter value.
     * @param offset the offset parameter value.
     * @param biome the biome to be added.
     */
    protected final void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, Climate.Parameter depth, float offset, ResourceKey<Biome> biome)
    {
        addBiome(mapper, Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, offset), biome);
    }

    /**
     * Adds a biome using the provided mapper.
     * @param mapper the mapper used to construct a list of {@link Climate.ParameterPoint ParameterPoint} to biome mappings.
     * @param temperature the temperature value.
     * @param humidity the humidity value.
     * @param continentalness the continentalness value.
     * @param erosion the erosion value.
     * @param weirdness the weirdness value
     * @param depth the depth value.
     * @param offset the offset parameter value.
     * @param biome the biome to be added.
     */
    protected final void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, ParameterUtils.Temperature temperature, ParameterUtils.Humidity humidity, ParameterUtils.Continentalness continentalness, ParameterUtils.Erosion erosion, ParameterUtils.Weirdness weirdness, ParameterUtils.Depth depth, float offset, ResourceKey<Biome> biome)
    {
        addBiome(mapper, Climate.parameters(temperature.parameter(), humidity.parameter(), continentalness.parameter(), erosion.parameter(), depth.parameter(), weirdness.parameter(), offset), biome);
    }

    /**
     * Adds a biome using the provided mapper.
     * @param mapper the mapper used to construct a list of {@link Climate.ParameterPoint ParameterPoint} to biome mappings.
     * @param parameters the parameters corresponding to the biome.
     * @param biome the biome to be added.
     */
    protected final void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.ParameterPoint parameters, ResourceKey<Biome> biome)
    {
        mapper.accept(Pair.of(parameters, biome));
    }

    /**
     * Adds a biome using climate parameters similar to those of a given Vanilla biome.
     * @param mapper the mapper used to construct a list of {@link Climate.ParameterPoint ParameterPoint} to biome mappings.
     * @param similarVanillaBiome the Vanilla biome that is similar to the one to be added.
     * @param biome the biome to be added.
     */
    protected final void addBiomeSimilar(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, ResourceKey<Biome> similarVanillaBiome, ResourceKey<Biome> biome)
    {
        List<Climate.ParameterPoint> points = RegionUtils.getVanillaParameterPoints(similarVanillaBiome).stream().collect(ImmutableList.toImmutableList());
        points.forEach(point -> addBiome(mapper, point, biome));
    }

    /**
     * Adds all Vanilla overworld biomes with any modifications made.
     * @param mapper the mapper used to construct a list of {@link Climate.ParameterPoint ParameterPoint} to biome mappings.
     * @param onModify a consumer which can be used to modify the Vanilla overworld parameters.
     */
    protected final void addModifiedVanillaOverworldBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Consumer<ModifiedVanillaOverworldBuilder> onModify)
    {
        ModifiedVanillaOverworldBuilder builder = new ModifiedVanillaOverworldBuilder();
        onModify.accept(builder);
        builder.build().forEach(mapper::accept);
    }

}
