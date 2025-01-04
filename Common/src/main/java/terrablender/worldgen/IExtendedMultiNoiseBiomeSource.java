package terrablender.worldgen;

import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

public interface IExtendedMultiNoiseBiomeSource extends Cloneable {

    MultiNoiseBiomeSource clone();

}
