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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import terrablender.core.TerraBlender;
import terrablender.worldgen.DefaultNetherRegion;
import terrablender.worldgen.DefaultOverworldRegion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Regions
{
    private static Map<RegionType, LinkedHashMap<ResourceLocation, Region>> regions = Maps.newHashMap();
    private static Map<RegionType, Map<ResourceLocation, Integer>> indices = Maps.newHashMap();

    /**
     * Register a {@link Region}.
     * @param name the name of the region.
     * @param region the region.
     */
    public static void register(ResourceLocation name, Region region)
    {
        regions.get(region.getType()).put(name, region);
        int index = regions.get(region.getType()).size() - 1;
        indices.get(region.getType()).put(name, index);
        TerraBlender.LOGGER.info("Registered region " + name + " to index " + index);
    }

    /**
     * Register a {@link Region} to a specified index.
     * @param name the name of the region.
     * @param index the index of the region.
     * @param region the region.
     */
    public static void register(ResourceLocation name, int index, Region region)
    {
        // Construct a list of the existing entries and add in our new entry
        List<Map.Entry<ResourceLocation, Region>> entries = Lists.newArrayList(regions.get(region.getType()).entrySet());
        entries.add(index, Map.entry(name, region));

        // Clear the current regions and reconstruct the map
        regions.get(region.getType()).clear();
        entries.forEach(entry -> regions.get(region.getType()).put(entry.getKey(), entry.getValue()));
        TerraBlender.LOGGER.info("Registered region " + name + " to index " + index + " for type " + region.getType());
    }

    /**
     * Register a {@link Region}.
     * @param region the region.
     */
    public static void register(Region region)
    {
        register(region.getName(), region);
    }

    /**
     * Remove a region.
     * @param type the type of the region.
     * @param name the name of the region.
     */
    public static void remove(RegionType type, ResourceLocation name)
    {
        if (!regions.get(type).containsKey(name))
            return;

        regions.get(type).remove(name);
        TerraBlender.LOGGER.info("Removed region " + name);
    }

    /**
     * Get the list of regions.
     * @param type the type of the region.
     * @return the list of regions.
     */
    public static List<Region> get(RegionType type)
    {
        return ImmutableList.copyOf(regions.get(type).values());
    }

    /**
     * Gets the index associated with a region's {@link ResourceLocation}.
     * @param type the type of the region.
     * @param location the location of the region.
     * @return the index of the region.
     */
    public static int getIndex(RegionType type, ResourceLocation location)
    {
        LinkedHashMap<ResourceLocation, Region> typedRegions = regions.get(type);
        Map<ResourceLocation, Integer> typedIndices = indices.get(type);

        if (typedIndices.containsKey(location))
            return typedIndices.get(location);

        if (!typedRegions.containsKey(location))
            throw new RuntimeException("Attempted to get index of an unregistered region " + location);

        int index = ImmutableList.copyOf(typedRegions.keySet()).indexOf(location);
        typedIndices.put(location, index);
        return index;
    }

    /**
     * Gets the number of regions for a type.
     * @param type the type of the region.
     * @return the region count.
     */
    public static int getCount(RegionType type)
    {
        return regions.get(type).size();
    }

    static
    {
        // Initialize the maps for all regions
        for (RegionType type : RegionType.values())
        {
            regions.put(type, Maps.newLinkedHashMap());
            indices.put(type, Maps.newHashMap());
        }

        register(new DefaultOverworldRegion(TerraBlender.CONFIG.vanillaOverworldRegionWeight));
        register(new DefaultNetherRegion(TerraBlender.CONFIG.vanillaNetherRegionWeight));
    }
}
