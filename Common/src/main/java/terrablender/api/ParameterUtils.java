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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;

import java.util.ArrayList;
import java.util.Arrays;
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
     * Builds a list of parameter points based on a list of parameters.
     */
    public static class ParameterPointListBuilder
    {
        private List<Climate.Parameter> temperatures = Lists.newArrayList();
        private List<Climate.Parameter> humidities = Lists.newArrayList();
        private List<Climate.Parameter> continentalnesses = Lists.newArrayList();
        private List<Climate.Parameter> erosions = Lists.newArrayList();
        private List<Climate.Parameter> depths = Lists.newArrayList();
        private List<Climate.Parameter> weirdnesses = Lists.newArrayList();
        private List<Climate.Parameter> uniquenesses = Lists.newArrayList();
        private List<Long> offsets = Lists.newArrayList();

        /**
         * Adds temperature parameters to the list.
         * @param values values to be added.
         */
        public void temperature(Climate.Parameter... values)
        {
            this.temperatures.addAll(Arrays.asList(values));
        }

        /**
         * Adds temperature values to the list.
         * @param values values to be added.
         */
        public void temperature(Temperature... values)
        {
            this.temperatures.addAll(Arrays.asList(values).stream().map(Temperature::parameter).toList());
        }

        /**
         * Adds humidity parameters to the list.
         * @param values values to be added.
         */
        public void humidity(Climate.Parameter... values)
        {
            this.humidities.addAll(Arrays.asList(values));
        }

        /**
         * Adds humidity values to the list.
         * @param values values to be added.
         */
        public void humidity(Humidity... values)
        {
            this.humidities.addAll(Arrays.asList(values).stream().map(Humidity::parameter).toList());
        }

        /**
         * Adds continentalness parameters to the list.
         * @param values values to be added.
         */
        public void continentalness(Climate.Parameter... values)
        {
            this.continentalnesses.addAll(Arrays.asList(values));
        }

        /**
         * Adds continentalness values to the list.
         * @param values values to be added.
         */
        public void continentalness(Continentalness... values)
        {
            this.continentalnesses.addAll(Arrays.asList(values).stream().map(Continentalness::parameter).toList());
        }

        /**
         * Adds erosion parameters to the list.
         * @param values values to be added.
         */
        public void erosion(Climate.Parameter... values)
        {
            this.erosions.addAll(Arrays.asList(values));
        }

        /**
         * Adds erosion values to the list.
         * @param values values to be added.
         */
        public void erosion(Erosion... values)
        {
            this.erosions.addAll(Arrays.asList(values).stream().map(Erosion::parameter).toList());
        }

        /**
         * Adds depth parameters to the list.
         * @param values values to be added.
         */
        public void depth(Climate.Parameter... values)
        {
            this.depths.addAll(Arrays.asList(values));
        }

        /**
         * Adds depth values to the list.
         * @param values values to be added.
         */
        public void depth(Depth... values)
        {
            this.depths.addAll(Arrays.asList(values).stream().map(Depth::parameter).toList());
        }

        /**
         * Adds weirdness parameters to the list.
         * @param values values to be added.
         */
        public void weirdness(Climate.Parameter... values)
        {
            this.weirdnesses.addAll(Arrays.asList(values));
        }

        /**
         * Adds weirdness values to the list.
         * @param values values to be added.
         */
        public void weirdness(Weirdness... values)
        {
            this.weirdnesses.addAll(Arrays.asList(values).stream().map(Weirdness::parameter).toList());
        }

        /**
         * Adds uniqueness parameters to the list.
         * @param values values to be added.
         */
        public void uniqueness(Climate.Parameter... values)
        {
            this.uniquenesses.addAll(Arrays.asList(values));
        }

        /**
         * Adds uniqueness values to the list.
         * @param values values to be added.
         */
        public void uniqueness(Integer... values)
        {
            this.uniquenesses.addAll(Arrays.asList(values).stream().map(BiomeProviderUtils::getUniquenessParameter).toList());
        }

        /**
         * Adds offset values to the list.
         * @param values values to be added.
         */
        public void offset(Float... values)
        {
            this.offsets.addAll(Arrays.asList(values).stream().map(Climate::quantizeCoord).toList());
        }

        /**
         * Adds offset values to the list.
         * @param values values to be added.
         */
        public void offset(Long... values)
        {
            this.offsets.addAll(Arrays.asList(values));
        }

        /**
         * Builds a list of {@link TBClimate.ParameterPoint}
         * @return the parameter point list.
         */
        public List<TBClimate.ParameterPoint> build()
        {
            this.populateIfEmpty();
            ImmutableList.Builder<TBClimate.ParameterPoint> builder = new ImmutableList.Builder<>();
            this.temperatures.forEach(temperature -> this.humidities.forEach(humidity -> this.continentalnesses.forEach(continentalness -> this.erosions.forEach(erosion ->
                this.depths.forEach(depth -> this.weirdnesses.forEach(weirdness -> this.uniquenesses.forEach(uniqueness -> this.offsets.forEach(offset -> {
                    builder.add(new TBClimate.ParameterPoint(temperature, humidity, continentalness, erosion, depth, weirdness, uniqueness, offset));
                }))))))));
            return builder.build();
        }

        /**
         * Builds a list of {@link Climate.ParameterPoint}
         * @return the parameter point list.
         */
        public List<Climate.ParameterPoint> buildVanilla()
        {
            return build().stream().map(point -> new Climate.ParameterPoint(point.temperature(), point.humidity(), point.continentalness(), point.erosion(), point.depth(), point.weirdness(), point.offset())).collect(ImmutableList.toImmutableList());
        }

        private void populateIfEmpty()
        {
            if (this.temperatures.isEmpty()) this.temperatures.add(Temperature.FULL_RANGE.parameter());
            if (this.humidities.isEmpty()) this.humidities.add(Humidity.FULL_RANGE.parameter());
            if (this.continentalnesses.isEmpty()) this.continentalnesses.add(Continentalness.FULL_RANGE.parameter());
            if (this.erosions.isEmpty()) this.erosions.add(Erosion.FULL_RANGE.parameter());
            if (this.depths.isEmpty()) this.depths.add(Depth.FULL_RANGE.parameter());
            if (this.weirdnesses.isEmpty()) this.weirdnesses.add(Weirdness.FULL_RANGE.parameter());
            if (this.uniquenesses.isEmpty()) this.uniquenesses.add(BiomeProviderUtils.getUniquenessParameter(0));
            if (this.offsets.isEmpty()) this.offsets.add(Climate.quantizeCoord(0.0F));
        }
    };

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
