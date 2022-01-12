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

import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Optional;

/**
 * Called using the terrablender entrypoint.
 */
public interface TerraBlenderApi
{
    /**
     * Get the default overworld surface rules.
     * @return the default overworld surface rules.
     */
    default Optional<SurfaceRules.RuleSource> getDefaultOverworldSurfaceRules()
    {
        return Optional.empty();
    }

    /**
     * Get the default nether surface rules.
     * @return the default nether surface rules.
     */
    default Optional<SurfaceRules.RuleSource> getDefaultNetherSurfaceRules()
    {
        return Optional.empty();
    }
}
