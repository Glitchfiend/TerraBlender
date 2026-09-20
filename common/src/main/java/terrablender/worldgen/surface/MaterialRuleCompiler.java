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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.AbovePreliminarySurfaceCondition;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.condition.StoneDepthCondition;
import net.minecraft.world.level.levelgen.material.rule.ConditionRule;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;
import net.minecraft.world.level.levelgen.material.rule.SequenceRule;

import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class MaterialRuleCompiler
{
    private final MaterialRuleContext context;
    private final Map<MaterialRule, RuleEvaluator> compiled = new IdentityHashMap<>();

    MaterialRuleCompiler(MaterialRuleContext context)
    {
        this.context = context;
    }

    public ConditionEvaluator createConditionEvaluator(Iterable<MaterialRule> rules)
    {
        var conditions = new LinkedHashSet<MaterialCondition>();
        for (MaterialRule rule : rules)
            if (!collectConditions(rule, conditions))
                return null;

        if (conditions.size() == 1)
            return conditions.getFirst().compile(this.context);

        // Compile the conditions and create an evaluator that tests them all
        ConditionEvaluator[] guards = conditions.stream().map(condition -> condition.compile(this.context)).toArray(ConditionEvaluator[]::new);
        return () -> {
            for (ConditionEvaluator guard : guards)
                if (guard.test())
                    return true;
            return false;
        };
    }

    public RuleEvaluator compile(MaterialRule rule)
    {
        if (this.compiled.containsKey(rule))
        {
            RuleEvaluator result = this.compiled.get(rule);
            if (result == null)
                throw new IllegalStateException("Cyclic material rule reference");
            return result;
        }

        this.compiled.put(rule, null);
        RuleEvaluator result;

        if (rule instanceof MaterialRule.HolderHolder reference)
        {
            result = this.compile(reference.holder().value());
        }
        else if (rule instanceof SequenceRule sequence)
        {
            if (sequence.sequence().size() == 1)
            {
                result = this.compile(sequence.sequence().getFirst());
            }
            else
            {
                RuleEvaluator[] children = new RuleEvaluator[sequence.sequence().size()];

                for (int i = 0; i < children.length; i++)
                    children[i] = compile(sequence.sequence().get(i));

                result = (x, y, z) -> {
                    for (RuleEvaluator child : children)
                    {
                        BlockState state = child.tryApply(x, y, z);
                        if (state != null)
                            return state;
                    }
                    return null;
                };
            }
        }
        else
            result = rule.compile(this.context);

        this.compiled.put(rule, result);
        return result;
    }

    private static boolean collectConditions(MaterialRule rule, Set<MaterialCondition> conditions)
    {
        if (rule instanceof MaterialRule.HolderHolder reference)
            return collectConditions(reference.holder().value(), conditions);

        // Collect the conditions of all children in the sequence
        if (rule instanceof SequenceRule sequence)
        {
            for (MaterialRule child : sequence.sequence())
                if (!collectConditions(child, conditions))
                    return false;
            return true;
        }

        if (rule instanceof ConditionRule conditional)
        {
            MaterialCondition condition = conditional.ifTrue();
            while (condition instanceof MaterialCondition.HolderHolder reference)
                condition = reference.holder().value();

            if (condition instanceof StoneDepthCondition || condition instanceof AbovePreliminarySurfaceCondition)
            {
                conditions.add(condition);
                return true;
            }
        }
        return false;
    }
}
