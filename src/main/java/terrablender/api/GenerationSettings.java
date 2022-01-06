/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
