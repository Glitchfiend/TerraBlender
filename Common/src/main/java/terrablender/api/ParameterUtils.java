/**
 * Copyright (C) Glitchfiend
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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

    /**
     * Preset values for temperature parameters.
     */
    public enum Temperature
    {
        ICY(Climate.Parameter.span(-1.0F, -0.45F)),
        COOL(Climate.Parameter.span(-0.45F, -0.15F)),
        NEUTRAL(Climate.Parameter.span(-0.15F, 0.2F)),
        WARM(Climate.Parameter.span(0.2F, 0.55F)),
        HOT(Climate.Parameter.span(0.55F, 1.0F)),
        FROZEN(Climate.Parameter.span(-1.0F, -0.45F)),
        UNFROZEN(Climate.Parameter.span(-0.45F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private Climate.Parameter parameter;

        Temperature(Climate.Parameter parameter)
        {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter()
        {
            return this.parameter;
        }

        public static Climate.Parameter span(Temperature min, Temperature max)
        {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    /**
     * Preset values for humidity parameters.
     */
    public enum Humidity
    {
        ARID(Climate.Parameter.span(-1.0F, -0.35F)),
        DRY(Climate.Parameter.span(-0.35F, -0.1F)),
        NEUTRAL(Climate.Parameter.span(-0.1F, 0.1F)),
        WET(Climate.Parameter.span(0.1F, 0.3F)),
        HUMID(Climate.Parameter.span(0.3F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private Climate.Parameter parameter;

        Humidity(Climate.Parameter parameter)
        {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter()
        {
            return this.parameter;
        }

        public static Climate.Parameter span(Humidity min, Humidity max)
        {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    /**
     * Preset values for continentalness parameters.
     */
    public enum Continentalness
    {
        MUSHROOM_FIELDS(Climate.Parameter.span(-1.2F, -1.05F)),
        DEEP_OCEAN(Climate.Parameter.span(-1.05F, -0.455F)),
        OCEAN(Climate.Parameter.span(-0.455F, -0.19F)),
        COAST(Climate.Parameter.span(-0.19F, -0.11F)),
        NEAR_INLAND(Climate.Parameter.span(-0.11F, 0.03F)),
        MID_INLAND(Climate.Parameter.span(0.03F, 0.3F)),
        FAR_INLAND(Climate.Parameter.span(0.3F, 1.0F)),
        INLAND(Climate.Parameter.span(-0.11F, 0.55F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private Climate.Parameter parameter;

        Continentalness(Climate.Parameter parameter)
        {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter()
        {
            return this.parameter;
        }

        public static Climate.Parameter span(Continentalness min, Continentalness max)
        {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    /**
     * Preset values for erosion parameters.
     */
    public enum Erosion
    {
        EROSION_0(Climate.Parameter.span(-1.0F, -0.78F)),
        EROSION_1(Climate.Parameter.span(-0.78F, -0.375F)),
        EROSION_2(Climate.Parameter.span(-0.375F, -0.2225F)),
        EROSION_3(Climate.Parameter.span(-0.2225F, 0.05F)),
        EROSION_4(Climate.Parameter.span(0.05F, 0.45F)),
        EROSION_5(Climate.Parameter.span(0.45F, 0.55F)),
        EROSION_6(Climate.Parameter.span(0.55F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private Climate.Parameter parameter;

        Erosion(Climate.Parameter parameter)
        {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter()
        {
            return this.parameter;
        }

        public static Climate.Parameter span(Erosion min, Erosion max)
        {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    /**
     * Preset values for depth parameters.
     */
    public enum Depth
    {
        SURFACE(Climate.Parameter.point(0.0F)),
        UNDERGROUND(Climate.Parameter.span(0.2F, 0.9F)),
        FLOOR(Climate.Parameter.point(1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private Climate.Parameter parameter;

        Depth(Climate.Parameter parameter)
        {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter()
        {
            return this.parameter;
        }

        public static Climate.Parameter span(Depth min, Depth max)
        {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    /**
     * Preset values for weirdness parameters.
     */
    public enum Weirdness
    {
        MID_SLICE_NORMAL_ASCENDING(Climate.Parameter.span(-1.0F, -0.93333334F)),
        HIGH_SLICE_NORMAL_ASCENDING(Climate.Parameter.span(-0.93333334F, -0.7666667F)),
        PEAK_NORMAL(Climate.Parameter.span(-0.7666667F, -0.56666666F)),
        HIGH_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.56666666F, -0.4F)),
        MID_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.4F, -0.26666668F)),
        LOW_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.26666668F, -0.05F)),
        VALLEY(Climate.Parameter.span(-0.05F, 0.05F)),
        LOW_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.05F, 0.26666668F)),
        MID_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.26666668F, 0.4F)),
        HIGH_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.4F, 0.56666666F)),
        PEAK_VARIANT(Climate.Parameter.span(0.56666666F, 0.7666667F)),
        HIGH_SLICE_VARIANT_DESCENDING(Climate.Parameter.span(0.7666667F, 0.93333334F)),
        MID_SLICE_VARIANT_DESCENDING(Climate.Parameter.span(0.93333334F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private Climate.Parameter parameter;

        Weirdness(Climate.Parameter parameter)
        {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter()
        {
            return this.parameter;
        }

        public static Climate.Parameter span(Weirdness min, Weirdness max)
        {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }
}
