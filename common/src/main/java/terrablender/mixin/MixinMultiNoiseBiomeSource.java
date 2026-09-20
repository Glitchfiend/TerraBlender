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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.worldgen.IExtendedMultiNoiseBiomeSource;
import terrablender.worldgen.IExtendedParameterList;

import java.util.List;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeSource implements IExtendedMultiNoiseBiomeSource
{
    @Shadow(remap = false)
    public abstract Climate.ParameterList<Holder<Biome>> parameters();

    @WrapOperation(method = "/^lambda\\$createResolver\\$.*/", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(Lnet/minecraft/world/level/biome/Climate$TargetPoint;)Lnet/minecraft/core/Holder;"), remap = false)
    private Holder<Biome> getNoiseBiome(MultiNoiseBiomeSource source, Climate.TargetPoint target, Operation<Holder<Biome>> original, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z)
    {
        IExtendedParameterList<Holder<Biome>> parameters = (IExtendedParameterList<Holder<Biome>>)this.parameters();
        return parameters.isInitialized() ? parameters.findValuePositional(target, x, y, z) : original.call(source, target);
    }

    @WrapOperation(method = "/^lambda\\$createResolverForChunk\\$.*/", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(Lnet/minecraft/world/level/biome/Climate$TargetPoint;)Lnet/minecraft/core/Holder;"), remap = false)
    private Holder<Biome> getNoiseBiomeForChunk(MultiNoiseBiomeSource source, Climate.TargetPoint target, Operation<Holder<Biome>> original, @Local(argsOnly = true, ordinal = 3) int x, @Local(argsOnly = true, ordinal = 4) int y, @Local(argsOnly = true, ordinal = 5) int z)
    {
        IExtendedParameterList<Holder<Biome>> parameters = (IExtendedParameterList<Holder<Biome>>)this.parameters();
        return parameters.isInitialized() ? parameters.findValuePositional(target, x, y, z) : original.call(source, target);
    }

    @Inject(method="addDebugInfo", at =@At("TAIL"), remap = false)
    public void addDebugInfo(List<String> debugLines, BlockPos pos, Climate.Sampler sampler, CallbackInfo ci)
    {
        int qx = QuartPos.fromBlock(pos.getX());
        int qz = QuartPos.fromBlock(pos.getZ());
        IExtendedParameterList<Holder<Biome>> extension = (IExtendedParameterList<Holder<Biome>>) this.parameters();
        if (extension.isInitialized()) debugLines.add("Region: " + extension.getRegion(extension.getUniqueness(qx, 0, qz)).getName().toString());
    }

    @Override
    public MultiNoiseBiomeSource clone() {
        try {
            return (MultiNoiseBiomeSource) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
