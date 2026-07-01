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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.worldgen.TBSurfaceRuleData;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SurfaceRuleManager
{
    private static final Map<RuleCategory, Map<String, RuleBuilder>> surfaceRuleBuilders = Maps.newHashMap();
    private static final Map<RuleCategory, RuleBuilder> defaultSurfaceRuleBuilders = Maps.newHashMap();
    private static final Map<RuleCategory, Map<RuleStage, List<Pair<Integer, RuleBuilder>>>> defaultSurfaceRuleInjections = Maps.newHashMap();

    private static final Map<RuleCategory, Map<String, SurfaceRules.RuleSource>> surfaceRules = Maps.newHashMap();
    private static final Map<RuleCategory, SurfaceRules.RuleSource> defaultSurfaceRules = Maps.newHashMap();

    /**
     * Add surface rules for biomes belonging to a modded namespace.
     * @param category the category to add surface rules for.
     * @param namespace the namespace to use these rules for.
     * @param rules the rules to add.
     */
    public static void addSurfaceRules(RuleCategory category, String namespace, RuleBuilder rules)
    {
        surfaceRuleBuilders.get(category).put(namespace, rules);
    }

    /**
     * Add surface rules to be inserted into the default surface rules at a given stage.
     * @param category the category of the surface rules.
     * @param ruleStage the stage to add the surface rules to.
     * @param priority the priority of the surface rules.
     * @param rules the rules to add.
     */
    public static void addToDefaultSurfaceRulesAtStage(RuleCategory category, RuleStage ruleStage, int priority, RuleBuilder rules)
    {
        defaultSurfaceRuleInjections.get(category).get(ruleStage).add(Pair.of(priority, rules));
    }

    /**
     * Set the default surface rules for a category.
     * @param category the category of the surface rules.
     * @param rules the new default surface rules.
     */
    public static void setDefaultSurfaceRules(RuleCategory category, RuleBuilder rules)
    {
        defaultSurfaceRuleBuilders.put(category, rules);
    }

    /**
     * Remove surface rules for biomes belonging to a modded namespace.
     * @param category the category to add surface rules for.
     * @param namespace the namespace to use these rules for.
     */
    public static void removeSurfaceRules(RuleCategory category, String namespace)
    {
        surfaceRuleBuilders.get(category).remove(namespace);
    }

    /**
     * Gets the namespaced rules for a given category.
     * @param category the category to get the surface rules for.
     * @param fallback the surface rules to fallback on.
     * @return the namespaced rules.
     */
    public static SurfaceRules.RuleSource getNamespacedRules(RuleCategory category, SurfaceRules.RuleSource fallback)
    {
        ImmutableMap.Builder<String, SurfaceRules.RuleSource> builder = ImmutableMap.builder();
        builder.put("minecraft", getDefaultSurfaceRules(category));
        builder.putAll(surfaceRules.get(category));
        return new NamespacedSurfaceRuleSource(fallback, builder.build());
    }

    /**
     * Get the surface rules to be added to a given stage.
     * @param category the category of the surface rules.
     * @param ruleStage the stage of the surface rules.
     * @return list of the surface rules to be added.
     */
    public static List<SurfaceRules.RuleSource> getDefaultSurfaceRuleAdditionsForStage(RuleCategory category, RuleStage ruleStage, HolderGetter<Biome> biomes)
    {
        return defaultSurfaceRuleInjections.get(category).get(ruleStage).stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(p -> p.getSecond().apply(biomes)).collect(ImmutableList.toImmutableList());
    }

    /**
     * Get the default surface rules for a category.
     * @param category the category to get the surface rules for.
     * @return the default surface rules.
     */
    public static SurfaceRules.RuleSource getDefaultSurfaceRules(RuleCategory category)
    {
        return defaultSurfaceRules.get(category);
    }

    /**
     * INTERNAL
     */
    public static void repopulateRules(HolderGetter<Biome> biomes)
    {
        // Repopulate regular surface rules
        surfaceRules.clear();
        surfaceRules.putAll(
            surfaceRuleBuilders.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            r -> r.getValue().apply(biomes)
                                    ))
                    ))
        );

        // Repopulate default rules
        defaultSurfaceRules.clear();
        defaultSurfaceRules.putAll(
            defaultSurfaceRuleBuilders.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().apply(biomes)
            ))
        );

        defaultSurfaceRules.putIfAbsent(RuleCategory.OVERWORLD, TBSurfaceRuleData.overworld(biomes));
        defaultSurfaceRules.putIfAbsent(RuleCategory.NETHER, TBSurfaceRuleData.nether(biomes));
        defaultSurfaceRules.putIfAbsent(RuleCategory.END, TBSurfaceRuleData.end(biomes));
    }

    /**
     * Categories for surface rules to be classed under.
     */
    public enum RuleCategory
    {
        OVERWORLD, NETHER, END
    }

    /**
     * The stage for surface rules to be added to.
     */
    public enum RuleStage
    {
        BEFORE_BEDROCK, AFTER_BEDROCK
    }

    static
    {
        for (RuleCategory category : RuleCategory.values())
        {
            // Initialize the surface rules map
            surfaceRuleBuilders.put(category, Maps.newHashMap());

            // Initialize the default surface rule injections map
            Map<RuleStage, List<Pair<Integer, RuleBuilder>>> ruleStages = Maps.newHashMap();
            for (RuleStage stage : RuleStage.values())
                ruleStages.put(stage, Lists.newArrayList());
            defaultSurfaceRuleInjections.put(category, ruleStages);
        }
    }

    public interface RuleBuilder extends Function<HolderGetter<Biome>, SurfaceRules.RuleSource> {}
}
