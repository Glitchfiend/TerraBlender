package terrablender.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import terrablender.worldgen.IExtendedMultiNoiseBiomeSource;
import terrablender.worldgen.IExtendedParameterList;

@Mixin(NoiseBasedChunkGenerator.class)
public class MixinNoiseBasedChunkGenerator {

    @ModifyArg(method = "doCreateBiomes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkAccess;fillBiomesFromNoise(Lnet/minecraft/world/level/biome/BiomeResolver;Lnet/minecraft/world/level/biome/Climate$Sampler;)V"))
    private BiomeResolver modifyBiomeSource(BiomeResolver biomeResolver) {
        if (biomeResolver instanceof MultiNoiseBiomeSource multiNoiseBiomeSource && ((IExtendedParameterList<Holder<Biome>>) multiNoiseBiomeSource.parameters()).isInitialized()) {
            MultiNoiseBiomeSource cloned = ((IExtendedMultiNoiseBiomeSource) multiNoiseBiomeSource).clone();
            Climate.ParameterList<Holder<Biome>> clonedParameterList = ((IExtendedParameterList<Holder<Biome>>) cloned.parameters()).clone();
            ((IExtendedParameterList<Holder<Biome>>) clonedParameterList).recreateUniqueness();
            ((MultiNoiseBiomeSourceAccess) cloned).setParameters(Either.left(clonedParameterList));
            return cloned;
        } else {
            return biomeResolver;
        }
    }

}
