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

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.core.TerraBlender;
import terrablender.util.LevelUtils;
import terrablender.worldgen.IExtendedChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator implements IExtendedChunkGenerator
{
    @Mutable
    @Shadow
    @Final
    private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;

    @Shadow
    @Final
    protected BiomeSource biomeSource;

    @Shadow
    @Final
    private Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    @Inject(method = "lambda$new$3(Lnet/minecraft/world/level/biome/BiomeSource;Ljava/util/function/Function;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void skipInitialFeaturesPerStep(BiomeSource biomeSource, Function function, CallbackInfoReturnable<List> cir)
    {
        cir.setReturnValue(new ArrayList<>());
    }

    @Override
    public void updateFeaturesPerStep()
    {
        this.featuresPerStep = Suppliers.memoize(() ->
        {
            return FeatureSorter.buildFeaturesPerStep(this.biomeSource.possibleBiomes().stream().toList(), biome -> {
                return this.generationSettingsGetter.apply(biome).features();
            }, true);
        });
    }
}
