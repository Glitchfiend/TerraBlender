/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package terrablender.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.util.LevelUtils;

// NOTE: Inject before fabric biome modifications (priority 1000)
// but after fabric has modified the nether biome source (priority 990).
@Mixin(value = MinecraftServer.class, priority = 995)
public class MixinMinecraftServer
{
    @Inject(method = "<init>", at = @At("RETURN"), require = 1)
    private void onInit(CallbackInfo ci)
    {
        LevelUtils.initializeOnServerStart((MinecraftServer)(Object)this);
    }
}
