/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
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

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.WorldPreset$4")
public abstract class MixinAmplifiedWorldPreset extends WorldPreset
{
    protected MixinAmplifiedWorldPreset(String name)
    {
        super(name);
    }

    @Shadow
    abstract protected ChunkGenerator generator(RegistryAccess registryAccess, long seed);

    @Inject(method = "generator(Lnet/minecraft/core/RegistryAccess;J)Lnet/minecraft/world/level/chunk/ChunkGenerator;", at = @At("HEAD"), cancellable = true)
    public void modifyGenerator(RegistryAccess registryAccess, long seed, CallbackInfoReturnable<ChunkGenerator> cir)
    {
        if (!TerraBlender.CONFIG.replaceDefaultWorldtypes) return;
        cir.setReturnValue(WorldPresetUtils.amplifiedChunkGenerator(registryAccess, seed));
    }

    @Override
    public WorldGenSettings create(RegistryAccess.RegistryHolder registryAccess, long seed, boolean generateFeatures, boolean generateBonusChest)
    {
        if (!TerraBlender.CONFIG.replaceDefaultWorldtypes) return super.create(registryAccess, seed, generateFeatures, generateBonusChest);
        return WorldPresetUtils.settings(registryAccess, seed, generateFeatures, generateBonusChest, WorldPresetUtils.dimensions(registryAccess, seed), this.generator(registryAccess, seed));
    }
}
