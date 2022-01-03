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

    public static void addRegisterStructuresCallback(Consumer<StructureMapper> callback)
    {
        registerStructuresCallbacks.add(callback);
    }

    public static void registerStructures(StructureMapper mapper)
    {
        registerStructuresCallbacks.forEach(callback -> callback.accept(mapper));
    }

    public interface StructureMapper extends BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> {}
}
