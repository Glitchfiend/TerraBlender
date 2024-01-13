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

import net.minecraft.util.random.WeightedEntry;
import terrablender.util.WeightedRandomList;

import java.util.List;

public abstract class WeightedRandomLayer<T extends WeightedEntry> implements AreaTransformer0
{
    private final WeightedRandomList<T> weightedEntries;

    public WeightedRandomLayer(List<T> entries)
    {
        this.weightedEntries = WeightedRandomList.create(entries);
    }

    @Override
    public int apply(AreaContext context, int x, int y)
    {
        return this.weightedEntries.getRandom(context).map(this::getEntryIndex).orElse(getDefaultIndex());
    }

    protected abstract int getEntryIndex(T entry);

    protected int getDefaultIndex()
    {
        return 0;
    }
}
