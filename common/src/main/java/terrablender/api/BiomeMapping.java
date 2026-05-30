package terrablender.api;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.worldgen.RegionUtils;

import java.util.List;

public record BiomeMapping(Climate.ParameterPoint parameters, ResourceKey<Biome> biome)
{
    public static final Codec<BiomeMapping> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(BiomeMapping::parameters),
                    ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeMapping::biome)
            ).apply(instance, BiomeMapping::new));

    public static List<BiomeMapping> addBiomeSimilar(ResourceKey<Biome> similarVanillaBiome, ResourceKey<Biome> biome)
    {
        List<Climate.ParameterPoint> points = RegionUtils.getVanillaParameterPoints(similarVanillaBiome).stream().collect(ImmutableList.toImmutableList());
        return points.stream().map(point -> new BiomeMapping(point, biome)).collect(ImmutableList.toImmutableList());
    }
}
