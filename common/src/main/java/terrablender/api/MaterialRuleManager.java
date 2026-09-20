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
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.worldgen.material.OverworldMaterialRules;
import net.minecraft.data.worldgen.material.NetherMaterialRules;
import net.minecraft.data.worldgen.material.EndMaterialRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import terrablender.worldgen.TBSurfaceRuleData;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MaterialRuleManager
{
    private static final Map<RuleCategory, Map<String, ResourceKey<MaterialRule>>> materialRuleKeys = Maps.newHashMap();
    private static final Map<RuleCategory, Map<String, RuleBuilder>> materialRuleBuilders = Maps.newHashMap();
    private static final Map<RuleCategory, RuleBuilder> defaultMaterialRuleBuilders = Maps.newHashMap();
    private static final Map<RuleCategory, Map<RuleStage, List<Pair<Integer, RuleBuilder>>>> defaultSurfaceRuleInjections = Maps.newHashMap();

    private static final Map<RuleCategory, Map<String, MaterialRule>> runtimeMaterialRules = Maps.newHashMap();
    private static final Map<RuleCategory, MaterialRule> defaultMaterialRules = Maps.newHashMap();

    /**
     * Add the material rules for biomes belonging to a modded namespace.
     * @param category the category to add material rules for.
     * @param namespace the namespace to use these rules for.
     * @param rules the rules to add.
     */
    public static void addRules(RuleCategory category, String namespace, ResourceKey<MaterialRule> rules)
    {
        materialRuleBuilders.get(category).remove(namespace);
        materialRuleKeys.get(category).put(namespace, rules);
    }

    /**
     * Add the material rules for biomes belonging to a modded namespace.
     * @param category the category to add material rules for.
     * @param namespace the namespace to use these rules for.
     * @param rules the rules to add.
     */
    public static void addRules(RuleCategory category, String namespace, RuleBuilder rules)
    {
        materialRuleKeys.get(category).remove(namespace);
        materialRuleBuilders.get(category).put(namespace, rules);
    }

    /**
     * Add material rules to be inserted into the default material rules at a given stage.
     * @param category the category of the material rules.
     * @param ruleStage the stage to add the material rules to.
     * @param priority the priority of the material rules.
     * @param rules the rules to add.
     */
    public static void addToDefaultRulesAtStage(RuleCategory category, RuleStage ruleStage, int priority, RuleBuilder rules)
    {
        defaultSurfaceRuleInjections.get(category).get(ruleStage).add(Pair.of(priority, rules));
    }

    /**
     * Set the default material rules for a category.
     * @param category the category of the material rules.
     * @param rules the new default material rules.
     */
    public static void setDefaultRules(RuleCategory category, RuleBuilder rules)
    {
        defaultMaterialRuleBuilders.put(category, rules);
    }

    /**
     * Remove material rules for biomes belonging to a modded namespace.
     * @param category the category to add material rules for.
     * @param namespace the namespace to use these rules for.
     */
    public static void removeRules(RuleCategory category, String namespace)
    {
        materialRuleKeys.get(category).remove(namespace);
        materialRuleBuilders.get(category).remove(namespace);
    }

    /**
     * Gets the namespaced rules for a given category.
     * @param category the category to get the material rules for.
     * @param fallback the material rules to fallback on.
     * @return the namespaced rules.
     */
    public static MaterialRule getNamespacedRules(RuleCategory category, MaterialRule fallback)
    {
        ImmutableMap.Builder<String, MaterialRule> builder = ImmutableMap.builder();
        if (defaultMaterialRuleBuilders.containsKey(category) || hasDefaultSurfaceRuleInjections(category))
            builder.put("minecraft", getDefaultRules(category));
        builder.putAll(runtimeMaterialRules.get(category));
        Map<String, MaterialRule> rules = builder.build();
        return rules.isEmpty() ? fallback : new NamespacedSurfaceRuleSource(fallback, rules);
    }

    private static boolean hasDefaultSurfaceRuleInjections(RuleCategory category)
    {
        return defaultSurfaceRuleInjections.get(category).values().stream().anyMatch(rules -> !rules.isEmpty());
    }

    /**
     * Get the material rules to be added to a given stage.
     * @param category the category of the material rules.
     * @param ruleStage the stage of the material rules.
     * @return list of the material rules to be added.
     */
    public static List<MaterialRule> getDefaultRuleAdditionsForStage(RuleCategory category, RuleStage ruleStage, HolderGetter<Biome> biomes)
    {
        return defaultSurfaceRuleInjections.get(category).get(ruleStage).stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(p -> p.getSecond().apply(biomes)).collect(ImmutableList.toImmutableList());
    }

    /**
     * Get the default material rules for a category.
     * @param category the category to get the material rules for.
     * @return the default material rules.
     */
    public static MaterialRule getDefaultRules(RuleCategory category)
    {
        return defaultMaterialRules.get(category);
    }

    public static void repopulateRules(RegistryAccess registries)
    {
        repopulateRules(registries.lookupOrThrow(Registries.BIOME), registries.lookupOrThrow(Registries.MATERIAL_RULE));
    }

    private static void repopulateRules(HolderGetter<Biome> biomes, HolderGetter<MaterialRule> materialRules)
    {
        // Repopulate the runtime regular material rules
        runtimeMaterialRules.clear();
        for (RuleCategory category : RuleCategory.values())
            runtimeMaterialRules.put(category, Maps.newHashMap());

        materialRuleBuilders.forEach((category, rules) -> rules.forEach((namespace, builder) -> {
            runtimeMaterialRules.get(category).put(namespace, builder.apply(biomes));
        }));

        materialRuleKeys.forEach((category, rules) -> rules.forEach((namespace, key) -> {
            if (materialRules == null)
                throw new IllegalStateException("Registry access is required to resolve material rule " + key.identifier());
            runtimeMaterialRules.get(category).put(namespace, MaterialRules.getRule(materialRules, key));
        }));

        // Repopulate default rules
        defaultMaterialRules.clear();
        defaultMaterialRules.putAll(
            defaultMaterialRuleBuilders.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().apply(biomes)
            ))
        );

        // Add fallback
        defaultMaterialRules.computeIfAbsent(RuleCategory.OVERWORLD, category -> TBSurfaceRuleData.withStages(category, MaterialRules.getRule(materialRules, OverworldMaterialRules.OVERWORLD), biomes));
        defaultMaterialRules.computeIfAbsent(RuleCategory.NETHER, category -> TBSurfaceRuleData.withStages(category, MaterialRules.getRule(materialRules, NetherMaterialRules.NETHER), biomes));
        defaultMaterialRules.computeIfAbsent(RuleCategory.END, category -> MaterialRules.getRule(materialRules, EndMaterialRules.END));
    }

    /**
     * Categories for material rules to be classed under.
     */
    public enum RuleCategory
    {
        OVERWORLD, NETHER, END
    }

    /**
     * The stage for material rules to be added to.
     */
    public enum RuleStage
    {
        BEFORE_BEDROCK, AFTER_BEDROCK
    }

    static
    {
        for (RuleCategory category : RuleCategory.values())
        {
            // Initialize the material rules map
            materialRuleKeys.put(category, Maps.newHashMap());

            // Initialize the material rule builders map
            materialRuleBuilders.put(category, Maps.newHashMap());

            // Initialize the runtime material rules map
            runtimeMaterialRules.put(category, Maps.newHashMap());

            // Initialize the default material rule injections map
            Map<RuleStage, List<Pair<Integer, RuleBuilder>>> ruleStages = Maps.newHashMap();
            for (RuleStage stage : RuleStage.values())
                ruleStages.put(stage, Lists.newArrayList());
            defaultSurfaceRuleInjections.put(category, ruleStages);
        }
    }

    public interface RuleBuilder extends Function<HolderGetter<Biome>, MaterialRule> {}
}
