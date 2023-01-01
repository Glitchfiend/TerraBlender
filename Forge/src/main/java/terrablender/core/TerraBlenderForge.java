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
package terrablender.core;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import terrablender.api.VanillaParameterOverlayBuilder;
import terrablender.config.TerraBlenderConfig;

@Mod(value = TerraBlender.MOD_ID)
public class TerraBlenderForge {
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FMLPaths.CONFIGDIR.get().resolve(TerraBlender.MOD_ID + ".toml"));

    public TerraBlenderForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
        TerraBlender.setConfig(CONFIG);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        builder.add(Climate.parameters(Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), 0), Biomes.FOREST);
        builder.add(Climate.parameters(Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(0.2F, 0.3F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), Climate.Parameter.span(-1.0F, -0.5F), 0), Biomes.FOREST);
        var points = builder.build();

        for (var point : points)
        {
            TerraBlender.LOGGER.info(point);
        }
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
    }
}
