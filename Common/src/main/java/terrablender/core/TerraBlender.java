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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.config.TerraBlenderConfig;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

public class TerraBlender
{
    public static final String MOD_ID = "terrablender";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static TerraBlenderConfig CONFIG;

    public TerraBlender()
    {
    }

    public static void register()
    {
        Registry.register(Registry.RULE, new ResourceLocation(terrablender.core.TerraBlender.MOD_ID, "merged"), NamespacedSurfaceRuleSource.CODEC);
    }

    public static void setConfig(TerraBlenderConfig config)
    {
        TerraBlender.CONFIG = config;
    }
}
