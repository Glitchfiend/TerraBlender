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
package terrablender.core;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import terrablender.config.TerraBlenderConfig;

@Mod(value = TerraBlender.MOD_ID)
public class TerraBlenderForge
{
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FMLPaths.CONFIGDIR.get().resolve(TerraBlender.MOD_ID + ".toml"));

    public TerraBlenderForge()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        TerraBlender.setConfig(CONFIG);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void loadComplete(final FMLLoadCompleteEvent event)
    {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class Handler
    {
        @SubscribeEvent
        public static void onRegisterBiomes(RegistryEvent.Register<Biome> event)
        {
            TerraBlender.register((key, biome) -> {
                biome.setRegistryName(key.location());
                event.getRegistry().register(biome);
            });
        }
    }
}
