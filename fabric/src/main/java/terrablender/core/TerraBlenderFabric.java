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

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import terrablender.api.data.RegionDefinition;
import terrablender.api.TerraBlenderApi;
import terrablender.config.TerraBlenderConfig;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TerraBlenderFabric implements ModInitializer
{
    private static final TerraBlenderConfig CONFIG = new TerraBlenderConfig(FabricLoader.getInstance().getConfigDir().resolve(TerraBlender.MOD_ID + ".toml"));

    @Override
    public void onInitialize()
    {
        TerraBlender.setConfig(CONFIG);
        TerraBlenderRegistries.initialize(new TerraBlenderRegistries.RegistryBootstrap() {
            @Override
            public <T> Supplier<Registry<T>> create(ResourceKey<Registry<T>> key, Consumer<BiConsumer<Identifier, T>> entryRegistrar) {
                Registry<T> registry = FabricRegistryBuilder.create(key).buildAndRegister();
                entryRegistrar.accept((id, value) -> Registry.register(registry, id, value));
                return () -> registry;
            }
        });
        DynamicRegistries.registerSynced(Registries.REGION, RegionDefinition.CODEC);

        FabricLoader.getInstance().getEntrypointContainers("terrablender", TerraBlenderApi.class).forEach(entrypoint -> {
            TerraBlenderApi api = entrypoint.getEntrypoint();
            api.onTerraBlenderInitialized();
        });
    }
}
