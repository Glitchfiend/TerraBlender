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

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.TerrainInfo;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import terrablender.core.TerraBlender;
import terrablender.worldgen.noise.Area;
import terrablender.worldgen.noise.LayeredNoiseUtil;

import java.util.List;

public class TBNoiseSampler extends NoiseSampler implements TBClimate.Sampler
{
    private final Area uniquenessNoise;

    private final List<TBClimate.ParameterPoint> tbSpawnTarget;

    public TBNoiseSampler(NoiseSettings noiseSettings, boolean isNoiseCavesEnabled, long seed, Registry<NormalNoise.NoiseParameters> noiseParamRegistry, WorldgenRandom.Algorithm randomSource, LayeredNoiseUtil.UniquenessType uniquenessType)
    {
        super(noiseSettings, isNoiseCavesEnabled, seed, noiseParamRegistry, randomSource);

        // Replace baseNoise to use our version of noiseData
        this.baseNoise = (instance) -> {
            return instance.createNoiseInterpolator((noiseX, noiseY, noiseZ) -> {
                return this.calculateBaseNoise(noiseX, noiseY, noiseZ, ((TBNoiseChunk)instance).noiseDataTB(QuartPos.fromBlock(noiseX), QuartPos.fromBlock(noiseZ)).terrainInfo(), instance.getBlender());
            });
        };

        this.uniquenessNoise = LayeredNoiseUtil.uniqueness(seed, uniquenessType);
        this.tbSpawnTarget = BiomeProviderUtils.getAllSpawnTargets();
        // Null Vanilla's spawn targets list to reduce memory usage.
        this.spawnTarget = null;
    }

    @Override
    public NoiseSampler.FlatNoiseData noiseData(int x, int z, Blender blender)
    {
        return null;
    }

    @Override
    public Climate.TargetPoint sample(int x, int y, int z)
    {
        throw new RuntimeException("Vanilla sample called on TBNoiseSampler!");
    }

    @Override
    public Climate.TargetPoint target(int x, int y, int z, NoiseSampler.FlatNoiseData noiseData)
    {
        throw new RuntimeException("Vanilla target called on TBNoiseSampler!");
    }

    @VisibleForDebug
    public TBFlatNoiseData noiseDataTB(int x, int z, Blender blender)
    {
        double shiftedX = (double)x + this.getOffset(x, 0, z);
        double shiftedZ = (double)z + this.getOffset(z, x, 0);
        double continentalness = this.getContinentalness(shiftedX, 0.0D, shiftedZ);
        double weirdness = this.getWeirdness(shiftedX, 0.0D, shiftedZ);
        double uniqueness = this.getUniqueness(shiftedX, 0.0D, shiftedZ);
        double erosion = this.getErosion(shiftedX, 0.0D, shiftedZ);
        TerrainInfo terraininfo = this.terrainInfo(QuartPos.toBlock(x), QuartPos.toBlock(z), (float)continentalness, (float)weirdness, (float)erosion, blender);
        return new TBFlatNoiseData(shiftedX, shiftedZ, continentalness, weirdness, uniqueness, erosion, terraininfo);
    }

    @Override
    public TBClimate.TargetPoint sampleTB(int x, int y, int z)
    {
        return this.targetTB(x, y, z, this.noiseDataTB(x, z, Blender.empty()));
    }

    @VisibleForDebug
    public TBClimate.TargetPoint targetTB(int x, int y, int z, TBFlatNoiseData noiseData)
    {
        double d0 = noiseData.shiftedX();
        double d1 = (double)y + this.getOffset(y, z, x);
        double d2 = noiseData.shiftedZ();
        double d3 = this.computeBaseDensity(QuartPos.toBlock(y), noiseData.terrainInfo());
        return TBClimate.target((float)this.getTemperature(d0, d1, d2), (float)this.getHumidity(d0, d1, d2), (float)noiseData.continentalness(), (float)noiseData.erosion(), (float)d3, (float)noiseData.weirdness(), (float)noiseData.uniqueness());
    }

    @Override
    public BlockPos findSpawnPosition()
    {
        return TBClimate.findSpawnPosition(this.tbSpawnTarget, this);
    }

    @VisibleForDebug
    public double getUniqueness(double x, double y, double z)
    {
        return Climate.unquantizeCoord(this.uniquenessNoise.get((int)x, (int)z));
    }

    public record TBFlatNoiseData(double shiftedX, double shiftedZ, double continentalness, double weirdness, double uniqueness, double erosion, TerrainInfo terrainInfo) {}
}