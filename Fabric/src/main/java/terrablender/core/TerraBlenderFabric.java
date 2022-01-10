/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.GenerationSettings;
import terrablender.api.TerraBlenderApi;
import terrablender.config.TerraBlenderConfig;

import java.util.Optional;

public class TerraBlenderFabric implements ModInitializer
{
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FabricLoader.getInstance().getConfigDir().resolve(TerraBlender.MOD_ID + ".toml"));

    @Override
    public void onInitialize()
    {
        TerraBlender.setConfig(CONFIG);
        TerraBlender.register();
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> TerraBlender.registerCommands(dispatcher)));

        FabricLoader.getInstance().getEntrypointContainers("terrablender", TerraBlenderApi.class).forEach(entrypoint -> {
            TerraBlenderApi api = entrypoint.getEntrypoint();
            Optional<SurfaceRules.RuleSource> defaultOverworldSurfaceRules = api.getDefaultOverworldSurfaceRules();
            Optional<SurfaceRules.RuleSource> defaultNetherSurfaceRules = api.getDefaultNetherSurfaceRules();

            if (defaultOverworldSurfaceRules.isPresent()) GenerationSettings.setDefaultOverworldSurfaceRules(defaultOverworldSurfaceRules.get());
            if (defaultNetherSurfaceRules.isPresent()) GenerationSettings.setDefaultNetherSurfaceRules(defaultNetherSurfaceRules.get());
        });

        TerraBlender.registerNoiseGeneratorSettings();
    }
}
