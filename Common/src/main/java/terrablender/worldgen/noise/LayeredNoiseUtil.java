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

import terrablender.api.BiomeProvider;
import terrablender.core.TerraBlender;

import java.util.function.Function;
import java.util.function.LongFunction;

public class LayeredNoiseUtil
{
    public static Area uniqueness(long worldSeed, UniquenessType type)
    {
        int regionSize;
        Function<BiomeProvider, Integer> weightGetter;

        switch (type)
        {
            case OVERWORLD_LARGE:
                regionSize = TerraBlender.CONFIG.overworldLargeBiomesRegionSize;
                weightGetter = BiomeProvider::getOverworldWeight;
                break;

            case NETHER:
                regionSize = TerraBlender.CONFIG.netherRegionSize;
                weightGetter = BiomeProvider::getNetherWeight;
                break;

            case NETHER_LARGE:
                regionSize = TerraBlender.CONFIG.netherLargeBiomesRegionSize;
                weightGetter = BiomeProvider::getNetherWeight;
                break;

            default:
                regionSize = TerraBlender.CONFIG.overworldRegionSize;
                weightGetter = BiomeProvider::getOverworldWeight;
                break;
        }

        LongFunction<AreaContext> contextFactory = (seedModifier) -> new AreaContext(25, worldSeed, seedModifier);
        AreaFactory factory = new InitialLayer(weightGetter).run(contextFactory.apply(1L));
        factory = ZoomLayer.FUZZY.run(contextFactory.apply(2000L), factory);
        factory = zoom(2001L, ZoomLayer.NORMAL, factory, 3, contextFactory);
        factory = zoom(1001L, ZoomLayer.NORMAL, factory, regionSize, contextFactory);
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

    public enum UniquenessType
    {
        OVERWORLD, OVERWORLD_LARGE, NETHER, NETHER_LARGE
    }
}
