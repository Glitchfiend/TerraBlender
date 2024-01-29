package terrablender.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.jetbrains.annotations.NotNull;

public class TerrablenderOverworldBiomeBuilder extends OverworldBiomeBuilder {

    private final ResourceKey<Biome>[][] beachBiomes;
    private final ResourceKey<Biome>[][] peakBiomes;
    private final ResourceKey<Biome>[][] peakBiomesVariant;
    private final ResourceKey<Biome>[][] slopeBiomes;
    private final ResourceKey<Biome>[][] slopeBiomesVariant;


    /**
     * @param oceans               - Appearing on terrain below sea level, here is the "ocean_biomes" layout:
     *                             [ DEEP-ICY, DEEP-COLD, DEEP-NEUTRAL, DEEP-WARM, DEEP-HOT ]
     *                             [ SHALLOW-ICY, SHALLOW-COLD, SHALLOW-NEUTRAL, SHALLOW-WARM, SHALLOW-HOT ],
     * @param middleBiomes         - Appearing on terrain BELOW weirdness 0 or in unfilled("NULL(nothing)") spots in "middle_biomes_variants", here is the "middle_biomes" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     * @param middleBiomesVariant  - Appearing on terrain ABOVE weirdness 0, here is the "middle_biomes_variant" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     *                             Null values may be passed in, the equivalent biome at the equivalent temperature/humidity index in the middleBiomes array will be used.
     * @param plateauBiomes        - Appearing on elevated flat terrain BELOW weirdness 0 or in unfilled("NULL(nothing)") spots in "plateau_biome_variants", here is the "plateau_biomes" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     * @param plateauBiomesVariant - Appearing on elevated flat terrain ABOVE weirdness 0, here is the "plateau_biomes_variant" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     *                             Null values may be passed in, the equivalent biome at the equivalent temperature/humidity index in the plateauBiomes array will be used.
     * @param shatteredBiomes      - Appearing on shattered terrain here is the "shattered_biomes" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     * @param beachBiomes          - Appearing on terrain bordering oceans, here is the "beach_biomes" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     * @param peakBiomes           - Appearing on mountainous terrain & BELOW weirdness 0, here is the "peak_biomes" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     * @param peakBiomesVariant    - Appearing on mountainous terrain & ABOVE weirdness 0, here is the "peak_biome_variants" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     *                             Null values may be passed in, the equivalent biome at the equivalent temperature/humidity index in the peakBiomes array will be used.
     * @param slopeBiomes          - Appearing on sloped terrain, near mountainous terrain, & BELOW weirdness 0, here is the "slope_biomes" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     * @param slopeBiomesVariant   - Appearing on sloped terrain, near mountainous terrain, & ABOVE weirdness 0, here is the "slope_biome_variants" layout:
     *                             [ ARID-ICY, DRY-ICY, NEUTRAL-ICY, WET-ICY, HUMID-ICY ],
     *                             [ ARID-COLD, DRY-COLD, NEUTRAL-COLD, WET-COLD, HUMID-COLD ],
     *                             [ ARID-NEUTRAL, DRY-NEUTRAL, NEUTRAL-NEUTRAL, WET-NEUTRAL, HUMID-NEUTRAL ],
     *                             [ ARID-WARM, DRY-WARM, NEUTRAL-WARM, WET-WARM, HUMID-WARM ],
     *                             [ ARID-HOT, DRY-HOT, NEUTRAL-HOT, WET-HOT, HUMID-HOT ]
     *                             Null values may be passed in, the equivalent biome at the equivalent temperature/humidity index in the slopeBiomes array will be used.
     */
    public TerrablenderOverworldBiomeBuilder(ResourceKey<Biome>[][] oceans, ResourceKey<Biome>[][] middleBiomes,
                                             ResourceKey<Biome>[][] middleBiomesVariant, ResourceKey<Biome>[][] plateauBiomes,
                                             ResourceKey<Biome>[][] plateauBiomesVariant, ResourceKey<Biome>[][] shatteredBiomes,
                                             ResourceKey<Biome>[][] beachBiomes, ResourceKey<Biome>[][] peakBiomes,
                                             ResourceKey<Biome>[][] peakBiomesVariant, ResourceKey<Biome>[][] slopeBiomes,
                                             ResourceKey<Biome>[][] slopeBiomesVariant) {
        this.OCEANS = oceans;

        this.MIDDLE_BIOMES = middleBiomes;
        this.MIDDLE_BIOMES_VARIANT = middleBiomesVariant;

        this.PLATEAU_BIOMES = plateauBiomes;
        this.PLATEAU_BIOMES_VARIANT = plateauBiomesVariant;

        this.SHATTERED_BIOMES = shatteredBiomes;

        this.beachBiomes = beachBiomes;

        this.peakBiomes = peakBiomes;
        this.peakBiomesVariant = peakBiomesVariant;

        this.slopeBiomes = slopeBiomes;
        this.slopeBiomesVariant = slopeBiomesVariant;
    }

    @Override
    public @NotNull ResourceKey<Biome> pickBeachBiome(int temp, int humidity) {
        return beachBiomes[temp][humidity];
    }

    @Override
    public @NotNull ResourceKey<Biome> pickPeakBiome(int temp, int humidity, Climate.Parameter weirdness) {
        ResourceKey<Biome> peakBiome = this.peakBiomes[temp][humidity];
        if (weirdness.max() < 0L) {
            return peakBiome;
        } else {
            ResourceKey<Biome> peakVariant = peakBiomesVariant[temp][humidity];
            return peakVariant == null ? peakBiome : peakVariant;
        }
    }

    @Override
    public @NotNull ResourceKey<Biome> pickSlopeBiome(int temp, int humidity, Climate.Parameter weirdness) {
        ResourceKey<Biome> slopeBiome = this.slopeBiomes[temp][humidity];
        if (weirdness.max() < 0L) {
            return slopeBiome;
        } else {
            ResourceKey<Biome> slopeVariant = slopeBiomesVariant[temp][humidity];
            return slopeVariant == null ? slopeBiome : slopeVariant;
        }
    }
}
