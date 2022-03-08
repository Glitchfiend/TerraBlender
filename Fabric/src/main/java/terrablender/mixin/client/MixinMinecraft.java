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

import com.mojang.datafixers.util.Function4;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Shadow
    private void doLoadLevel(String string, RegistryAccess.RegistryHolder registryHolder, Function<LevelStorageSource.LevelStorageAccess, DataPackConfig> function, Function4<LevelStorageSource.LevelStorageAccess, RegistryAccess.RegistryHolder, ResourceManager, DataPackConfig, WorldData> function4, boolean bl, Minecraft.ExperimentalDialogType experimentalDialogType) {}

    @Inject(method = "createLevel", at = @At("HEAD"), cancellable = true)
    public void createLevel(String levelName, LevelSettings levelSettings, RegistryAccess.RegistryHolder registryAccess, WorldGenSettings currentSettings, CallbackInfo ci)
    {
        this.doLoadLevel(levelName, registryAccess, (levelStorageAccess) -> {
            return levelSettings.getDataPackConfig();
        }, (levelStorageAccess, p_167887_, resourceManager, dataPackConfig) -> {
            return MinecraftHooks.createPrimaryLevelData(levelSettings, registryAccess, currentSettings, resourceManager);
        }, false, Minecraft.ExperimentalDialogType.NONE);
        ci.cancel();
    }
}
