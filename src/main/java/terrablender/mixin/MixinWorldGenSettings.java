/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.GenerationSettings;
import terrablender.api.WorldPresetUtils;
import terrablender.core.TerraBlender;

import java.util.Optional;
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
