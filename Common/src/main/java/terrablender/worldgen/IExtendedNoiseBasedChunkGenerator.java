/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.worldgen;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.function.Supplier;

public interface IExtendedNoiseBasedChunkGenerator
{
    NoiseChunk forColumn(int x, int z, int cellNoiseMinY, int cellCountY, NoiseSampler sampler, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker);
    NoiseChunk getOrCreateNoiseChunk(ChunkAccess chunkAccess, NoiseSampler sampler, Supplier<NoiseChunk.NoiseFiller> noiseFiller, NoiseGeneratorSettings settings, Aquifer.FluidPicker fluidPicker, Blender blender);
}
