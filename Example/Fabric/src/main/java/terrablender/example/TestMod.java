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
package terrablender.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.BiomeProviders;
import terrablender.api.TerraBlenderApi;

public class TestMod implements ModInitializer, TerraBlenderApi
{
    public static final String MOD_ID = "test";

    @Override
    public void onInitialize()
    {
        ModBiomes.registerBiomes();
    }

    @Override
    public void onTerraBlenderInitialized()
    {
        // Given we only add two biomes, we should keep our weight relatively low.
        BiomeProviders.register(new TestBiomeProvider(new ResourceLocation(MOD_ID, "biome_provider"), 2));
    }
}
