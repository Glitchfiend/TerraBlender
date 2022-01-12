/*
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
package terrablender.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.command.CommandBiomeParameters;
import terrablender.config.TerraBlenderConfig;
import terrablender.worldgen.TBMultiNoiseBiomeSource;
import terrablender.worldgen.TBNoiseBasedChunkGenerator;
import terrablender.worldgen.TBNoiseGeneratorSettings;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

public class TerraBlender
{
    public static final String MOD_ID = "terrablender";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static TerraBlenderConfig CONFIG;

    public TerraBlender()
    {
    }

    public static void register()
    {
        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(terrablender.core.TerraBlender.MOD_ID, "multi_noise"), TBMultiNoiseBiomeSource.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(terrablender.core.TerraBlender.MOD_ID, "noise"), TBNoiseBasedChunkGenerator.CODEC);
        Registry.register(Registry.RULE, new ResourceLocation(terrablender.core.TerraBlender.MOD_ID, "merged"), NamespacedSurfaceRuleSource.CODEC);
    }

    public static void registerNoiseGeneratorSettings()
    {
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.OVERWORLD, TBNoiseGeneratorSettings.overworld(false, false));
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.LARGE_BIOMES, TBNoiseGeneratorSettings.overworld(false, true));
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.AMPLIFIED, TBNoiseGeneratorSettings.overworld(true, false));
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.NETHER, TBNoiseGeneratorSettings.nether());
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("tb").requires(cs -> cs.hasPermission(2)).then(CommandBiomeParameters.register()));
    }

    public static void setConfig(TerraBlenderConfig config)
    {
        TerraBlender.CONFIG = config;
    }
}
