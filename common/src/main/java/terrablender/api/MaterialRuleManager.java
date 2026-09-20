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

import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import terrablender.worldgen.surface.MaterialRuleResolver;

import java.util.List;
import java.util.function.Function;

public class MaterialRuleManager
{
    /**
     * Add the material rules for biomes belonging to a modded namespace.
     * @param category the category to add material rules for.
     * @param namespace the namespace to use these rules for.
     * @param rules the rules to add.
     */
    public static void addRules(RuleCategory category, String namespace, ResourceKey<MaterialRule> rules)
    {
        MaterialRuleResolver.addRules(category, namespace, rules);
    }

    /**
     * Add the material rules for biomes belonging to a modded namespace.
     * @param category the category to add material rules for.
     * @param namespace the namespace to use these rules for.
     * @param rules the rules to add.
     */
    public static void addRules(RuleCategory category, String namespace, RuleBuilder rules)
    {
        MaterialRuleResolver.addRules(category, namespace, rules);
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
        MaterialRuleResolver.addToDefaultRulesAtStage(category, ruleStage, priority, rules);
    }

    /**
     * Set the default material rules for a category.
     * @param category the category of the material rules.
     * @param rules the new default material rules.
     */
    public static void setDefaultRules(RuleCategory category, RuleBuilder rules)
    {
        MaterialRuleResolver.setDefaultRules(category, rules);
    }

    /**
     * Remove material rules for biomes belonging to a modded namespace.
     * @param category the category to add material rules for.
     * @param namespace the namespace to use these rules for.
     */
    public static void removeRules(RuleCategory category, String namespace)
    {
        MaterialRuleResolver.removeRules(category, namespace);
    }

    /**
     * Get the default material rules for a category.
     * @param category the category to get the material rules for.
     * @return the material rules for this category.
     */
    public static MaterialRule getDefaultRules(RuleCategory category)
    {
        return MaterialRuleResolver.getDefaultRules(category);
    }

    /**
     * Get the default material rules for a category.
     * @param category the category to get the material rules for.
     * @param base the fallback material rule
     * @return the default material rules.
     */
    public static MaterialRule getDefaultRules(RuleCategory category, MaterialRule base)
    {
        return MaterialRuleResolver.getDefaultRules(category, base);
    }

    /**
     * Get the material rules to be added to a given stage.
     * @param category the category of the material rules.
     * @param ruleStage the stage of the material rules.
     * @return list of the material rules to be added.
     */
    public static List<MaterialRule> getDefaultRuleAdditionsForStage(RuleCategory category, RuleStage ruleStage, HolderGetter<Biome> biomes)
    {
        return MaterialRuleResolver.getDefaultRuleAdditionsForStage(category, ruleStage, biomes);
    }

    /**
     * Gets the namespaced rules for a given category.
     * @param category the category to get the material rules for.
     * @param fallback the material rules to fallback on.
     * @return the namespaced rules.
     */
    public static MaterialRule getNamespacedRules(RuleCategory category, MaterialRule fallback)
    {
        return MaterialRuleResolver.getNamespacedRules(category, fallback);
    }

    public static void repopulateRules(RegistryAccess registries)
    {
        MaterialRuleResolver.repopulateRules(registries);
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

    public interface RuleBuilder extends Function<HolderGetter<Biome>, MaterialRule> {}
}
