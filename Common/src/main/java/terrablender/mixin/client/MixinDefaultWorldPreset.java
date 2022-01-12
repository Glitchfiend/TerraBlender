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
package terrablender.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.GenerationSettings;
import terrablender.api.WorldPresetUtils;
import terrablender.core.TerraBlender;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.WorldPreset$1")
public abstract class MixinDefaultWorldPreset extends WorldPreset
{
    protected MixinDefaultWorldPreset(String name)
    {
        super(name);
    }

    @Shadow
    abstract protected ChunkGenerator generator(RegistryAccess registryAccess, long seed);

    @Inject(method = "generator(Lnet/minecraft/core/RegistryAccess;J)Lnet/minecraft/world/level/chunk/ChunkGenerator;", at = @At("HEAD"), cancellable = true)
    public void modifyGenerator(RegistryAccess registryAccess, long seed, CallbackInfoReturnable<ChunkGenerator> cir)
    {
        if (!TerraBlender.CONFIG.replaceDefaultWorldtypes) return;
        cir.setReturnValue(WorldPresetUtils.overworldChunkGenerator(registryAccess, seed));
    }

    @Override
    public WorldGenSettings create(RegistryAccess.RegistryHolder registryAccess, long seed, boolean generateFeatures, boolean generateBonusChest)
    {
        if (!TerraBlender.CONFIG.replaceDefaultWorldtypes) return super.create(registryAccess, seed, generateFeatures, generateBonusChest);
        return WorldPresetUtils.settings(registryAccess, seed, generateFeatures, generateBonusChest, WorldPresetUtils.dimensions(registryAccess, seed), this.generator(registryAccess, seed));
    }
}
