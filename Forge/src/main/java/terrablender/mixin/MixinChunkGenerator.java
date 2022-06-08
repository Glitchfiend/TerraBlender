package terrablender.mixin;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.IExtendedChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator implements IExtendedChunkGenerator {

    @Mutable
    @Shadow
    @Final
    private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;

    @Shadow
    @Final
    protected BiomeSource biomeSource;

    @Shadow
    @Final
    private Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    @Inject(method = "lambda$new$3(Lnet/minecraft/world/level/biome/BiomeSource;Ljava/util/function/Function;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void skipInitialFeaturesPerStep(BiomeSource biomeSource, Function function, CallbackInfoReturnable<List> cir) {
        cir.setReturnValue(new ArrayList<>());
    }

    @Override
    public void appendFeaturesPerStep() {
        this.featuresPerStep = Suppliers.memoize(() -> {
            return FeatureSorter.buildFeaturesPerStep(this.biomeSource.possibleBiomes().stream().toList(), biome -> {
                return this.generationSettingsGetter.apply(biome).features();
            }, true);
        });
    }
}
