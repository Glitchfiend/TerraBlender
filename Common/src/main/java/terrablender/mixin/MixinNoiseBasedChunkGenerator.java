package terrablender.mixin;

import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import terrablender.worldgen.IExtendedMultiNoiseBiomeSource;

@Mixin(NoiseBasedChunkGenerator.class)
public class MixinNoiseBasedChunkGenerator {

    @ModifyArg(method = "doCreateBiomes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkAccess;fillBiomesFromNoise(Lnet/minecraft/world/level/biome/BiomeResolver;Lnet/minecraft/world/level/biome/Climate$Sampler;)V"))
    private BiomeResolver modifyBiomeSource(BiomeResolver biomeResolver) {
        if (biomeResolver instanceof MultiNoiseBiomeSource multiNoiseBiomeSource) {
            return ((IExtendedMultiNoiseBiomeSource) multiNoiseBiomeSource).clone();
        } else {
            return biomeResolver;
        }
    }

}
