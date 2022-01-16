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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.apache.commons.compress.utils.Lists;
import terrablender.worldgen.TBSurfaceRuleData;

import java.util.Comparator;
import java.util.List;

public class GenerationSettings
{
    private static SurfaceRules.RuleSource defaultOverworldSurfaceRules;
    private static SurfaceRules.RuleSource defaultNetherSurfaceRules;

    private static List<Pair<Integer, SurfaceRules.RuleSource>> beforeBedrockOverworldSurfaceRules = Lists.newArrayList();
    private static List<Pair<Integer, SurfaceRules.RuleSource>> afterBedrockOverworldSurfaceRules = Lists.newArrayList();
    private static List<Pair<Integer, SurfaceRules.RuleSource>> beforeBedrockNetherSurfaceRules = Lists.newArrayList();
    private static List<Pair<Integer, SurfaceRules.RuleSource>> afterBedrockNetherSurfaceRules = Lists.newArrayList();

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
     * Add default surface rules to be executed before the bedrock surface rules in the overworld.
     * @param priority the priority of the rules.
     * @param rules the surface rules to add.
     */
    public static void addBeforeBedrockOverworldSurfaceRules(int priority, SurfaceRules.RuleSource rules)
    {
        beforeBedrockOverworldSurfaceRules.add(Pair.of(priority, rules));
    }

    /**
     * Add default surface rules to be executed before the bedrock surface rules in the overworld.
     * @param rules the surface rules to add.
     */
    public static void addBeforeBedrockOverworldSurfaceRules(SurfaceRules.RuleSource rules)
    {
        addBeforeBedrockOverworldSurfaceRules(0, rules);
    }

    /**
     * Add default surface rules to be executed after the bedrock surface rules in the overworld.
     * @param priority the priority of the rules.
     * @param rules the surface rules to add.
     */
    public static void addAfterBedrockOverworldSurfaceRules(int priority, SurfaceRules.RuleSource rules)
    {
        afterBedrockOverworldSurfaceRules.add(Pair.of(priority, rules));
    }

    /**
     * Add default surface rules to be executed after the bedrock surface rules in the overworld.
     * @param rules the surface rules to add.
     */
    public static void addAfterBedrockOverworldSurfaceRules(SurfaceRules.RuleSource rules)
    {
        addAfterBedrockOverworldSurfaceRules(0, rules);
    }

    /**
     * Add default surface rules to be executed before the bedrock surface rules in the nether.
     * @param priority the priority of the rules.
     * @param rules the surface rules to add.
     */
    public static void addBeforeBedrockNetherSurfaceRules(int priority, SurfaceRules.RuleSource rules)
    {
        beforeBedrockNetherSurfaceRules.add(Pair.of(priority, rules));
    }

    /**
     * Add default surface rules to be executed before the bedrock surface rules in the nether.
     * @param rules the surface rules to add.
     */
    public static void addBeforeBedrockNetherSurfaceRules(SurfaceRules.RuleSource rules)
    {
        addBeforeBedrockNetherSurfaceRules(0, rules);
    }

    /**
     * Add default surface rules to be executed after the bedrock surface rules in the nether.
     * @param priority the priority of the rules.
     * @param rules the surface rules to add.
     */
    public static void addAfterBedrockNetherSurfaceRules(int priority, SurfaceRules.RuleSource rules)
    {
        afterBedrockNetherSurfaceRules.add(Pair.of(priority, rules));
    }

    /**
     * Add default surface rules to be executed after the bedrock surface rules in the nether.
     * @param rules the surface rules to add.
     */
    public static void addAfterBedrockNetherSurfaceRules(SurfaceRules.RuleSource rules)
    {
        addAfterBedrockNetherSurfaceRules(0, rules);
    }

    /**
     * Get the default overworld surface rules.
     * @return the default overworld surface rules.
     */
    public static SurfaceRules.RuleSource getDefaultOverworldSurfaceRules()
    {
        if (defaultOverworldSurfaceRules == null)
            defaultOverworldSurfaceRules = TBSurfaceRuleData.overworld();

        return defaultOverworldSurfaceRules;
    }

    /**
     * Get the default nether surface rules.
     * @return the default nether surface rules.
     */
    public static SurfaceRules.RuleSource getDefaultNetherSurfaceRules()
    {
        if (defaultNetherSurfaceRules == null)
            defaultNetherSurfaceRules = TBSurfaceRuleData.nether();

        return defaultNetherSurfaceRules;
    }

    /**
     * Get the list of default surface rules to be executed before the bedrock surface rules in the overworld.
     * @return the list of surface rules.
     */
    public static List<SurfaceRules.RuleSource> getBeforeBedrockOverworldSurfaceRules()
    {
        return beforeBedrockOverworldSurfaceRules.stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(Pair::getSecond).collect(ImmutableList.toImmutableList());
    }

    /**
     * Get the list of default surface rules to be executed after the bedrock surface rules in the overworld.
     * @return the list of surface rules.
     */
    public static List<SurfaceRules.RuleSource> getAfterBedrockOverworldSurfaceRules()
    {
        return afterBedrockOverworldSurfaceRules.stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(Pair::getSecond).collect(ImmutableList.toImmutableList());
    }

    /**
     * Get the list of default surface rules to be executed before the bedrock surface rules in the nether.
     * @return the list of surface rules.
     */
    public static List<SurfaceRules.RuleSource> getBeforeBedrockNetherSurfaceRules()
    {
        return beforeBedrockNetherSurfaceRules.stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(Pair::getSecond).collect(ImmutableList.toImmutableList());
    }

    /**
     * Get the list of default surface rules to be executed after the bedrock surface rules in the nether.
     * @return the list of surface rules.
     */
    public static List<SurfaceRules.RuleSource> getAfterBedrockNetherSurfaceRules()
    {
        return afterBedrockNetherSurfaceRules.stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(Pair::getSecond).collect(ImmutableList.toImmutableList());
    }
}
