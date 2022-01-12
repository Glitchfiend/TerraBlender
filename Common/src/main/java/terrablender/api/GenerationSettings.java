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
package terrablender.api;

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenSettings;

import java.util.Optional;

public class GenerationSettings
{
    private static SurfaceRules.RuleSource defaultOverworldSurfaceRules = SurfaceRuleData.overworld();
    private static SurfaceRules.RuleSource defaultNetherSurfaceRules = SurfaceRuleData.nether();

    /**
     * Set the default overworld surface rules. This is used when a {@link BiomeProvider BiomeProvider} does not specify its own rules.
     * @param rules surface rules.
     */
    public static void setDefaultOverworldSurfaceRules(SurfaceRules.RuleSource rules)
    {
        defaultOverworldSurfaceRules = rules;
    }

    /**
     * Set the default nether surface rules. This is used when a {@link BiomeProvider BiomeProvider} does not specify its own rules.
     * @param rules surface rules.
     */
    public static void setDefaultNetherSurfaceRules(SurfaceRules.RuleSource rules)
    {
        defaultNetherSurfaceRules = rules;
    }

    /**
     * Get the default overworld surface rules.
     * @return the default overworld surface rules.
     */
    public static SurfaceRules.RuleSource getDefaultOverworldSurfaceRules()
    {
        return defaultOverworldSurfaceRules;
    }

    /**
     * Get the default nether surface rules.
     * @return the default nether surface rules.
     */
    public static SurfaceRules.RuleSource getDefaultNetherSurfaceRules()
    {
        return defaultNetherSurfaceRules;
    }
}
