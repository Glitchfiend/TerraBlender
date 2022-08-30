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
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.IExtendedBiomeSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public abstract class MixinBiomeSource implements BiomeResolver, IExtendedBiomeSource
{
    @Shadow
    public Set<Holder<Biome>> possibleBiomes;

    @Shadow
    public Supplier<List<BiomeSource.StepFeatureData>> featuresPerStep;

    private List<Holder<Biome>> originalBiomeList;

    private boolean hasAppended = false;

    @Shadow
    abstract List<BiomeSource.StepFeatureData> buildFeaturesPerStep(List<Holder<Biome>> biomeList, boolean ignoreOrderCycle);

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"))
    protected void onInit(List<Holder<Biome>> biomeList, CallbackInfo ci)
    {
        this.originalBiomeList = biomeList;
    }

    @Inject(method = "Lnet/minecraft/world/level/biome/BiomeSource;method_40141(Ljava/util/List;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private void skipInitialFeaturesPerStep(List<Holder<Biome>> possibleBiomes, CallbackInfoReturnable<List<BiomeSource.StepFeatureData>> cir)
    {
        cir.setReturnValue(new ArrayList<>());
    }

    @Override
    public void appendDeferredBiomesList(List<Holder<Biome>> biomesToAppend)
    {
        // Don't append the biomes list again if we have already done so
        if (this.hasAppended) {
            return;
        }

        ImmutableList.Builder<Holder<Biome>> builder = ImmutableList.builder();
        builder.addAll(this.originalBiomeList);
        builder.addAll(biomesToAppend);
        ImmutableList<Holder<Biome>> biomeList = builder.build().stream().distinct().collect(ImmutableList.toImmutableList());

        this.possibleBiomes = new ObjectLinkedOpenHashSet<>(biomeList);
        this.hasAppended = true;
    }

    @Override
    public void updateFeaturesPerStep()
    {
        this.featuresPerStep = Suppliers.memoize(() -> this.buildFeaturesPerStep(ImmutableList.copyOf(this.possibleBiomes), true));
    }
}
