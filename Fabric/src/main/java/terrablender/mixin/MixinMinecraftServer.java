/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.util.LevelUtils;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer
{
    // NOTE: We can't inject at HEAD because we're trying to inject after Fabric does.
    // Fabric nukes our initialization if we are called before it.
    @Inject(method = "createLevels", at = @At(value = "FIELD", shift = At.Shift.BEFORE, ordinal = 0), require = 1)
    private void onCreateLevels(ChunkProgressListener listener, CallbackInfo ci)
    {
        LevelUtils.initializeOnServerStart((MinecraftServer)(Object)this);
    }
}
