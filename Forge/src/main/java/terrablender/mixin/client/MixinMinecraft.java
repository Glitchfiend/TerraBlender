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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import terrablender.hooks.MinecraftHooks;

import java.util.function.Function;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Shadow
    private void doLoadLevel(String p_91220_, RegistryAccess.RegistryHolder p_91221_, Function<LevelStorageSource.LevelStorageAccess, DataPackConfig> p_91222_, Function4<LevelStorageSource.LevelStorageAccess, RegistryAccess.RegistryHolder, ResourceManager, DataPackConfig, WorldData> p_91223_, boolean p_91224_, Minecraft.ExperimentalDialogType p_91225_, boolean creating) {}

    @Overwrite
    public void createLevel(String levelName, LevelSettings levelSettings, RegistryAccess.RegistryHolder registryAccess, WorldGenSettings currentSettings)
    {
        this.doLoadLevel(levelName, registryAccess, (levelStorageAccess) -> {
            return levelSettings.getDataPackConfig();
        }, (levelStorageAccess, p_167887_, resourceManager, dataPackConfig) -> {
            return MinecraftHooks.createPrimaryLevelData(levelSettings, registryAccess, currentSettings, resourceManager);
        }, false, Minecraft.ExperimentalDialogType.NONE, true);
    }
}
