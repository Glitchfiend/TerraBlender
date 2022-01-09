/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.api.BiomeProviders;
import terrablender.data.DataPackManager;

@Mixin(ClientLevel.class)
public class MixinClientLevel
{
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci)
    {
        BiomeProviders.remove(DataPackManager.DATA_PACK_PROVIDER_LOCATION);
    }
}
