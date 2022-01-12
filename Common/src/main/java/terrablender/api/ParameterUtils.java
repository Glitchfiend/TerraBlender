/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;

import java.util.List;

public class ParameterUtils
{
    /**
     * Get a list of {@link Climate.ParameterPoint Climate.ParameterPoints} that are used for a given Vanilla biome.
     * @param biome the biome to find the parameters of.
     * @return a list of parameter points.
     */
    public static List<Climate.ParameterPoint> getVanillaParameterPoints(ResourceKey<Biome> biome)
    {
        return BiomeProviderUtils.getVanillaParameterPoints(biome);
    }

    /**
     * Convert from Vanilla's {@link Climate.ParameterPoint} to TerraBlender's {@link TBClimate.ParameterPoint}.
     * @param point the parameter point to convert.
     * @param uniqueness the uniqueness for the converted parameter point.
     * @return the converted parameter point.
     */
    public static TBClimate.ParameterPoint convertParameterPoint(Climate.ParameterPoint point, Climate.Parameter uniqueness)
    {
        return TBClimate.parameters(point.temperature(), point.humidity(), point.continentalness(), point.erosion(), point.depth(), point.weirdness(), uniqueness, Climate.unquantizeCoord(point.offset()));
    }
}
