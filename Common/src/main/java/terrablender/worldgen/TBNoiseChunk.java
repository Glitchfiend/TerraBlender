/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.worldgen;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

public class TBNoiseChunk extends NoiseChunk
{
    private final TBNoiseSampler.TBFlatNoiseData[][] tbNoiseData;

    public static TBNoiseChunk forChunk(ChunkAccess chunkAccess, TBNoiseSampler sampler, Supplier<TBNoiseChunk.NoiseFiller> noiseFiller, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker p_188778_, Blender blender)
    {
        ChunkPos chunkpos = chunkAccess.getPos();
        NoiseSettings noisesettings = noiseGenSettings.noiseSettings();
        int i = Math.max(noisesettings.minY(), chunkAccess.getMinBuildHeight());
        int j = Math.min(noisesettings.minY() + noisesettings.height(), chunkAccess.getMaxBuildHeight());
        int cellNoiseMinY = Mth.intFloorDiv(i, noisesettings.getCellHeight());
        int cellCountY = Mth.intFloorDiv(j - i, noisesettings.getCellHeight());
        return new TBNoiseChunk(16 / noisesettings.getCellWidth(), cellCountY, cellNoiseMinY, sampler, chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), noiseFiller.get(), noiseGenSettings, p_188778_, blender);
    }

    public static TBNoiseChunk forColumn(int x, int z, int cellNoiseMinY, int cellCountY, TBNoiseSampler sampler, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker)
    {
        return new TBNoiseChunk(1, cellCountY, cellNoiseMinY, sampler, x, z, (noiseX, noiseY, noiseZ) -> {
            return 0.0D;
        }, noiseGenSettings, fluidPicker, Blender.empty());
    }

    private TBNoiseChunk(int cellCountXZ, int cellCountY, int cellNoiseMinY, TBNoiseSampler sampler, int chunkX, int chunkZ, NoiseChunk.NoiseFiller noiseFiller, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker, Blender blender)
    {
        super(cellCountXZ, cellCountY, cellNoiseMinY, sampler, chunkX, chunkZ, noiseFiller, noiseGenSettings, fluidPicker, blender);

        int cellWidth = this.noiseSettings.getCellWidth();
        int j = QuartPos.fromBlock(cellCountXZ * cellWidth);

        this.tbNoiseData = new TBNoiseSampler.TBFlatNoiseData[j + 1][];

        for (int k = 0; k <= j; ++k)
        {
            int l = this.firstNoiseX + k;
            this.tbNoiseData[k] = new TBNoiseSampler.TBFlatNoiseData[j + 1];

            for (int i1 = 0; i1 <= j; ++i1)
            {
                int j1 = this.firstNoiseZ + i1;
                this.tbNoiseData[k][i1] = sampler.noiseDataTB(l, j1, blender);
            }
        }

        // Nuke Vanilla's noise data to reduce memory usage.
        this.noiseData = null;
    }

    @Override
    public NoiseSampler.FlatNoiseData noiseData(int x, int z)
    {
        throw new RuntimeException("Vanilla noiseData called on TBNoiseChunk!");
    }

    public TBNoiseSampler.TBFlatNoiseData noiseDataTB(int x, int z)
    {
        return this.tbNoiseData[x - this.firstNoiseX][z - this.firstNoiseZ];
    }

    @Override
    public int preliminarySurfaceLevel(int x, int z)
    {
        return this.preliminarySurfaceLevel.computeIfAbsent(ChunkPos.asLong(QuartPos.fromBlock(x), QuartPos.fromBlock(z)), this::computePreliminarySurfaceLevel);
    }

    private int computePreliminarySurfaceLevel(long chunkPos)
    {
        int i = ChunkPos.getX(chunkPos);
        int j = ChunkPos.getZ(chunkPos);
        int k = i - this.firstNoiseX;
        int l = j - this.firstNoiseZ;
        int i1 = this.tbNoiseData.length;
        TerrainInfo terraininfo;
        if (k >= 0 && l >= 0 && k < i1 && l < i1) {
            terraininfo = this.tbNoiseData[k][l].terrainInfo();
        } else {
            terraininfo = ((TBNoiseSampler)this.sampler).noiseDataTB(i, j, this.blender).terrainInfo();
        }

        return this.sampler.getPreliminarySurfaceLevel(QuartPos.toBlock(i), QuartPos.toBlock(j), terraininfo);
    }
}
