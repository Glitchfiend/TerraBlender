/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
