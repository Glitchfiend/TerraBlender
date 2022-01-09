/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.hooks;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import terrablender.core.TerraBlender;
import terrablender.data.DataPackManager;

public class MinecraftHooks
{
    public static PrimaryLevelData createPrimaryLevelData(LevelSettings levelSettings, RegistryAccess.RegistryHolder registryAccess, WorldGenSettings currentSettings, ResourceManager resourceManager)
    {
        RegistryWriteOps<JsonElement> registryWriteOps = RegistryWriteOps.create(JsonOps.INSTANCE, registryAccess);
        RegistryReadOps<JsonElement> registryReadOps = RegistryReadOps.createAndLoad(JsonOps.INSTANCE, resourceManager, registryAccess);
        DataResult<WorldGenSettings> worldGenSettingsDataResult = WorldGenSettings.CODEC.encodeStart(registryWriteOps, currentSettings).setLifecycle(Lifecycle.stable()).flatMap((p_167969_) -> {
            return WorldGenSettings.CODEC.parse(registryReadOps, p_167969_);
        });
        WorldGenSettings newSettings = worldGenSettingsDataResult.resultOrPartial(Util.prefix("Error reading worldgen settings after loading data packs: ", TerraBlender.LOGGER::error)).orElse(currentSettings);
        newSettings = DataPackManager.mergeWorldGenSettings(registryAccess, currentSettings, newSettings);
        return new PrimaryLevelData(levelSettings, newSettings, worldGenSettingsDataResult.lifecycle());
    }
}
