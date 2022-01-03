/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BiomeProviders
{
    public static final ResourceLocation DEFAULT_PROVIDER_LOCATION = new ResourceLocation("minecraft:biome_provider");

    private static LinkedHashMap<ResourceLocation, BiomeProvider> biomeProviders = Maps.newLinkedHashMap();
    private static Map<ResourceLocation, Integer> biomeIndices = Maps.newHashMap();
    private static List<Runnable> indexResetListeners = Lists.newArrayList();

    public static void register(ResourceLocation location, BiomeProvider provider)
    {
        biomeProviders.put(location, provider);
        biomeIndices.put(location, biomeProviders.size() - 1);
    }

    public static void remove(ResourceLocation location)
    {
        biomeProviders.remove(location);
        biomeIndices.clear();
        indexResetListeners.forEach(listener -> listener.run());
    }

    public static ImmutableList<BiomeProvider> get()
    {
        return ImmutableList.copyOf(biomeProviders.values());
    }

    public static void addIndexResetListener(Runnable runnable)
    {
        indexResetListeners.add(runnable);
    }

    public static int getIndex(ResourceLocation location)
    {
        if (biomeIndices.containsKey(location))
            return biomeIndices.get(location);

        int index = ImmutableList.of(biomeProviders.keySet()).indexOf(location);;
        biomeIndices.put(location, index);
        return index;
    }

    public static int getCount()
    {
        return biomeProviders.size();
    }

    static
    {
        register(DEFAULT_PROVIDER_LOCATION, new DefaultBiomeProvider(DEFAULT_PROVIDER_LOCATION, 10));
    }
}
