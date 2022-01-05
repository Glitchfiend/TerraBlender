/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.config;

import java.nio.file.Path;

public class TerraBlenderConfig extends ConfigFile
{
    public final boolean replaceDefaultWorldtypes;
    public final boolean replaceDefaultNether;
    public final int regionSize;
    public final int largeBiomesRegionSize;
    public final int vanillaRegionWeight;
    public final int datapackRegionWeight;

    public TerraBlenderConfig(Path path)
    {
        super(path);

        Config generalConfig = this.getSubConfig("general");
        this.replaceDefaultWorldtypes = generalConfig.add("Whether to replace the default built-in world types with our own.", "replace_default_worldtypes", true);
        this.replaceDefaultNether = generalConfig.add("Whether to replace the default Nether with our own.", "replace_default_nether", true);
        this.addSubConfig("General settings", "general", generalConfig);

        Config generationSettings = this.getSubConfig("generation_settings");
        this.regionSize = generationSettings.addNumber("The size of regions of biomes from each mod that uses TerraBlender.", "region_size", 3, 2, 6);
        this.largeBiomesRegionSize = generationSettings.addNumber("The size of regions of biomes from each mod that uses TerraBlender when using the large biomes world type.", "large_biomes_region_size", 5, 2, 6);
        this.vanillaRegionWeight = generationSettings.addNumber("The weighting of vanilla biome regions.", "vanilla_region_weight", 10, 0, Integer.MAX_VALUE);
        this.datapackRegionWeight = generationSettings.addNumber("The weighting of data pack biome regions.", "datapack_region_weight", 10, 0, Integer.MAX_VALUE);
        this.addSubConfig("Generation settings", "generation_settings", generationSettings);

        this.save();
    }
}
