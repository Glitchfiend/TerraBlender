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
package terrablender.handler;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.registries.*;
import terrablender.api.data.RegionDefinition;
import terrablender.api.Regions;
import terrablender.core.Registries;
import terrablender.core.TerraBlenderRegistries;
import terrablender.util.LevelUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InitializationHandler
{
    public static void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        LevelUtils.initializeOnServerStart(event.getServer());
        Regions.loadDataDrivenRegions(event.getServer().registryAccess());
    }

    public static void onRegisterDataPackRegistries(DataPackRegistryEvent.NewRegistry event)
    {
        event.dataPackRegistry(Registries.REGION, RegionDefinition.CODEC);
    }

    public static void onNewRegistry(NewRegistryEvent event)
    {
        TerraBlenderRegistries.initialize(new TerraBlenderRegistries.RegistryBootstrap() {
            @Override
            public <T> Supplier<Registry<T>> create(ResourceKey<Registry<T>> key, Consumer<BiConsumer<Identifier, T>> entryRegistrar) {
                if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<? extends Registry<?>> mappedRegistry) {
                    mappedRegistry.unfreeze();
                }

                Registry<T> registry = BuiltInRegistries.registerSimple(key, _ -> new Object());

                if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<? extends Registry<?>> mappedRegistry) {
                    mappedRegistry.freeze();
                }
                return () -> registry;
            }
        });
    }
}
