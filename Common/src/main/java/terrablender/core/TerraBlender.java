/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
