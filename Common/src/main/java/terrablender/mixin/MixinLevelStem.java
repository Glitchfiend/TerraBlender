/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.util.RegistryUtils;
import terrablender.worldgen.IExtendedBiomeSource;
import terrablender.worldgen.IExtendedParameterList;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mixin(LevelStem.class)
public class MixinLevelStem
{
    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/chunk/ChunkGenerator;)V", at = @At("RETURN"))
    public void onInit(Holder<DimensionType> dimensionType, ChunkGenerator chunkGenerator, CallbackInfo ci)
    {
        // Only apply to NoiseBasedChunkGenerator with MultiNoiseBiomeSources
        if (!(chunkGenerator instanceof NoiseBasedChunkGenerator) || !(chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource))
            return;

        final RegionType regionType;
        if (dimensionType.is(DimensionType.NETHER_LOCATION)) regionType = RegionType.NETHER;
        else if (dimensionType.is(DimensionType.OVERWORLD_LOCATION)) regionType = RegionType.OVERWORLD;
        else regionType = null;

        // Don't continue if region type is uninitialized
        if (regionType == null)
            return;

        NoiseBasedChunkGenerator noiseBasedChunkGenerator = (NoiseBasedChunkGenerator)chunkGenerator;
        MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource)chunkGenerator.getBiomeSource();
        IExtendedBiomeSource biomeSourceEx = (IExtendedBiomeSource)biomeSource;
        Climate.ParameterList parameters = biomeSource.parameters;
        IExtendedParameterList parametersEx = (IExtendedParameterList)parameters;
        NoiseGeneratorSettings currentSettings = noiseBasedChunkGenerator.settings.value();

        // If necessary, replace the settings to use our surface rules. Important note: Surface rules are responsible for the vast majority of world generation lag :(
        if (!(currentSettings.surfaceRule() instanceof NamespacedSurfaceRuleSource))
        {
            SurfaceRules.RuleSource surfaceRules = regionType == RegionType.NETHER ? SurfaceRuleManager.getNamespacedRules(SurfaceRuleManager.RuleCategory.NETHER, currentSettings.surfaceRule()) : SurfaceRuleManager.getNamespacedRules(SurfaceRuleManager.RuleCategory.OVERWORLD, currentSettings.surfaceRule());
            noiseBasedChunkGenerator.settings = new Holder.Direct<>(new NoiseGeneratorSettings(currentSettings.noiseSettings(), currentSettings.defaultBlock(), currentSettings.defaultFluid(), currentSettings.noiseRouter(), surfaceRules, currentSettings.seaLevel(), currentSettings.disableMobGeneration(), currentSettings.aquifersEnabled(), currentSettings.oreVeinsEnabled(), currentSettings.useLegacyRandomSource()));
        }

        // Initialize the parameter list for TerraBlender
        parametersEx.initializeForTerraBlender(regionType, noiseBasedChunkGenerator.seed);

        // Append modded biomes to the biome source biome list
        RegistryUtils.addRegistryAccessCaptureOneShotListener(registryAccess -> {
            Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
            ImmutableList.Builder<Holder<Biome>> builder = ImmutableList.builder();
            Regions.get(regionType).forEach(region -> region.addBiomes(biomeRegistry, pair -> builder.add(biomeRegistry.getOrCreateHolder(pair.getSecond()))));
            biomeSourceEx.appendDeferredBiomesList(builder.build());
        });
    }
}
