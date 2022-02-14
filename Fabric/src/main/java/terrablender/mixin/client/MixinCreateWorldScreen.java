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

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.api.BiomeProviders;
import terrablender.core.TerraBlender;
import terrablender.data.DataPackManager;

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

    @Inject(method = "removed", at = @At("HEAD"))
    public void onRemoved(CallbackInfo ci)
    {
        BiomeProviders.remove(DataPackManager.DATA_PACK_PROVIDER_LOCATION);
    }
}
