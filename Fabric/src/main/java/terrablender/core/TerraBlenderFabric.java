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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import terrablender.api.TerraBlenderApi;
import terrablender.config.TerraBlenderConfig;
import terrablender.util.LevelUtils;

import java.util.Map;

public class TerraBlenderFabric implements ModInitializer
{
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FabricLoader.getInstance().getConfigDir().resolve(TerraBlender.MOD_ID + ".toml"));

    @Override
    public void onInitialize()
    {
        TerraBlender.setConfig(CONFIG);
        TerraBlender.registerBiome((key, biome) -> BuiltinRegistries.register(BuiltinRegistries.BIOME, key, biome.get()));
        TerraBlender.registerRule((key, rule) -> Registry.register(Registry.RULE, new ResourceLocation(TerraBlender.MOD_ID, key), rule.get()));

        FabricLoader.getInstance().getEntrypointContainers("terrablender", TerraBlenderApi.class).forEach(entrypoint -> {
            TerraBlenderApi api = entrypoint.getEntrypoint();
            api.onTerraBlenderInitialized();
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            WorldGenSettings worldGenSettings = server.getWorldData().worldGenSettings();
            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : worldGenSettings.dimensions().entrySet())
            {
                LevelStem stem = entry.getValue();
                LevelUtils.initializeBiomes(stem.typeHolder(), entry.getKey(), stem.generator(),  worldGenSettings.seed());
            }
        });
    }
}
