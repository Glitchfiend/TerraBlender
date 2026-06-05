package terrablender.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import terrablender.api.data.condition.BiomeMappingConditionType;
import terrablender.api.data.condition.BiomeMappingConditionTypes;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TerraBlenderRegistries {
    public static Supplier<Registry<BiomeMappingConditionType<?>>> BIOME_MAPPING_CONDITION;

    public static void initialize(RegistryBootstrap bootstrap)
    {
        BIOME_MAPPING_CONDITION = bootstrap.create(Registries.BIOME_MAPPING_CONDITION, BiomeMappingConditionTypes::initialize);
    }

    public interface RegistryBootstrap
    {
        <T> Supplier<Registry<T>> create(ResourceKey<Registry<T>> key, Consumer<BiConsumer<Identifier, T>> entryRegistrar);
    }
}
