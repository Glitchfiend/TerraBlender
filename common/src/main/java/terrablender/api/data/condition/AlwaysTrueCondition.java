package terrablender.api.data.condition;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

public class AlwaysTrueCondition extends BiomeMappingCondition {
    @Override
    public boolean test(ResourceKey<Biome> biome, Climate.ParameterPoint parameterPoint) {
        return true;
    }

    @Override
    protected BiomeMappingConditionType<?> getType() {
        return BiomeMappingConditionTypes.ALWAYS_TRUE;
    }
}
