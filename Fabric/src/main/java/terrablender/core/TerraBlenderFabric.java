/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import terrablender.config.TerraBlenderConfig;
import terrablender.core.TerraBlender;

public class TerraBlenderFabric implements ModInitializer
{
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FabricLoader.getInstance().getConfigDir().resolve(TerraBlender.MOD_ID + ".toml"));

    @Override
    public void onInitialize()
    {
        TerraBlender.setConfig(CONFIG);
        TerraBlender.register();
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> TerraBlender.registerCommands(dispatcher)));
    }

    public static void postInitialize()
    {
        TerraBlender.registerNoiseGeneratorSettings();
    }
}
