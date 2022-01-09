/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.mixin.client;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import terrablender.core.TerraBlender;

import java.util.Random;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen
{
    // NOTE: Forge has patches which make this Mixin unnecessary
    @Redirect(method="create", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/levelgen/WorldGenSettings;makeDefault(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/level/levelgen/WorldGenSettings;"))
    private static WorldGenSettings onMakeDefault(RegistryAccess registryAccess)
    {
        if (!TerraBlender.CONFIG.replaceDefaultWorldtypes) WorldGenSettings.makeDefault(registryAccess);
        long seed = (new Random()).nextLong();
        return WorldPreset.NORMAL.create((RegistryAccess.RegistryHolder)registryAccess, seed, true, false);
    }
}
