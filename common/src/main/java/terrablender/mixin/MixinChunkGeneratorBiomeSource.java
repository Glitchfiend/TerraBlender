package terrablender.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import terrablender.worldgen.IExtendedMultiNoiseBiomeSource;
import terrablender.worldgen.IExtendedParameterList;

@Mixin(ChunkGenerator.class)
public class MixinChunkGeneratorBiomeSource {

    @ModifyExpressionValue(method = "doCreateBiomes", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;biomeSource:Lnet/minecraft/world/level/biome/BiomeSource;"), remap = false)
    private BiomeSource modifyBiomeSource(BiomeSource biomeSource) {
        if (biomeSource instanceof MultiNoiseBiomeSource multiNoiseBiomeSource && ((IExtendedParameterList<Holder<Biome>>) multiNoiseBiomeSource.parameters()).isInitialized()) {
            MultiNoiseBiomeSource cloned = ((IExtendedMultiNoiseBiomeSource) multiNoiseBiomeSource).clone();
            Climate.ParameterList<Holder<Biome>> clonedParameterList = ((IExtendedParameterList<Holder<Biome>>) cloned.parameters()).clone();
            ((IExtendedParameterList<Holder<Biome>>) clonedParameterList).recreateUniqueness();
            ((MultiNoiseBiomeSourceAccess) cloned).setParameters(Either.left(clonedParameterList));
            return cloned;
        } else {
            return biomeSource;
        }
    }

}
