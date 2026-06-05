package terrablender.api.data.condition;

import com.mojang.serialization.MapCodec;

public interface BiomeMappingConditionType<B extends BiomeMappingCondition>
{
    MapCodec<B> codec();
}
