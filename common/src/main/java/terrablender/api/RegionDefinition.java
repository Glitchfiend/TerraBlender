package terrablender.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public record RegionDefinition(Identifier name, RegionType type, int weight, List<BiomeMapping> biomeMappings)
{
    public static final Codec<RegionDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("name").forGetter(RegionDefinition::name),
            RegionType.CODEC.fieldOf("type").forGetter(RegionDefinition::type),
            Codec.INT.fieldOf("weight").forGetter(RegionDefinition::weight),
            BiomeMapping.CODEC.listOf().fieldOf("biomeMappings").forGetter(RegionDefinition::biomeMappings)
    ).apply(instance, RegionDefinition::new));
}
