/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import terrablender.config.TerraBlenderConfig;
import terrablender.core.TerraBlender;

@Mod(value = TerraBlender.MOD_ID)
public class TerraBlenderForge
{
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FMLPaths.CONFIGDIR.get().resolve(TerraBlender.MOD_ID + ".toml"));

    public TerraBlenderForge()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        TerraBlender.setConfig(CONFIG);
        TerraBlender.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void loadComplete(final FMLLoadCompleteEvent event)
    {
        event.enqueueWork(() ->
        {
            TerraBlender.registerNoiseGeneratorSettings();
        });
    }

    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        TerraBlender.registerCommands(event.getDispatcher());
    }
}
