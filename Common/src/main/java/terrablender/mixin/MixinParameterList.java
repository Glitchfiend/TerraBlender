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

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.worldgen.IExtendedParameterList;
import terrablender.worldgen.RegionUtils.SearchTreeEntry;
import terrablender.worldgen.noise.Area;
import terrablender.worldgen.noise.InitialLayer;
import terrablender.worldgen.noise.LayeredNoiseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(Climate.ParameterList.class)
public abstract class MixinParameterList<T> implements IExtendedParameterList<T>
{
    @Shadow
    @Final
    private List<Pair<Climate.ParameterPoint, T>> values;

    @Shadow
    public abstract T findValue(Climate.TargetPoint target);

    private boolean initialized = false;
    private boolean treesPopulated = false;
    private Supplier<Area> newUniqueness;
    private Area uniqueness;
    private SearchTreeEntry[] uniqueTrees;

    @Override
    public void initializeForTerraBlender(RegistryAccess registryAccess, RegionType regionType, long seed)
    {
        // We don't want to initialize multiple times
        if (this.initialized)
            return;

        InitialLayer initialLayer = LayeredNoiseUtil.initialUniqueness(registryAccess, regionType);
        this.newUniqueness = () -> LayeredNoiseUtil.finalUniqueness(regionType, seed, initialLayer);
        this.uniqueness = this.newUniqueness.get();
        this.uniqueTrees = new SearchTreeEntry[Regions.getCount(regionType)];

        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        for (Region region : Regions.get(regionType))
        {
            int regionIndex = Regions.getIndex(regionType, region.getName());

            // Use the existing values for index 0, rather than those from the region. This is for datapack support.
            if (regionIndex == 0)
            {
                this.uniqueTrees[0] = new SearchTreeEntry(region, Climate.RTree.create(this.values));
            }
            else
            {
                List<Pair<Climate.ParameterPoint, Holder<Biome>>> pairs = new ArrayList<>();
                region.addBiomes(biomeRegistry, pair -> {
                    if (biomeRegistry.getHolder(pair.getSecond()).isPresent())
                        pairs.add(pair.mapSecond(biomeRegistry::getHolderOrThrow));
                });

                // We can't create an RTree if there are no values present.
                if (!pairs.isEmpty())
                    this.uniqueTrees[regionIndex] = new SearchTreeEntry(region, Climate.RTree.create(pairs));
            }
        }

        this.treesPopulated = true;

        // Mark as initialized
        this.initialized = true;
    }

    @Override
    public int getUniqueness(int x, int y, int z)
    {
        return this.uniqueness.get(x, z);
    }

    @Override
    public Climate.RTree getTree(int uniqueness)
    {
        return this.uniqueTrees[uniqueness].tree();
    }

    @Override
    public Region getRegion(int uniqueness)
    {
        return this.uniqueTrees[uniqueness].region();
    }

    @Override
    public int getTreeCount()
    {
        return this.uniqueTrees.length;
    }

    @Override
    public boolean isInitialized()
    {
        return this.initialized && this.treesPopulated;
    }

    @Override
    public T findValuePositional(Climate.TargetPoint target, int x, int y, int z)
    {
        // Fallback on findValue if we are uninitialized (may be the case for non-TerraBlender dimensions)
        if (!this.initialized)
            return this.findValue(target);

        if (!this.treesPopulated)
            throw new RuntimeException("Attempted to call findValuePositional whilst trees remain unpopulated!");

        int uniqueness = this.getUniqueness(x, y, z);
        Holder<Biome> biome = (Holder<Biome>)this.getTree(uniqueness).search(target, Climate.RTree.Node::distance);

        if (biome.is(Region.DEFERRED_PLACEHOLDER))
            return (T)this.uniqueTrees[0].tree().search(target, Climate.RTree.Node::distance);
        else
            return (T)biome;
    }

    @Override
    public Climate.ParameterList<T> clone() {
        try {
            return (Climate.ParameterList<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Unique
    @Override
    public void recreateUniqueness() {
        if (!this.initialized) {
            return;
        }
        this.uniqueness = this.newUniqueness.get();
    }
}
