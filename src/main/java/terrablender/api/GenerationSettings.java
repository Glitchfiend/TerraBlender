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
    private static boolean replaceDefaultWorldtypes = false;
    private static SurfaceRules.RuleSource defaultOverworldSurfaceRules = SurfaceRuleData.overworld();
    private static SurfaceRules.RuleSource defaultNetherSurfaceRules = SurfaceRuleData.nether();

    public static void setReplaceDefaultWorldtypes(boolean value)
    {
        replaceDefaultWorldtypes = value;
    }

    public static void setDefaultOverworldSurfaceRules(SurfaceRules.RuleSource rules)
    {
        defaultOverworldSurfaceRules = rules;
    }

    public static void setDefaultNetherSurfaceRules(SurfaceRules.RuleSource rules)
    {
        defaultNetherSurfaceRules = rules;
    }

    public static boolean getReplaceDefaultWorldTypes()
    {
        return replaceDefaultWorldtypes;
    }

    public static SurfaceRules.RuleSource getDefaultOverworldSurfaceRules()
    {
        return defaultOverworldSurfaceRules;
    }

    public static SurfaceRules.RuleSource getDefaultNetherSurfaceRules()
    {
        return defaultNetherSurfaceRules;
    }
}
