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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record NamespacedSurfaceRuleSource(MaterialRule base, Map<String, MaterialRule> sources) implements MaterialRule
{
    public static final MapCodec<NamespacedSurfaceRuleSource> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            MaterialRule.CODEC.fieldOf("base").forGetter(NamespacedSurfaceRuleSource::base),
            Codec.unboundedMap(Codec.STRING, MaterialRule.CODEC).fieldOf("sources").forGetter(NamespacedSurfaceRuleSource::sources)
    ).apply(builder, NamespacedSurfaceRuleSource::new));

    @Override
    public MapCodec<? extends MaterialRule> codec()
    {
        return CODEC;
    }

    @Override
    public RuleEvaluator compile(MaterialRuleContext context)
    {
        MaterialRuleCompiler compiler = new MaterialRuleCompiler(context);
        RuleEvaluator fallback = compiler.compile(this.base);
        Map<String, RuleEvaluator> rules = new HashMap<>();
        Function<String, RuleEvaluator> compileNamespace = namespace -> {
            MaterialRule source = namespace == null ? null : this.sources.get(namespace);
            return source == null ? fallback : withFallback(compiler.compile(source), fallback);
        };

        Set<Holder<Biome>> possibleBiomes = context.possibleBiomes();
        if (possibleBiomes == null)
        {
            this.sources.keySet().forEach(namespace -> rules.put(namespace, compileNamespace.apply(namespace)));
            if (rules.isEmpty())
                return fallback;
            RuleEvaluator dispatch = (x, y, z) -> rules.getOrDefault(namespaceOf(context.getBiome()), fallback).tryApply(x, y, z);
            return dispatchConditional(compiler, rules.keySet(), dispatch, fallback);
        }

        Map<Holder<Biome>, RuleEvaluator> biomeRules = new IdentityHashMap<>();
        RuleEvaluator uniformRule = null;
        boolean uniform = true;
        for (Holder<Biome> biome : possibleBiomes)
        {
            RuleEvaluator rule = rules.computeIfAbsent(namespaceOf(biome), compileNamespace);
            biomeRules.put(biome, rule);
            if (uniformRule == null)
                uniformRule = rule;
            else if (uniformRule != rule)
                uniform = false;
        }

        if (uniform)
            return uniformRule == null ? fallback : uniformRule;

        RuleEvaluator dispatch = (x, y, z) -> {
            Holder<Biome> biome = context.getBiome();
            RuleEvaluator rule = biomeRules.get(biome);
            if (rule == null)
                rule = rules.getOrDefault(namespaceOf(biome), fallback);
            return rule.tryApply(x, y, z);
        };
        return dispatchConditional(compiler, rules.keySet(), dispatch, fallback);
    }

    private RuleEvaluator dispatchConditional(MaterialRuleCompiler compiler, Set<String> namespaces, RuleEvaluator dispatch, RuleEvaluator fallback)
    {
        var rules = new ArrayList<MaterialRule>();

        // Gather rules for all requested namespaces
        for (String namespace : namespaces)
        {
            MaterialRule source = namespace == null ? null : this.sources.get(namespace);
            if (source != null)
                rules.add(source);
        }
        ConditionEvaluator evaluator = compiler.createConditionEvaluator(rules);
        if (evaluator == null)
            return dispatch;
        return (x, y, z) -> (evaluator.test() ? dispatch : fallback).tryApply(x, y, z);
    }

    private static RuleEvaluator withFallback(RuleEvaluator base, RuleEvaluator fallback)
    {
        return (x, y, z) -> {
            BlockState state = base.tryApply(x, y, z);
            return state != null ? state : fallback.tryApply(x, y, z);
        };
    }

    private static String namespaceOf(Holder<Biome> biome)
    {
        var key = biome.unwrapKey();
        return key.isPresent() ? key.get().identifier().getNamespace() : null;
    }
}
