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

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import terrablender.api.RegionType;
import terrablender.core.TerraBlender;

import java.util.List;
import java.util.function.LongFunction;

public class LayeredNoiseUtil
{
    public static Area uniqueness(RegistryAccess registryAccess, RegionType regionType, long seed)
    {
        return finalUniqueness(regionType, seed, initialUniqueness(registryAccess, regionType));
    }

    public static InitialLayer initialUniqueness(RegistryAccess registryAccess, RegionType regionType) {
        return new InitialLayer(registryAccess, regionType);
    }

    public static Area finalUniqueness(RegionType regionType, long seed, InitialLayer initialLayer) {
        int numZooms = TerraBlender.CONFIG.overworldRegionSize;

        if (regionType == RegionType.NETHER)
            numZooms = TerraBlender.CONFIG.netherRegionSize;

        return createZoomedArea(seed, numZooms, initialLayer);
    }

    public static Area biomeArea(RegistryAccess registryAccess, long seed, int size, List<WeightedEntry.Wrapper<ResourceKey<Biome>>> entries)
    {
        return createZoomedArea(seed, size, new BiomeInitialLayer(registryAccess, entries));
    }

    public static Area createZoomedArea(long seed, int zooms, AreaTransformer0 initialTransformer)
    {
        LongFunction<AreaContext> contextFactory = (seedModifier) -> new AreaContext(25, seed, seedModifier);
        AreaFactory factory = initialTransformer.run(contextFactory.apply(1L));
        factory = ZoomLayer.FUZZY.run(contextFactory.apply(2000L), factory);
        factory = zoom(2001L, ZoomLayer.NORMAL, factory, 3, contextFactory);
        factory = zoom(1001L, ZoomLayer.NORMAL, factory, zooms, contextFactory);
        return factory.make();
    }

    public static AreaFactory zoom(long seedModifier, AreaTransformer1 transformer, AreaFactory initialAreaFactory, int times, LongFunction<AreaContext> contextFactory)
    {
        AreaFactory areaFactory = initialAreaFactory;

        for (int i = 0; i < times; ++i)
        {
            areaFactory = transformer.run(contextFactory.apply(seedModifier + (long)i), areaFactory);
        }

        return areaFactory;
    }
}
