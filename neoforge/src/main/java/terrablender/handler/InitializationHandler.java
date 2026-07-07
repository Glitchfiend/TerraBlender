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


import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import terrablender.api.data.RegionDefinition;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import terrablender.api.Regions;
import terrablender.core.Registries;
import terrablender.core.TerraBlenderRegistries;
import terrablender.util.LevelUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InitializationHandler
{
    // Use the lowest priority to account for nonsense from e.g. MCreator.
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
                Registry<T> registry = event.create(new RegistryBuilder<>(key));
                entryRegistrar.accept((id, value) -> Registry.register(registry, id, value));
                return () -> registry;
            }
        });
    }
}
