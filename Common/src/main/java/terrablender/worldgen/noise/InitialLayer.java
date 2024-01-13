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
package terrablender.worldgen.noise;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class InitialLayer extends WeightedRandomLayer<WeightedEntry.Wrapper<Region>>
{
    private final RegionType regionType;

    public InitialLayer(RegistryAccess registryAccess, RegionType regionType)
    {
        super(createEntries(registryAccess, regionType));
        this.regionType = regionType;
    }

    @Override
    protected int getEntryIndex(WeightedEntry.Wrapper<Region> entry)
    {
        return Regions.getIndex(this.regionType, entry.getData().getName());
    }

    private static List<WeightedEntry.Wrapper<Region>> createEntries(RegistryAccess registryAccess, RegionType regionType)
    {
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        return Regions.get(regionType).stream().filter(region -> {
            AtomicBoolean biomesAdded = new AtomicBoolean(false);
            region.addBiomes(biomeRegistry, pair -> biomesAdded.set(true));

            // Filter out irrelevant regions or regions without any biomes
            return region.getType() == regionType && biomesAdded.get();
        }).map(region -> WeightedEntry.wrap(region, region.getWeight())).collect(ImmutableList.toImmutableList());
    }
}