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

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.IExtendedMultiNoiseBiomeSource;
import terrablender.worldgen.IExtendedParameterList;

import java.util.List;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeSource implements IExtendedMultiNoiseBiomeSource
{
    @Shadow
    public abstract Climate.ParameterList<Holder<Biome>> parameters();

    @Shadow @Final @Mutable
    private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

    @Inject(method="getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at=@At("HEAD"), cancellable = true)
    public void getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir)
    {
        cir.setReturnValue(((IExtendedParameterList<Holder<Biome>>)this.parameters()).findValuePositional(sampler.sample(x, y, z), x, y, z));
    }

    @Inject(method="addDebugInfo", at =@At("TAIL"))
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
            MultiNoiseBiomeSource cloned = (MultiNoiseBiomeSource) super.clone();
            ((MixinMultiNoiseBiomeSource) (Object) cloned).parameters = Either.left(((IExtendedParameterList<Holder<Biome>>) this.parameters()).clone());
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
