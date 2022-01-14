/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.example;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.BiomeProvider;

import terrablender.worldgen.TBClimate;

import java.util.Optional;
import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;

public class TestBiomeProvider extends BiomeProvider
{
    public TestBiomeProvider(ResourceLocation name, int overworldWeight)
    {
        super(name, overworldWeight);
    }

    @Override
    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            // Simple example:
            // Replace the Vanilla desert with our hot_red biome
            builder.replaceBiome(Biomes.DESERT, TestBiomes.HOT_RED);

            // More complex example:
            // Replace specific parameter points for the frozen peaks with our cold_blue biome
            for (int i = 0; i <= Temperature.NEUTRAL.ordinal(); i++)
            {
                Temperature temperature = Temperature.values()[i];
                for (int j = 0; j <= Humidity.HUMID.ordinal(); j++)
                {
                    Humidity humidity = Humidity.values()[j];
                    for (int k = 0; k <= Erosion.EROSION_1.ordinal(); k++)
                    {
                        Erosion erosion = Erosion.values()[k];
                        for (int l = Weirdness.HIGH_SLICE_VARIANT_ASCENDING.ordinal(); l <= Weirdness.HIGH_SLICE_VARIANT_DESCENDING.ordinal(); l++)
                        {
                            Weirdness weirdness = Weirdness.values()[l];
                            Climate.ParameterPoint coastToFarFrozenPeaksPoint = Climate.parameters(
                                    temperature.parameter(),
                                    humidity.parameter(),
                                    Continentalness.span(Continentalness.COAST, Continentalness.FAR_INLAND),
                                    erosion.parameter(),
                                    Depth.SURFACE.parameter(),
                                    weirdness.parameter(),
                                    0.0F
                            );

                            Climate.ParameterPoint midToFarFrozenPeaksPoint = Climate.parameters(
                                    temperature.parameter(),
                                    humidity.parameter(),
                                    Continentalness.span(Continentalness.MID_INLAND, Continentalness.FAR_INLAND),
                                    erosion.parameter(),
                                    Depth.SURFACE.parameter(),
                                    weirdness.parameter(),
                                    0.0F
                            );
                            builder.replaceBiome(coastToFarFrozenPeaksPoint, TestBiomes.COLD_BLUE);
                            builder.replaceBiome(midToFarFrozenPeaksPoint, TestBiomes.COLD_BLUE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public Optional<SurfaceRules.RuleSource> getOverworldSurfaceRules()
    {
        return Optional.of(TestSurfaceRuleData.makeRules());
    }
}
