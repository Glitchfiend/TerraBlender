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
package terrablender.api.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;

import java.util.function.Consumer;

/**
 * A region that is defined by a {@link RegionDefinition} and can be serialized to and from JSON.
 * @see RegionDefinition
 */
public class DataDrivenRegion extends Region
{
    public static final Codec<DataDrivenRegion> CODEC = RecordCodecBuilder.create(regionInstance -> regionInstance.group(
            RegionDefinition.CODEC.fieldOf("definition").forGetter(region -> region.definition)
    ).apply(regionInstance, DataDrivenRegion::new));

    private final RegionDefinition definition;

    public DataDrivenRegion(RegionDefinition regionDefinition)
    {
        super(regionDefinition);
        this.definition = regionDefinition;
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        for (BiomeMapping mapping : definition.biomeMappings())
        {
            mapper.accept(Pair.of(mapping.parameters(), mapping.biome()));
        }
    }
}


