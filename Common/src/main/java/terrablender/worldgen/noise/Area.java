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

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

import java.util.Arrays;
import java.util.concurrent.locks.StampedLock;

public class Area
{
    private final long[] keys;
    private final int[] values;
    private final int mask;
    private final PixelTransformer operator;
    private final StampedLock lock = new StampedLock();

    public Area(PixelTransformer operator, int size)
    {
        this.operator = operator;

        size = Mth.smallestEncompassingPowerOfTwo(size);
        this.mask = size - 1;

        this.keys = new long[size];
        Arrays.fill(this.keys, Long.MIN_VALUE);
        this.values = new int[size];
    }

    public int get(int x, int z)
    {
        long key = key(x, z);
        int idx = hash(key) & this.mask;
        long stamp = this.lock.readLock();

        // if the entry here has a key that matches ours, we have a cache hit
        if (this.keys[idx] == key) {
            int value = this.values[idx];
            this.lock.unlockRead(stamp);

            return value;
        } else {
            // cache miss: sample and put the result into our cache entry
            this.lock.unlockRead(stamp);

            stamp = this.lock.writeLock();

            int value = this.operator.apply(x, z);
            this.keys[idx] = key;
            this.values[idx] = value;

            this.lock.unlockWrite(stamp);

            return value;
        }
    }

    private int hash(long key) {
        return (int) HashCommon.mix(key);
    }

    private long key(int x, int z)
    {
        return ChunkPos.asLong(x, z);
    }

    public int getMaxCache()
    {
        return this.mask + 1;
    }
}