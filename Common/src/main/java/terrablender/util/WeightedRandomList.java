/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import terrablender.worldgen.noise.AreaContext;
import terrablender.worldgen.noise.WeightedRandomLayer;

import java.util.List;
import java.util.Optional;

public class WeightedRandomList<E extends WeightedEntry>
{
    private final int totalWeight;
    private final ImmutableList<E> items;

    WeightedRandomList(List<? extends E> items)
    {
        this.items = ImmutableList.copyOf(items);
        this.totalWeight = WeightedRandom.getTotalWeight(items);
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> create()
    {
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
