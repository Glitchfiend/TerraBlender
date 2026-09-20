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

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.joml.Vector3f;

public class TestBiomes
{
    public static final ResourceKey<Biome> HOT_RED = register("hot_red");
    public static final ResourceKey<Biome> COLD_BLUE = register("cold_blue");

    public static void bootstrap(BootstrapContext<Biome> context)
    {
        context.register(HOT_RED, createBiome(2.0F));
        context.register(COLD_BLUE, createBiome(-0.7F));
    }

    private static Biome createBiome(float temperature)
    {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(temperature).downfall(0.0F)
                .setAttribute(EnvironmentAttributes.FOG_COLOR, new Vector3f(192 / 255.0F, 216 / 255.0F, 1.0F))
                .setAttribute(EnvironmentAttributes.SKY_COLOR, new Vector3f(110 / 255.0F, 177 / 255.0F, 1.0F))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, new Vector3f(5 / 255.0F, 5 / 255.0F, 51 / 255.0F))
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).build())
                .mobSpawnSettings(MobSpawnSettings.NO_SPAWNS)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build();
    }

    private static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(TestMod.MOD_ID, name));
    }
}
