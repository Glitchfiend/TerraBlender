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
package terrablender.worldgen.surface;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.material.VanillaMaterialRules;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.SequenceRule;
import terrablender.api.MaterialRuleManager;
import terrablender.api.MaterialRuleManager.RuleBuilder;
import terrablender.api.MaterialRuleManager.RuleCategory;
import terrablender.api.MaterialRuleManager.RuleStage;

import java.util.Comparator;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MaterialRuleResolver
{
    private static final Map<RuleCategory, Map<String, ResourceKey<MaterialRule>>> materialRuleKeys = Maps.newHashMap();
    private static final Map<RuleCategory, Map<String, RuleBuilder>> materialRuleBuilders = Maps.newHashMap();
    private static final Map<RuleCategory, RuleBuilder> defaultMaterialRuleBuilders = Maps.newHashMap();
    private static final Map<RuleCategory, Map<RuleStage, List<Pair<Integer, RuleBuilder>>>> defaultSurfaceRuleInjections = Maps.newHashMap();

    private static final Map<RuleCategory, Map<String, MaterialRule>> runtimeMaterialRules = Maps.newHashMap();
    private static final Map<RuleCategory, MaterialRule> defaultMaterialRules = Maps.newHashMap();

    private static RegistryAccess registryAccess;

    public static void addRules(RuleCategory category, String namespace, ResourceKey<MaterialRule> rules)
    {
        materialRuleBuilders.get(category).remove(namespace);
        materialRuleKeys.get(category).put(namespace, rules);
    }

    public static void addRules(RuleCategory category, String namespace, RuleBuilder rules)
    {
        materialRuleKeys.get(category).remove(namespace);
        materialRuleBuilders.get(category).put(namespace, rules);
    }

    public static void addToDefaultRulesAtStage(RuleCategory category, RuleStage ruleStage, int priority, RuleBuilder rules)
    {
        defaultSurfaceRuleInjections.get(category).get(ruleStage).add(Pair.of(priority, rules));
    }

    public static void setDefaultRules(RuleCategory category, RuleBuilder rules)
    {
        defaultMaterialRuleBuilders.put(category, rules);
    }

    public static void removeRules(RuleCategory category, String namespace)
    {
        materialRuleKeys.get(category).remove(namespace);
        materialRuleBuilders.get(category).remove(namespace);
    }

    public static MaterialRule getNamespacedRules(RuleCategory category, MaterialRule fallback)
    {
        ImmutableMap.Builder<String, MaterialRule> builder = ImmutableMap.builder();
        if (defaultMaterialRuleBuilders.containsKey(category) || hasDefaultSurfaceRuleInjections(category))
            builder.put("minecraft", getDefaultRules(category, fallback));
        builder.putAll(runtimeMaterialRules.get(category));
        Map<String, MaterialRule> rules = builder.build();
        if (rules.isEmpty())
            return fallback;

        return new NamespacedSurfaceRuleSource(fallback, rules);
    }

    private static MaterialRule unwrapHolders(MaterialRule rule)
    {
        Set<MaterialRule> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        MaterialRule resolved = rule;
        while (resolved instanceof MaterialRule.HolderHolder(Holder<MaterialRule> holder))
        {
            if (!visited.add(resolved))
                throw new IllegalStateException("Cyclic material rule reference");
            resolved = holder.value();
        }
        return resolved;
    }

    private static boolean hasDefaultSurfaceRuleInjections(RuleCategory category)
    {
        return defaultSurfaceRuleInjections.get(category).values().stream().anyMatch(rules -> !rules.isEmpty());
    }

    public static List<MaterialRule> getDefaultRuleAdditionsForStage(RuleCategory category, RuleStage ruleStage, RegistryAccess registries)
    {
        return defaultSurfaceRuleInjections.get(category).get(ruleStage).stream().sorted(Comparator.comparing(Pair::getFirst, Comparator.reverseOrder())).map(p -> p.getSecond().apply(registries)).collect(ImmutableList.toImmutableList());
    }

    public static MaterialRule getDefaultRules(RuleCategory category)
    {
        return defaultMaterialRules.get(category);
    }

    public static MaterialRule getDefaultRules(RuleCategory category, MaterialRule base)
    {
        MaterialRule override = defaultMaterialRules.get(category);
        if (override != null)
            return override;
        if (registryAccess == null && hasDefaultSurfaceRuleInjections(category))
            throw new IllegalStateException("Called getDefaultRules for " + category + " before repopulateRules was called");
        return withStages(category, base, registryAccess);
    }

    public static void repopulateRules(RegistryAccess registries)
    {
        HolderGetter<MaterialRule> materialRules = registries.lookupOrThrow(Registries.MATERIAL_RULE);
        registryAccess = registries;
        runtimeMaterialRules.clear();
        for (RuleCategory category : RuleCategory.values())
            runtimeMaterialRules.put(category, Maps.newHashMap());

        materialRuleBuilders.forEach((category, rules) -> rules.forEach((namespace, builder) -> {
            runtimeMaterialRules.get(category).put(namespace, builder.apply(registries));
        }));

        materialRuleKeys.forEach((category, rules) -> rules.forEach((namespace, key) ->
            runtimeMaterialRules.get(category).put(namespace, MaterialRules.getRule(materialRules, key))));
        defaultMaterialRules.clear();
        defaultMaterialRules.putAll(
            defaultMaterialRuleBuilders.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().apply(registries)
            ))
        );
    }

    public static MaterialRule withStages(RuleCategory category, MaterialRule vanilla, RegistryAccess registries)
    {
        List<MaterialRule> beforeBedrockRules = getDefaultRuleAdditionsForStage(category, RuleStage.BEFORE_BEDROCK, registries);
        List<MaterialRule> afterBedrockRules = getDefaultRuleAdditionsForStage(category, RuleStage.AFTER_BEDROCK, registries);
        if (beforeBedrockRules.isEmpty() && afterBedrockRules.isEmpty())
            return vanilla;

        MaterialRule resolved = unwrapHolders(vanilla);
        List<MaterialRule> sequence = resolved instanceof SequenceRule rules ? rules.sequence() : List.of(vanilla);
        ImmutableList.Builder<MaterialRule> result = ImmutableList.builder();
        result.addAll(beforeBedrockRules);

        int index = 0;
        while (index < sequence.size() && sequence.get(index) instanceof MaterialRule.HolderHolder reference && (reference.holder().is(VanillaMaterialRules.BEDROCK_FLOOR) || reference.holder().is(VanillaMaterialRules.BEDROCK_ROOF)))
            result.add(sequence.get(index++));

        if (!afterBedrockRules.isEmpty())
        {
            MaterialRule additions = MaterialRules.sequence(afterBedrockRules);
            result.add(category == RuleCategory.OVERWORLD
                    ? MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), additions) : additions);
        }
        result.addAll(sequence.subList(index, sequence.size()));
        return MaterialRules.sequence(result.build());
    }

    static
    {
        for (RuleCategory category : RuleCategory.values())
        {
            materialRuleKeys.put(category, Maps.newHashMap());
            materialRuleBuilders.put(category, Maps.newHashMap());
            runtimeMaterialRules.put(category, Maps.newHashMap());
            Map<RuleStage, List<Pair<Integer, RuleBuilder>>> ruleStages = Maps.newHashMap();
            for (RuleStage stage : RuleStage.values())
                ruleStages.put(stage, Lists.newArrayList());
            defaultSurfaceRuleInjections.put(category, ruleStages);
        }
    }
}
