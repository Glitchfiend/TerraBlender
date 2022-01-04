/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.core;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.worldgen.TBMultiNoiseBiomeSource;
import terrablender.worldgen.TBNoiseBasedChunkGenerator;
import terrablender.worldgen.TBNoiseGeneratorSettings;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mod(value = TerraBlender.MOD_ID)
public class TerraBlender
{
    public static final String MOD_ID = "terrablender";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public TerraBlender()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(TerraBlender.MOD_ID, "multi_noise"), TBMultiNoiseBiomeSource.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(TerraBlender.MOD_ID, "noise"), TBNoiseBasedChunkGenerator.CODEC);
        Registry.register(Registry.RULE, new ResourceLocation(TerraBlender.MOD_ID, "merged"), NamespacedSurfaceRuleSource.CODEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void loadComplete(final FMLLoadCompleteEvent event)
    {
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.OVERWORLD, TBNoiseGeneratorSettings.overworld(false, false));
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.LARGE_BIOMES, TBNoiseGeneratorSettings.overworld(false, true));
        Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, TBNoiseGeneratorSettings.AMPLIFIED, TBNoiseGeneratorSettings.overworld(true, false));
    }
}
