/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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
