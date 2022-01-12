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
package terrablender.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.Main;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import terrablender.data.DataPackManager;

import java.util.function.Consumer;

@Mixin(Main.class)
public class MixinMain
{
    private static RegistryAccess registryAccess = null;
    private static WorldGenSettings currentSettings = null;

    // All of this code is designed to replace this:
    // worldgensettings = WorldGenSettings.CODEC.encodeStart(net.minecraft.resources.RegistryWriteOps.create(NbtOps.INSTANCE, registryaccess$registryholder), worldgensettings).flatMap(nbt -> WorldGenSettings.CODEC.parse(registryreadops, nbt)).getOrThrow(false, errorMsg->{});

    @Redirect(method = "main", at=@At(value="INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;getDataTag(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/level/DataPackConfig;)Lnet/minecraft/world/level/storage/WorldData;"))
    private static WorldData onGetDataTag(LevelStorageSource.LevelStorageAccess storageAccess, DynamicOps<Tag> dynamic, DataPackConfig config)
    {
        // Quick and dirty solution to allow us to handle data packs correctly on servers.
        // We can likely get away with only parsing WorldGenSettings and registering the data pack BiomeProvider, but that requires substantially more code.
        storageAccess.getDataTag(dynamic, config);
        return storageAccess.getDataTag(dynamic, config);
    }

    @Redirect(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryWriteOps;create(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/resources/RegistryWriteOps;"))
    private static RegistryWriteOps onCreate(DynamicOps ops, RegistryAccess registryAccess)
    {
        MixinMain.registryAccess = registryAccess;
        return RegistryWriteOps.create(NbtOps.INSTANCE, registryAccess);
    }

    @Redirect(method = "main", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;encodeStart(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"))
    private static <A> DataResult onEncodeStart(Codec codec, DynamicOps ops, A input)
    {
        DataResult result = codec.encodeStart(ops, input);

        if (codec != WorldGenSettings.CODEC || !(input instanceof WorldGenSettings))
            return result;

        currentSettings = (WorldGenSettings)input;
        return result;
    }

    @Redirect(method = "main", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;getOrThrow(ZLjava/util/function/Consumer;)Ljava/lang/Object;"))
    private static Object onGetOrThrow(DataResult dataResult, boolean allowPartial, Consumer<String> onError)
    {
        Object obj = dataResult.getOrThrow(allowPartial, onError);

        if (!(obj instanceof WorldGenSettings) || registryAccess == null || currentSettings == null)
            return obj;

        return DataPackManager.mergeWorldGenSettings(registryAccess, currentSettings, (WorldGenSettings)obj);
    }
}
