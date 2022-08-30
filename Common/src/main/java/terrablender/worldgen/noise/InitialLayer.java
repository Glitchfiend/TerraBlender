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

public class InitialLayer implements AreaTransformer0
{
    private final RegionType regionType;
    private final WeightedRandomList<WeightedEntry.Wrapper<Region>> weightedEntries;

    public InitialLayer(RegistryAccess registryAccess, RegionType type)
    {
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        this.regionType = type;
        this.weightedEntries = WeightedRandomList.create(Regions.get(this.regionType).stream().filter(region -> {
            AtomicBoolean biomesAdded = new AtomicBoolean(false);
            region.addBiomes(biomeRegistry, pair -> biomesAdded.set(true));

            // Filter out irrelevant regions or regions without any biomes
            return region.getType() == type && biomesAdded.get();
        }).map(region -> WeightedEntry.wrap(region, region.getWeight())).collect(ImmutableList.toImmutableList()));
    }

    @Override
    public int apply(AreaContext context, int x, int y)
    {
        Optional<WeightedEntry.Wrapper<Region>> entry = weightedEntries.getRandom(context);
        return entry.isPresent() ? Regions.getIndex(this.regionType, entry.get().getData().getName()) : 0;
    }

    private static class WeightedRandomList<E extends WeightedEntry>
    {
        private final int totalWeight;
        private final ImmutableList<E> items;

        WeightedRandomList(List<? extends E> items)
        {
            this.items = ImmutableList.copyOf(items);
            this.totalWeight = WeightedRandom.getTotalWeight(items);
        }

        public static <E extends WeightedEntry> WeightedRandomList<E> create() {
            return new WeightedRandomList<>(ImmutableList.of());
        }

        @SafeVarargs
        public static <E extends WeightedEntry> WeightedRandomList<E> create(E... entries)
        {
            return new WeightedRandomList<>(ImmutableList.copyOf(entries));
        }

        public static <E extends WeightedEntry> WeightedRandomList<E> create(List<E> entries)
        {
            return new WeightedRandomList<>(entries);
        }

        public Optional<E> getRandom(AreaContext context)
        {
            if (this.totalWeight == 0) {
                return Optional.empty();
            } else {
                int i = context.nextRandom(this.totalWeight);
                return WeightedRandom.getWeightedItem(this.items, i);
            }
        }
    }
}
