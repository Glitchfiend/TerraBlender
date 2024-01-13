/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.mixin;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import terrablender.worldgen.IExtendedBiomeSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mixin(BiomeSource.class)
public abstract class MixinBiomeSource implements BiomeResolver, IExtendedBiomeSource
{
    @Shadow
    public Supplier<Set<Holder<Biome>>> possibleBiomes;

    @Unique
    private boolean hasAppended = false;

    @Override
    public void appendDeferredBiomesList(List<Holder<Biome>> biomesToAppend)
    {
        // Don't append the biomes list again if we have already done so
        if (this.hasAppended) {
            return;
        }

        List<Holder<Biome>> possibleBiomes = new ArrayList<>();
        possibleBiomes.addAll(this.possibleBiomes.get());
        possibleBiomes.addAll(biomesToAppend);

        this.possibleBiomes = () -> new ObjectLinkedOpenHashSet<>(possibleBiomes.stream().distinct().collect(Collectors.toList()));
        this.hasAppended = true;
    }
}
