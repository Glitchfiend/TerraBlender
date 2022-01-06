/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BiomeStructures
{
    private static List<Consumer<StructureMapper>> registerStructuresCallbacks = Lists.newArrayList();

    /**
     * Add a callback for registering biomes to certain {@link ConfiguredStructureFeature ConfiguredStructureFeatures}.
     * This is called during {@link terrablender.worldgen.TBStructureSettings TBStructureSettings} construction.
     * @param callback the callback.
     */
    public static void addRegisterStructuresCallback(Consumer<StructureMapper> callback)
    {
        registerStructuresCallbacks.add(callback);
    }

    /**
     * Registers all structures using the provided {@link StructureMapper}.
     * This is normally used by {@link terrablender.worldgen.TBStructureSettings TBStructureSettings}.
     * @param mapper
     */
    public static void registerStructures(StructureMapper mapper)
    {
        registerStructuresCallbacks.forEach(callback -> callback.accept(mapper));
    }

    /** An interface for a type of {@link java.util.function.BiConsumer BiConsumer} which maps {@link net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature ConfiguredStructureFeatures}
     * to {@link net.minecraft.resources.ResourceKey ResourceKeys}. */
    public interface StructureMapper extends BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> {}
}
