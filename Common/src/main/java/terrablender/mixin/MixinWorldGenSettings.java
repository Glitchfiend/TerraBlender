/*
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
package terrablender.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.WorldPresetUtils;
import terrablender.core.TerraBlender;

import java.util.Properties;
import java.util.Random;

@Mixin(WorldGenSettings.class)
public class MixinWorldGenSettings
{
    @Inject(method = "create", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/core/RegistryAccess;registryOrThrow(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/core/Registry;"), cancellable = true)
    private static void onCreate(RegistryAccess registryAccess, Properties properties, CallbackInfoReturnable<WorldGenSettings> cir)
    {
        if (!TerraBlender.CONFIG.replaceDefaultWorldtypes) return;

        String levelSeed = (String)properties.get("level-seed");
        String levelType = (String)properties.get("level-type");
        boolean generateFeatures = Boolean.parseBoolean((String)properties.get("generate-structures"));
        long seed = (new Random()).nextLong();

        if (!levelSeed.isEmpty())
        {
            try
            {
                long j = Long.parseLong(levelSeed);
                if (j != 0L) seed = j;
            }
            catch (NumberFormatException numberformatexception)
            {
                seed = (long)levelSeed.hashCode();
            }
        }

        switch (levelType)
        {
            case "amplified":
                cir.setReturnValue(WorldPresetUtils.settings(registryAccess, seed, generateFeatures, false, WorldPresetUtils.dimensions(registryAccess, seed), WorldPresetUtils.amplifiedChunkGenerator(registryAccess, seed)));
                break;
            case "largebiomes":
                cir.setReturnValue(WorldPresetUtils.settings(registryAccess, seed, generateFeatures, false, WorldPresetUtils.dimensions(registryAccess, seed), WorldPresetUtils.largeBiomesChunkGenerator(registryAccess, seed)));
                break;
            default:
                cir.setReturnValue(WorldPresetUtils.settings(registryAccess, seed, generateFeatures, false, WorldPresetUtils.dimensions(registryAccess, seed), WorldPresetUtils.overworldChunkGenerator(registryAccess, seed)));
                break;
        }
    }
}
