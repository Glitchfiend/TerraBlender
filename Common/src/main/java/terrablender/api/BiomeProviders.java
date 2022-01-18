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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import terrablender.core.TerraBlender;
import terrablender.worldgen.DefaultBiomeProvider;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BiomeProviders
{
    /** The resource location of the default biome provider. */
    public static final ResourceLocation DEFAULT_PROVIDER_LOCATION = new ResourceLocation("minecraft:biome_provider");

    private static LinkedHashMap<ResourceLocation, BiomeProvider> biomeProviders = Maps.newLinkedHashMap();
    private static Map<ResourceLocation, Integer> biomeIndices = Maps.newHashMap();
    private static List<Runnable> indexResetListeners = Lists.newArrayList();

    /**
     * Register a {@link BiomeProvider}.
     * @param name the name of the biome provider.
     * @param provider the biome provider.
     */
    public static void register(ResourceLocation name, BiomeProvider provider)
    {
        biomeProviders.put(name, provider);
        int index = biomeProviders.size() - 1;
        biomeIndices.put(name, index);
        TerraBlender.LOGGER.info("Registered biome provider " + name + " to index " + index);
    }

    /**
     * Register a {@link BiomeProvider} to a specified index.
     * @param name the name of the biome provider.
     * @param index the index of the biome provider.
     * @param provider the biome provider.
     */
    public static void register(ResourceLocation name, int index, BiomeProvider provider)
    {
        // Construct a list of the existing entries and add in our new entry
        List<Map.Entry<ResourceLocation, BiomeProvider>> entries = Lists.newArrayList(biomeProviders.entrySet());
        entries.add(index, Map.entry(name, provider));

        // Clear the current biome providers and reconstruct the map
        biomeProviders.clear();
        entries.forEach(entry -> biomeProviders.put(entry.getKey(), entry.getValue()));

        // Reset the indices
        resetIndices();
        TerraBlender.LOGGER.info("Registered biome provider " + name + " to index " + index);
    }

    /**
     * Register a {@link BiomeProvider}.
     * @param provider the biome provider.
     */
    public static void register(BiomeProvider provider)
    {
        register(provider.getName(), provider);
    }

    /**
     * Remove a biome provider.
     * @param name the name of the biome provider.
     */
    public static void remove(ResourceLocation name)
    {
        if (!biomeProviders.containsKey(name))
            return;

        biomeProviders.remove(name);
        resetIndices();
        TerraBlender.LOGGER.info("Removed biome provider " + name);
    }

    /**
     * Get the list of biome providers.
     * @return the list of biome providers.
     */
    public static List<BiomeProvider> get()
    {
        return ImmutableList.copyOf(biomeProviders.values());
    }

    /**
     * Add a listener for when biome provider indices are reset.
     * This usually happens when a biome provider is removed at runtime, such as in the case of data packs.
     * @param listener the listener.
     */
    public static void addIndexResetListener(Runnable listener)
    {
        indexResetListeners.add(listener);
    }

    /**
     * Gets the index associated with a biome provider's {@link ResourceLocation}.
     * @param location the location of the biome provider.
     * @return the index of the biome provider.
     */
    public static int getIndex(ResourceLocation location)
    {
        if (biomeIndices.containsKey(location))
            return biomeIndices.get(location);

        if (!biomeProviders.containsKey(location))
            throw new RuntimeException("Attempted to get index of an unregistered biome provider " + location);

        int index = ImmutableList.copyOf(biomeProviders.keySet()).indexOf(location);
        biomeIndices.put(location, index);
        return index;
    }

    /**
     * Gets the number of biome providers.
     * @return the biome provider count.
     */
    public static int getCount()
    {
        return biomeProviders.size();
    }

    private static void resetIndices()
    {
        biomeIndices.clear();
        indexResetListeners.forEach(listener -> listener.run());
    }

    static
    {
        register(DEFAULT_PROVIDER_LOCATION, new DefaultBiomeProvider(DEFAULT_PROVIDER_LOCATION, TerraBlender.CONFIG.vanillaOverworldRegionWeight, TerraBlender.CONFIG.vanillaNetherRegionWeight));
    }
}
