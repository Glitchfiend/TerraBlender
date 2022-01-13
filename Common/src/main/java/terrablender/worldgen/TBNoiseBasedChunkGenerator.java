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
package terrablender.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import terrablender.core.TerraBlender;
import terrablender.worldgen.noise.LayeredNoiseUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class TBNoiseBasedChunkGenerator extends NoiseBasedChunkGenerator implements IExtendedNoiseBasedChunkGenerator
{
    public static final Codec<TBNoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create((builder) -> {
        return builder.group(RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter((instance) -> {
            return instance.noises;
        }), BiomeSource.CODEC.fieldOf("biome_source").forGetter((instance) -> {
            return instance.biomeSource;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((instance) -> {
            return instance.seed;
        }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((instance) -> {
            return instance.settings;
        })).apply(builder, builder.stable(TBNoiseBasedChunkGenerator::new));
    });

    public TBNoiseBasedChunkGenerator(Registry<NormalNoise.NoiseParameters> noises, BiomeSource biomeSource, long seed, Supplier<NoiseGeneratorSettings> settings)
    {
        this(noises, biomeSource, biomeSource, seed, settings);
    }

    private TBNoiseBasedChunkGenerator(Registry<NormalNoise.NoiseParameters> noises, BiomeSource biomeSource, BiomeSource runtimeBiomeSource, long seed, Supplier<NoiseGeneratorSettings> settings)
    {
        super(noises, runtimeBiomeSource, seed, settings);
        NoiseGeneratorSettings noiseGeneratorSettings = this.settings.get();
        NoiseSettings noiseSettings = noiseGeneratorSettings.noiseSettings();
        boolean largeBiomes = noiseSettings.largeBiomes();

        LayeredNoiseUtil.UniquenessType uniquenessType = largeBiomes ? LayeredNoiseUtil.UniquenessType.OVERWORLD_LARGE : LayeredNoiseUtil.UniquenessType.OVERWORLD;

        // Simple check to determine if we are working with the nether or not
        if (noiseGeneratorSettings.getDefaultBlock().getBlock() == Blocks.NETHERRACK && noiseGeneratorSettings.getDefaultFluid().getBlock() == Blocks.LAVA)
        {
            uniquenessType = largeBiomes ? LayeredNoiseUtil.UniquenessType.NETHER_LARGE : LayeredNoiseUtil.UniquenessType.NETHER;
        }

        this.sampler = new TBNoiseSampler(noiseSettings, noiseGeneratorSettings.isNoiseCavesEnabled(), seed, noises, noiseGeneratorSettings.getRandomSource(), uniquenessType);
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> registry, Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess)
    {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            this.doCreateBiomes(registry, blender, structureFeatureManager, chunkAccess);
            return chunkAccess;
        }), Util.backgroundExecutor());
    }

    private void doCreateBiomes(Registry<Biome> biomeRegistry, Blender blender, StructureFeatureManager structureManager, ChunkAccess chunkAccess)
    {
        TBNoiseChunk noiseChunk = (TBNoiseChunk)this.getOrCreateNoiseChunk(chunkAccess, this.sampler, () -> {
            return new Beardifier(structureManager, chunkAccess);
        }, this.settings.get(), this.globalFluidPicker, blender);
        BiomeResolver biomeresolver = BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(this.runtimeBiomeSource), biomeRegistry, chunkAccess);
        chunkAccess.fillBiomesFromNoise(biomeresolver, (TBClimate.Sampler)(x, y, z) -> ((TBNoiseSampler)TBNoiseBasedChunkGenerator.this.sampler).targetTB(x, y, z, noiseChunk.noiseDataTB(x, z)));
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec()
    {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed)
    {
        return new TBNoiseBasedChunkGenerator(this.noises, this.biomeSource.withSeed(seed), seed, this.settings);
    }

    @Override
    public NoiseChunk forColumn(int x, int z, int cellNoiseMinY, int cellCountY, NoiseSampler sampler, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker)
    {
        return TBNoiseChunk.forColumn(x, z, cellNoiseMinY, cellCountY, (TBNoiseSampler)sampler, noiseGenSettings, fluidPicker);
    }

    @Override
    public NoiseChunk getOrCreateNoiseChunk(ChunkAccess chunkAccess, NoiseSampler sampler, Supplier<NoiseChunk.NoiseFiller> noiseFiller, NoiseGeneratorSettings settings, Aquifer.FluidPicker fluidPicker, Blender blender)
    {
        if (chunkAccess.noiseChunk == null)
            chunkAccess.noiseChunk = TBNoiseChunk.forChunk(chunkAccess, (TBNoiseSampler)sampler, noiseFiller, settings, fluidPicker, blender);

        return chunkAccess.noiseChunk;
    }
}
