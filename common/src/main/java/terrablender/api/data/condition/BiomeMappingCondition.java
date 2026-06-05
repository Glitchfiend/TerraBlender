package terrablender.api.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.core.TerraBlenderRegistries;

public abstract class BiomeMappingCondition {
    public static final Codec<BiomeMappingCondition> CODEC = TerraBlenderRegistries.BIOME_MAPPING_CONDITION.get().byNameCodec().dispatch(BiomeMappingCondition::getType, BiomeMappingConditionType::codec);

    public abstract boolean test(ResourceKey<Biome> biome, Climate.ParameterPoint parameterPoint);

    protected abstract BiomeMappingConditionType<?> getType();
}
