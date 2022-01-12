/*
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

import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.List;
import java.util.Optional;

public class InitialLayer implements AreaTransformer0
{
    private final WeightedRandomList<BiomeProvider> weightedEntries;

    public InitialLayer()
    {
        this.weightedEntries = WeightedRandomList.create(BiomeProviders.get());
    }

    @Override
    public int apply(AreaContext context, int x, int y)
    {
        Optional<BiomeProvider> entry = weightedEntries.getRandom(context);
        return entry.isPresent() ? entry.get().getIndex() : 0;
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
