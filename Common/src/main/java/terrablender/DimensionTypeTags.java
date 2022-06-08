package terrablender;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import terrablender.core.TerraBlender;

public class DimensionTypeTags {

    public static final TagKey<DimensionType> OVERWORLD_REGIONS = create("overworld_regions");
    public static final TagKey<DimensionType> NETHER_REGIONS = create("nether_regions");

    private static TagKey<DimensionType> create(String id) {
        return TagKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(TerraBlender.MOD_ID, id));
    }
}
