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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import terrablender.api.RegionType;

import java.util.List;

public record RegionDefinition(Identifier name, RegionType type, int weight, List<BiomeMapping> biomeMappings)
{
    public static final Codec<RegionDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("name").forGetter(RegionDefinition::name),
            RegionType.CODEC.fieldOf("type").forGetter(RegionDefinition::type),
            Codec.INT.fieldOf("weight").forGetter(RegionDefinition::weight),
            BiomeMapping.CODEC.listOf().fieldOf("biomeMappings").forGetter(RegionDefinition::biomeMappings)
    ).apply(instance, RegionDefinition::new));
}
