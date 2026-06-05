package terrablender.api.data.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import terrablender.core.TerraBlender;

import java.util.function.BiConsumer;

public class BiomeMappingConditionTypes {
    public static final BiomeMappingConditionType<AlwaysTrueCondition> ALWAYS_TRUE = () -> MapCodec.unit(new AlwaysTrueCondition());

    public static void initialize(BiConsumer<Identifier, BiomeMappingConditionType<?>> registrar)
    {
        registrar.accept(Identifier.fromNamespaceAndPath(TerraBlender.MOD_ID, "always_true"), ALWAYS_TRUE);
    }
}
