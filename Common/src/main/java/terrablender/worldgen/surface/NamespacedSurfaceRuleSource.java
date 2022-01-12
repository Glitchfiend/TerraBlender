/*
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

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import javax.annotation.Nullable;
import java.util.Map;

public record NamespacedSurfaceRuleSource(SurfaceRules.RuleSource base, Map<String, SurfaceRules.RuleSource> sources) implements SurfaceRules.RuleSource
{
    public static final Codec<NamespacedSurfaceRuleSource> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(
            SurfaceRules.RuleSource.CODEC.fieldOf("base").forGetter(NamespacedSurfaceRuleSource::base),
            Codec.unboundedMap(Codec.STRING, SurfaceRules.RuleSource.CODEC).fieldOf("sources").forGetter(NamespacedSurfaceRuleSource::sources)
        ).apply(builder, NamespacedSurfaceRuleSource::new);
    });

    @Override
    public Codec<? extends SurfaceRules.RuleSource> codec()
    {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context)
    {
        ImmutableMap.Builder<String, SurfaceRules.SurfaceRule> rules = new ImmutableMap.Builder<>();
        this.sources.entrySet().forEach(entry -> rules.put(entry.getKey(), entry.getValue().apply(context)));
        return new NamespacedRule(context, this.base.apply(context), rules.build());
    }

    record NamespacedRule(SurfaceRules.Context context, SurfaceRules.SurfaceRule baseRule, Map<String, SurfaceRules.SurfaceRule> rules) implements SurfaceRules.SurfaceRule
    {
        @Nullable
        public BlockState tryApply(int x, int y, int z)
        {
            Biome biome = context.biomeGetter.apply(new BlockPos(x, y, z));
            ResourceLocation biomeKey = context.biomes.getKey(biome);
            String namespace = biomeKey.getNamespace();
            BlockState state = null;

            if (this.rules.containsKey(namespace))
                state = this.rules.get(namespace).tryApply(x, y, z);

            if (state == null)
                state = this.baseRule.tryApply(x, y, z);

            return state;
        }
    }
}
