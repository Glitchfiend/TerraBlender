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
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import terrablender.core.TerraBlender;

import java.util.ArrayList;
import java.util.List;

public class EndBiomeRegistry
{
    private static final List<WeightedEntry.Wrapper<ResourceKey<Biome>>> highlandsBiomes = new ArrayList<>();
    private static final List<WeightedEntry.Wrapper<ResourceKey<Biome>>> midlandsBiomes = new ArrayList<>();
    private static final List<WeightedEntry.Wrapper<ResourceKey<Biome>>> edgeBiomes = new ArrayList<>();
    private static final List<WeightedEntry.Wrapper<ResourceKey<Biome>>> islandBiomes = new ArrayList<>();

    /**
     * Registers a biome to generate in the highlands of the end.
     * @param biome the biome to register.
     * @param weight the biome weight.
     */
    public static void registerHighlandsBiome(ResourceKey<Biome> biome, int weight)
    {
        highlandsBiomes.add(WeightedEntry.wrap(biome, weight));
    }

    /**
     * Registers a biome to generate in the midlands of the end.
     * @param biome the biome to register.
     * @param weight the biome weight.
     */
    public static void registerMidlandsBiome(ResourceKey<Biome> biome, int weight)
    {
        midlandsBiomes.add(WeightedEntry.wrap(biome, weight));
    }

    /**
     * Registers a biome to generate in the outer edges of islands in the end.
     * @param biome the biome to register.
     * @param weight the biome weight.
     */
    public static void registerEdgeBiome(ResourceKey<Biome> biome, int weight)
    {
        edgeBiomes.add(WeightedEntry.wrap(biome, weight));
    }

    /**
     * Registers a biome to generate as a small island of the end.
     * @param biome the biome to register.
     * @param weight the biome weight.
     */
    public static void registerIslandBiome(ResourceKey<Biome> biome, int weight)
    {
        islandBiomes.add(WeightedEntry.wrap(biome, weight));
    }

    /**
     * Gets the biomes to generate in the highlands of the end.
     * @return the biomes to generate.
     */
    public static List<WeightedEntry.Wrapper<ResourceKey<Biome>>> getHighlandsBiomes()
    {
        return ImmutableList.copyOf(highlandsBiomes);
    }

    /**
     * Gets the biomes to generate in the midlands of the end.
     * @return the biomes to generate.
     */
    public static List<WeightedEntry.Wrapper<ResourceKey<Biome>>> getMidlandsBiomes()
    {
        return ImmutableList.copyOf(midlandsBiomes);
    }

    /**
     * Gets the biomes to generate in the outer edges of islands in the end.
     * @return the biomes to generate.
     */
    public static List<WeightedEntry.Wrapper<ResourceKey<Biome>>> getEdgeBiomes()
    {
        return ImmutableList.copyOf(edgeBiomes);
    }

    /**
     * Gets the biomes to generate as a small island of the end.
     * @return the biomes to generate.
     */
    public static List<WeightedEntry.Wrapper<ResourceKey<Biome>>> getIslandBiomes()
    {
        return ImmutableList.copyOf(islandBiomes);
    }

    static
    {
        registerHighlandsBiome(Biomes.END_HIGHLANDS, TerraBlender.CONFIG.vanillaEndHighlandsWeight);
        registerMidlandsBiome(Biomes.END_MIDLANDS, TerraBlender.CONFIG.vanillaEndMidlandsWeight);
        registerEdgeBiome(Biomes.END_BARRENS, TerraBlender.CONFIG.vanillaEndBarrensWeight);
        registerIslandBiome(Biomes.SMALL_END_ISLANDS, TerraBlender.CONFIG.vanillaSmallEndIslandsWeight);
    }
}
