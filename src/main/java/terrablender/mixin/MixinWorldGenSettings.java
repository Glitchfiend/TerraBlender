/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.GenerationSettings;

import java.util.Optional;
import java.util.Properties;

@Mixin(WorldGenSettings.class)
public class MixinWorldGenSettings
{
    @Inject(method = "create", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/core/RegistryAccess;registryOrThrow(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/core/Registry;"))
    private static void onCreate(RegistryAccess registryAccess, Properties properties, CallbackInfoReturnable<WorldGenSettings> cir)
    {
        String levelType = (String)properties.get("level-type");
        Optional<WorldGenSettings> settings = GenerationSettings.getDefaultWorldGenSettingsOverride();

        if (levelType.equals("default") && settings.isPresent())
        {
            cir.setReturnValue(settings.get());
        }
    }
}
