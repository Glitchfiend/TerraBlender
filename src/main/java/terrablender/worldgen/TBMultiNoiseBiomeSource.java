/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import terrablender.core.TerraBlender;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TBMultiNoiseBiomeSource extends BiomeSource
{
    public static final MapCodec<TBMultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec((p_187070_) -> {
        return p_187070_.group(ExtraCodecs.<Pair<TBClimate.ParameterPoint, Supplier<Biome>>>nonEmptyList(RecordCodecBuilder.<Pair<TBClimate.ParameterPoint, Supplier<Biome>>>create((biomeMapping) -> {
            return biomeMapping.group(TBClimate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(biomeMapping, Pair::of);
        }).listOf()).xmap(TBClimate.ParameterList::new, (Function<TBClimate.ParameterList<Supplier<Biome>>, List<Pair<TBClimate.ParameterPoint, Supplier<Biome>>>>) TBClimate.ParameterList::values).fieldOf("biomes").forGetter((p_187080_) -> {
            return p_187080_.parameters;
        })).apply(p_187070_, TBMultiNoiseBiomeSource::new);
    });
    public static final Codec<TBMultiNoiseBiomeSource> CODEC = Codec.mapEither(TBMultiNoiseBiomeSource.PresetInstance.CODEC, DIRECT_CODEC).xmap((p_187068_) -> {
        return p_187068_.map(TBMultiNoiseBiomeSource.PresetInstance::biomeSource, Function.identity());
    }, (p_187066_) -> {
        return p_187066_.preset().map(Either::<TBMultiNoiseBiomeSource.PresetInstance, TBMultiNoiseBiomeSource>left).orElseGet(() -> {
            return Either.right(p_187066_);
        });
    }).codec();
    private TBClimate.ParameterList<Supplier<Biome>> parameters;
    private final Optional<PresetInstance> preset;

    private TBMultiNoiseBiomeSource(TBClimate.ParameterList<Supplier<Biome>> p_187057_)
    {
        this(p_187057_, Optional.empty());
    }

    TBMultiNoiseBiomeSource(TBClimate.ParameterList<Supplier<Biome>> parameters, Optional<PresetInstance> p_187060_)
    {
        super(parameters.values().stream().map(Pair::getSecond));
        this.preset = p_187060_;
        this.parameters = parameters;
    }

    @Override
    protected Codec<? extends BiomeSource> codec()
    {
        return CODEC;
    }

    public BiomeSource withSeed(long p_48466_)
    {
        return this;
    }

    @Override
    public Biome getNoiseBiome(int p_186735_, int p_186736_, int p_186737_, Climate.Sampler sampler)
    {
        // Under normal circumstances there should never be a situation where the sampler isn't a TBClimate.Sampler.
        return this.getNoiseBiome(((TBClimate.Sampler)sampler).sampleTB(p_186735_, p_186736_, p_186737_));
    }

    private Optional<TBMultiNoiseBiomeSource.PresetInstance> preset()
    {
        return this.preset;
    }

    public boolean stable(TBMultiNoiseBiomeSource.Preset p_187064_)
    {
        return this.preset.isPresent() && Objects.equals(this.preset.get().preset(), p_187064_);
    }

    @VisibleForDebug
    public Biome getNoiseBiome(TBClimate.TargetPoint p_187062_)
    {
        return this.parameters.findValue(p_187062_, () -> {
            return net.minecraft.data.worldgen.biome.Biomes.THE_VOID;
        }).get();
    }

    public static class Preset
    {
        static final Map<ResourceLocation, TBMultiNoiseBiomeSource.Preset> BY_NAME = Maps.newHashMap();

        public static final TBMultiNoiseBiomeSource.Preset OVERWORLD = new TBMultiNoiseBiomeSource.Preset(new ResourceLocation(TerraBlender.MOD_ID, "overworld"), (biomeRegistry) -> {
            return createParameterSource(biomeRegistry, BiomeProviderUtils::addAllOverworldBiomes);
        });

        public static final TBMultiNoiseBiomeSource.Preset NETHER = new TBMultiNoiseBiomeSource.Preset(new ResourceLocation(TerraBlender.MOD_ID, "nether"), (biomeRegistry) -> {
            return createParameterSource(biomeRegistry, BiomeProviderUtils::addAllNetherBiomes);
        });

        final ResourceLocation name;
        private final Function<Registry<Biome>, TBClimate.ParameterList<Supplier<Biome>>> parameterSource;

        public Preset(ResourceLocation name, Function<Registry<Biome>, TBClimate.ParameterList<Supplier<Biome>>> parameterSource)
        {
            this.name = name;
            this.parameterSource = parameterSource;
            BY_NAME.put(name, this);
        }

        public TBMultiNoiseBiomeSource biomeSource(TBMultiNoiseBiomeSource.PresetInstance preset, boolean isPreset)
        {
            return new TBMultiNoiseBiomeSource(this.buildParameters(preset), isPreset ? Optional.of(preset) : Optional.empty());
        }

        public TBMultiNoiseBiomeSource biomeSource(Registry<Biome> biomeRegistry, boolean isPreset)
        {
            return this.biomeSource(new TBMultiNoiseBiomeSource.PresetInstance(this, biomeRegistry), isPreset);
        }

        public TBMultiNoiseBiomeSource biomeSource(Registry<Biome> biomeRegistry)
        {
            return this.biomeSource(biomeRegistry, true);
        }

        private TBClimate.ParameterList<Supplier<Biome>> buildParameters(TBMultiNoiseBiomeSource.PresetInstance preset)
        {
            ImmutableList.Builder<Pair<TBClimate.ParameterPoint, Supplier<Biome>>> parameterListBuilder = ImmutableList.builder();
            parameterListBuilder.addAll(this.parameterSource.apply(preset.biomes()).values());
            return new TBClimate.ParameterList<>(parameterListBuilder.build());
        }

        private static TBClimate.ParameterList<Supplier<Biome>> createParameterSource(Registry<Biome> biomeRegistry, BiConsumer<Registry<Biome>, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>>> addBiomes)
        {
            ImmutableList.Builder<Pair<TBClimate.ParameterPoint, Supplier<Biome>>> builder = ImmutableList.builder();

            Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper = (parameterPair) -> {
                builder.add(parameterPair.mapSecond((key) -> () -> biomeRegistry.getOrThrow(key)));
            };

            addBiomes.accept(biomeRegistry, mapper);
            return new TBClimate.ParameterList<>(builder.build());
        }
    }

    record PresetInstance(TBMultiNoiseBiomeSource.Preset preset, Registry<Biome> biomes)
    {
        public static final MapCodec<TBMultiNoiseBiomeSource.PresetInstance> CODEC = RecordCodecBuilder.mapCodec((p_48558_) -> {
            return p_48558_.group(ResourceLocation.CODEC.flatXmap((p_151869_) -> {
                return Optional.ofNullable(TBMultiNoiseBiomeSource.Preset.BY_NAME.get(p_151869_)).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("Unknown preset: " + p_151869_);
                });
            }, (p_151867_) -> {
                return DataResult.success(p_151867_.name);
            }).fieldOf("preset").stable().forGetter(TBMultiNoiseBiomeSource.PresetInstance::preset), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(TBMultiNoiseBiomeSource.PresetInstance::biomes)).apply(p_48558_, p_48558_.stable(TBMultiNoiseBiomeSource.PresetInstance::new));
        });

        public TBMultiNoiseBiomeSource biomeSource()
        {
            return this.preset.biomeSource(this, true);
        }
    }
}