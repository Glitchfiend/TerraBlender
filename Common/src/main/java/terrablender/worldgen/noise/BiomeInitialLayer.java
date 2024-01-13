/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.worldgen.noise;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;

public class BiomeInitialLayer extends WeightedRandomLayer<WeightedEntry.Wrapper<ResourceKey<Biome>>>
{
    private final Registry<Biome> biomeRegistry;

    public BiomeInitialLayer(RegistryAccess registryAccess, List<WeightedEntry.Wrapper<ResourceKey<Biome>>> entries)
    {
        super(entries);
        this.biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
    }

    @Override
    protected int getEntryIndex(WeightedEntry.Wrapper<ResourceKey<Biome>> entry)
    {
        return this.resolveId(entry.getData());
    }

    @Override
    protected int getDefaultIndex()
    {
        return this.resolveId(Biomes.OCEAN);
    }

    private int resolveId(ResourceKey<Biome> key)
    {
        if (!this.biomeRegistry.containsKey(key))
            throw new RuntimeException("Attempted to resolve id for unregistered biome " + key);

        return this.biomeRegistry.getId(this.biomeRegistry.get(key));
    }
}
