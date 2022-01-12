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

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import terrablender.api.BiomeProvider;
import terrablender.api.ParameterUtils;
import terrablender.core.TerraBlender;

import java.util.Optional;
import java.util.function.Consumer;

public class DataPackBiomeProvider extends BiomeProvider
{
    private WorldGenSettings settings;
    private ChunkGenerator overworldGenerator;
    private Optional<SurfaceRules.RuleSource> overworldSurfaceRules = Optional.empty();
    private Optional<SurfaceRules.RuleSource> netherSurfaceRules = Optional.empty();

    public DataPackBiomeProvider(ResourceLocation name, int weight, WorldGenSettings settings)
    {
        super(name, weight);
        this.settings = settings;
        this.overworldGenerator = settings.overworld();

        if (this.overworldGenerator instanceof NoiseBasedChunkGenerator)
        {
            NoiseGeneratorSettings generatorSettings = ((NoiseBasedChunkGenerator)this.overworldGenerator).settings.get();
            this.overworldSurfaceRules = Optional.of(generatorSettings.surfaceRule());
        }

        LevelStem netherStem = settings.dimensions().get(LevelStem.NETHER);
        ChunkGenerator netherGenerator = netherStem != null ? netherStem.generator() : null;

        if (netherGenerator != null && netherGenerator instanceof NoiseBasedChunkGenerator)
        {
            NoiseGeneratorSettings generatorSettings = ((NoiseBasedChunkGenerator)netherGenerator).settings.get();
            this.netherSurfaceRules = Optional.of(generatorSettings.surfaceRule());
        }
    }

    @Override
    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        if (!(this.overworldGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource))
            return;

        MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource)this.overworldGenerator.getBiomeSource();
        Climate.Parameter uniquenessParameter = this.getUniquenessParameter();
        TerraBlender.LOGGER.info("Adding overworld biomes for datapack " + this.getName() + " with uniqueness " + uniquenessParameter);

        biomeSource.parameters.values().stream().forEach(pair -> {
            TBClimate.ParameterPoint parameters = ParameterUtils.convertParameterPoint(pair.getFirst(), uniquenessParameter);
            Optional<ResourceKey<Biome>> key = registry.getResourceKey(pair.getSecond().get());

            if (key.isPresent())
                this.addBiome(mapper, parameters, key.get());
        });
    }

    @Override
    public Optional<SurfaceRules.RuleSource> getOverworldSurfaceRules()
    {
        return this.overworldSurfaceRules;
    }

    @Override
    public Optional<SurfaceRules.RuleSource> getNetherSurfaceRules()
    {
        return this.netherSurfaceRules;
    }
}
