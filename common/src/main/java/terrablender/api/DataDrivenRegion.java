package terrablender.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.function.Consumer;

public class DataDrivenRegion extends Region
{
    public static final Codec<DataDrivenRegion> CODEC = RecordCodecBuilder.create(regionInstance -> regionInstance.group(
            RegionDefinition.CODEC.fieldOf("definition").forGetter(region -> region.definition)
    ).apply(regionInstance, DataDrivenRegion::new));

    private final RegionDefinition definition;

    public DataDrivenRegion(RegionDefinition regionDefinition)
    {
        super(regionDefinition.name(), regionDefinition.type(), regionDefinition.weight());
        this.definition = regionDefinition;
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        for (BiomeMapping mapping : definition.biomeMappings())
        {
            mapper.accept(Pair.of(mapping.parameters(), mapping.biome()));
        }
    }
}


