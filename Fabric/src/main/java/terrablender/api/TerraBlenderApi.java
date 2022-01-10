/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
