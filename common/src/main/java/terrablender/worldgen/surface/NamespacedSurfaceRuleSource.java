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
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public record NamespacedSurfaceRuleSource(MaterialRule base, Map<String, MaterialRule> sources) implements MaterialRule
{
    public static final MapCodec<NamespacedSurfaceRuleSource> CODEC = RecordCodecBuilder.mapCodec((builder) ->
    {
        return builder.group(
            MaterialRule.CODEC.fieldOf("base").forGetter(NamespacedSurfaceRuleSource::base),
            Codec.unboundedMap(Codec.STRING, MaterialRule.CODEC).fieldOf("sources").forGetter(NamespacedSurfaceRuleSource::sources)
        ).apply(builder, NamespacedSurfaceRuleSource::new);
    });

    @Override
    public MapCodec<? extends MaterialRule> codec() {
        return CODEC;
    }

    @Override
    public RuleEvaluator compile(MaterialRuleContext context)
    {
        // Map Vanilla's possibleBiomes to possible namespaces
        Set<Holder<Biome>> possibleBiomes = context.possibleBiomes();
        Set<String> namespaces = new HashSet<>();
        if (possibleBiomes != null)
            possibleBiomes.forEach(biome -> namespaces.add(biome.unwrapKey().map(key -> key.identifier().getNamespace()).orElse("")));

        // Gather the rules for the possible namespaces
        RuleEvaluator fallback = this.base.compile(context);
        Map<String, RuleEvaluator> rules = new HashMap<>();
        this.sources.forEach((namespace, source) -> {
            if (possibleBiomes == null || namespaces.contains(namespace))
                rules.put(namespace, source.compile(context));
        });

        if (rules.isEmpty())
            return fallback;

        // No per-block biome lookup is necessary when every possible biome uses the same rule.
        if (possibleBiomes != null && namespaces.size() == 1)
        {
            RuleEvaluator selected = rules.get(namespaces.iterator().next());
            return (x, y, z) -> {
                BlockState state = selected.tryApply(x, y, z);
                return state != null ? state : fallback.tryApply(x, y, z);
            };
        }

        return new NamespacedRule(context, fallback, Map.copyOf(rules));
    }

    record NamespacedRule(MaterialRuleContext context, RuleEvaluator baseRule, Map<String, RuleEvaluator> rules) implements RuleEvaluator
    {
        public BlockState tryApply(int x, int y, int z)
        {
            BlockState state = null;

            var key = context.getBiome().unwrapKey();
            RuleEvaluator rule = key.map(biomeResourceKey -> this.rules.get(biomeResourceKey.identifier().getNamespace())).orElse(null);
            if (rule != null)
                state = rule.tryApply(x, y, z);

            if (state == null)
                state = this.baseRule.tryApply(x, y, z);

            return state;
        }
    }
}
