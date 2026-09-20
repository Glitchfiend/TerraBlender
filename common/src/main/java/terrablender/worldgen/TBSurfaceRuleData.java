/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.material.VanillaMaterialRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.SequenceRule;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import terrablender.api.MaterialRuleManager;

import java.util.List;

public class TBSurfaceRuleData
{
    public static MaterialRule withStages(MaterialRuleManager.RuleCategory category, MaterialRule vanilla, HolderGetter<Biome> biomes)
    {
        List<MaterialRule> beforeBedrockRules = MaterialRuleManager.getDefaultRuleAdditionsForStage(category, MaterialRuleManager.RuleStage.BEFORE_BEDROCK, biomes);
        List<MaterialRule> afterBedrockRules = MaterialRuleManager.getDefaultRuleAdditionsForStage(category, MaterialRuleManager.RuleStage.AFTER_BEDROCK, biomes);
        if (beforeBedrockRules.isEmpty() && afterBedrockRules.isEmpty())
            return vanilla;

        MaterialRule resolved = vanilla;
        while (resolved instanceof MaterialRule.HolderHolder(net.minecraft.core.Holder<MaterialRule> holder))
            resolved = holder.value();
        List<MaterialRule> sequence = resolved instanceof SequenceRule rules ? rules.sequence() : List.of(vanilla);
        ImmutableList.Builder<MaterialRule> result = ImmutableList.builder();
        result.addAll(beforeBedrockRules);

        int index = 0;
        while (index < sequence.size() && sequence.get(index) instanceof MaterialRule.HolderHolder reference && (reference.holder().is(VanillaMaterialRules.BEDROCK_FLOOR) || reference.holder().is(VanillaMaterialRules.BEDROCK_ROOF)))
            result.add(sequence.get(index++));

        if (!afterBedrockRules.isEmpty())
        {
            MaterialRule additions = MaterialRules.sequence(afterBedrockRules);
            // Retain the Overworld API's surface-only AFTER_BEDROCK behavior.
            result.add(category == MaterialRuleManager.RuleCategory.OVERWORLD
                    ? MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), additions) : additions);
        }
        result.addAll(sequence.subList(index, sequence.size()));
        return MaterialRules.sequence(result.build());
    }

    private static final MaterialRule AIR = makeStateRule(Blocks.AIR);
    private static final MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final MaterialRule WHITE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.white());
    private static final MaterialRule ORANGE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.orange());
    private static final MaterialRule TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
    private static final MaterialRule RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final MaterialRule RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final MaterialRule STONE = makeStateRule(Blocks.STONE);
    private static final MaterialRule DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
    private static final MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRule PODZOL = makeStateRule(Blocks.PODZOL);
    private static final MaterialRule COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
    private static final MaterialRule MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRule CALCITE = makeStateRule(Blocks.CALCITE);
    private static final MaterialRule GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final MaterialRule SAND = makeStateRule(Blocks.SAND);
    private static final MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final MaterialRule PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final MaterialRule SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final MaterialRule MUD = makeStateRule(Blocks.MUD);
    private static final MaterialRule POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
    private static final MaterialRule ICE = makeStateRule(Blocks.ICE);
    private static final MaterialRule WATER = makeStateRule(Blocks.WATER);
    private static final MaterialRule LAVA = makeStateRule(Blocks.LAVA);
    private static final MaterialRule NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final MaterialRule SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
    private static final MaterialRule SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
    private static final MaterialRule BASALT = makeStateRule(Blocks.BASALT);
    private static final MaterialRule BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
    private static final MaterialRule WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
    private static final MaterialRule WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
    private static final MaterialRule NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
    private static final MaterialRule CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
    private static final MaterialRule ENDSTONE = makeStateRule(Blocks.END_STONE);

    private static MaterialRule makeStateRule(Block block)
    {
        return MaterialRules.state(block.defaultBlockState());
    }

    public static MaterialRule overworld(HolderGetter<Biome> biomes)
    {
        return overworldLike(biomes, true, false, true);
    }

    // Up-to-date as of 1.21.4
    public static MaterialRule overworldLike(HolderGetter<Biome> biomes, boolean checkAbovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor)
    {
        MaterialCondition isBlockAboveY97WithVariationAbove = MaterialRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        MaterialCondition isBlockAboveY256 = MaterialRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        MaterialCondition isSurfaceAbove63WithVariationBelow = MaterialRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        MaterialCondition isSurfaceAboveY74WithVariationAbove = MaterialRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        MaterialCondition isBlockAboveY60 = MaterialRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        MaterialCondition isBlockAboveY62 = MaterialRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        MaterialCondition isBlockAboveY63 = MaterialRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        MaterialCondition isInOrAboveShallowWater = MaterialRules.waterBlockCheck(-1, 0);
        MaterialCondition isAboveWater = MaterialRules.waterBlockCheck(0, 0);
        MaterialCondition isInOrAboveDeepWaterWithVariationBelow = MaterialRules.waterStartCheck(-6, -1);
        MaterialCondition isHole = MaterialRules.hole();
        MaterialCondition isFrozenOcean = MaterialRules.isBiome(biomes, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        MaterialCondition isSteep = MaterialRules.steep();
        MaterialRule surfaceGrassOrDirtIfSubmerged = MaterialRules.sequence(MaterialRules.ifTrue(isAboveWater, GRASS_BLOCK), DIRT);
        MaterialRule sandOrSandstoneCeiling = MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.CEILING), SANDSTONE), SAND);
        MaterialRule gravelOrStoneCeiling = MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.CEILING), STONE), GRAVEL);
        MaterialCondition isSandyShoreOrOcean = MaterialRules.isBiome(biomes, Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        MaterialCondition isDesert = MaterialRules.isBiome(biomes, Biomes.DESERT);

        MaterialRule onAndUnderFloorSurfaceRules = MaterialRules.sequence(
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.STONY_PEAKS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.noiseCondition2d(Noises.CALCITE, -0.0125D, 0.0125D),
                        CALCITE
                    ),
                    STONE
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.STONY_SHORE),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.noiseCondition2d(Noises.GRAVEL, -0.05D, 0.05D),
                        gravelOrStoneCeiling
                    ),
                    STONE
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_HILLS),
                MaterialRules.ifTrue(
                    surfaceNoiseAbove(1.0D),
                    STONE
                )
            ),
            MaterialRules.ifTrue(
                isSandyShoreOrOcean,
                sandOrSandstoneCeiling
            ),
            MaterialRules.ifTrue(isDesert, sandOrSandstoneCeiling),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.DRIPSTONE_CAVES),
                STONE
            )
        );

        MaterialRule powderedSnowSmallPatches = MaterialRules.ifTrue(
            MaterialRules.noiseCondition2d(Noises.POWDER_SNOW, 0.45D, 0.58D),
            MaterialRules.ifTrue(isAboveWater, POWDER_SNOW)
        );

        MaterialRule powderedSnowLargePatches = MaterialRules.ifTrue(
            MaterialRules.noiseCondition2d(Noises.POWDER_SNOW, 0.35D, 0.6D),
            MaterialRules.ifTrue(isAboveWater, POWDER_SNOW)
        );

        MaterialRule underFloorSurfaceRules = MaterialRules.sequence(
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.FROZEN_PEAKS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(isSteep, PACKED_ICE),
                    MaterialRules.ifTrue(
                        MaterialRules.noiseCondition2d(Noises.PACKED_ICE, -0.5D, 0.2D),
                        PACKED_ICE
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.noiseCondition2d(Noises.ICE, -0.0625D, 0.025D),
                        ICE
                    ),
                    MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.SNOWY_SLOPES),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(isSteep, STONE),
                    powderedSnowSmallPatches,
                    MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.JAGGED_PEAKS),
                STONE
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.GROVE),
                MaterialRules.sequence(
                    powderedSnowSmallPatches,
                    DIRT
                )
            ),
            onAndUnderFloorSurfaceRules,
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_SAVANNA),
                MaterialRules.ifTrue(
                    surfaceNoiseAbove(1.75D),
                    STONE
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_GRAVELLY_HILLS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(2.0D),
                        gravelOrStoneCeiling
                    ),
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(1.0D),
                        STONE
                    ),
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(-1.0D),
                        DIRT
                    ),
                    gravelOrStoneCeiling
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.MANGROVE_SWAMP),
                MUD
            ),
            DIRT
        );

        MaterialRule shallowFloorSurfaceRules = MaterialRules.sequence(
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.FROZEN_PEAKS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(isSteep, PACKED_ICE),
                    MaterialRules.ifTrue(
                        MaterialRules.noiseCondition2d(Noises.PACKED_ICE, 0.0D, 0.2D),
                        PACKED_ICE
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.noiseCondition2d(Noises.ICE, 0.0D, 0.025D),
                        ICE
                    ),
                    MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.SNOWY_SLOPES),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(isSteep, STONE),
                    powderedSnowLargePatches,
                    MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
                )
            ),
            // Place stone on the cliff faces and snow on top for the jagged peaks
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.JAGGED_PEAKS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(isSteep, STONE),
                    MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.GROVE),
                MaterialRules.sequence(
                    powderedSnowLargePatches,
                    MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
                )
            ),
            onAndUnderFloorSurfaceRules,
            // Windswept savanna stone mixed with coarse dirt
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_SAVANNA),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(1.75D),
                        STONE
                    ),
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(-0.5D),
                        COARSE_DIRT
                    )
                )
            ),
            // Windswept gravelly hills: Gravel, stone and grass mixed
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_GRAVELLY_HILLS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(2.0D),
                        gravelOrStoneCeiling
                    ),
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(1.0D),
                        STONE
                    ),
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(-1.0D),
                        surfaceGrassOrDirtIfSubmerged
                    ),
                    gravelOrStoneCeiling
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(1.75D),
                        COARSE_DIRT
                    ),
                    MaterialRules.ifTrue(
                        surfaceNoiseAbove(-0.95D),
                        PODZOL
                    )
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.ICE_SPIKES),
                MaterialRules.ifTrue(isAboveWater, SNOW_BLOCK)
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.MANGROVE_SWAMP),
                MUD
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.MUSHROOM_FIELDS),
                MYCELIUM
            ),
            surfaceGrassOrDirtIfSubmerged
        );

        MaterialCondition isSuitableSurfaceNoiseLower = MaterialRules.noiseCondition2d(Noises.SURFACE, -0.909D, -0.5454D);
        MaterialCondition isSuitableSurfaceNoiseMid = MaterialRules.noiseCondition2d(Noises.SURFACE, -0.1818D, 0.1818D);
        MaterialCondition isSuitableSurfaceNoiseUpper = MaterialRules.noiseCondition2d(Noises.SURFACE, 0.5454D, 0.909D);

        MaterialRule surfaceRules = MaterialRules.sequence(
            // Noisy biome floors (as seen in the wooded badlands, swamp and mangrove swamp)
            MaterialRules.ifTrue(
                MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.WOODED_BADLANDS),
                        MaterialRules.ifTrue(
                            isBlockAboveY97WithVariationAbove,
                            MaterialRules.sequence(
                                MaterialRules.ifTrue(isSuitableSurfaceNoiseLower, COARSE_DIRT),
                                MaterialRules.ifTrue(isSuitableSurfaceNoiseMid, COARSE_DIRT),
                                MaterialRules.ifTrue(isSuitableSurfaceNoiseUpper, COARSE_DIRT),
                                surfaceGrassOrDirtIfSubmerged
                            )
                        )
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.SWAMP),
                        MaterialRules.ifTrue(
                            isBlockAboveY62,
                            MaterialRules.ifTrue(
                                MaterialRules.not(isBlockAboveY63),
                                MaterialRules.ifTrue(
                                    MaterialRules.noiseCondition2d(Noises.SWAMP, 0.0D),
                                    WATER
                                )
                            )
                        )
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.MANGROVE_SWAMP),
                        MaterialRules.ifTrue(
                            isBlockAboveY60,
                            MaterialRules.ifTrue(
                                MaterialRules.not(isBlockAboveY63),
                                MaterialRules.ifTrue(
                                    MaterialRules.noiseCondition2d(Noises.SWAMP, 0.0D),
                                    WATER
                                )
                            )
                        )
                    )
                )
            ),
            // Badlands surface rules
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                        MaterialRules.sequence(
                            MaterialRules.ifTrue(isBlockAboveY256, ORANGE_TERRACOTTA),
                            MaterialRules.ifTrue(
                                isSurfaceAboveY74WithVariationAbove,
                                MaterialRules.sequence(
                                    MaterialRules.ifTrue(isSuitableSurfaceNoiseLower, TERRACOTTA),
                                    MaterialRules.ifTrue(isSuitableSurfaceNoiseMid, TERRACOTTA),
                                    MaterialRules.ifTrue(isSuitableSurfaceNoiseUpper, TERRACOTTA),
                                    MaterialRules.bandlands()
                                )
                            ),
                            MaterialRules.ifTrue(
                                isInOrAboveShallowWater,
                                MaterialRules.sequence(
                                    MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.CEILING), RED_SANDSTONE),
                                    RED_SAND
                                )
                            ),
                            MaterialRules.ifTrue(
                                MaterialRules.not(isHole),
                                ORANGE_TERRACOTTA
                            ),
                            MaterialRules.ifTrue(isInOrAboveDeepWaterWithVariationBelow, WHITE_TERRACOTTA),
                            gravelOrStoneCeiling
                        )
                    ),
                    MaterialRules.ifTrue(
                        isSurfaceAbove63WithVariationBelow,
                        MaterialRules.sequence(
                            MaterialRules.ifTrue(
                                isBlockAboveY63,
                                MaterialRules.ifTrue(
                                    MaterialRules.not(isSurfaceAboveY74WithVariationAbove),
                                    ORANGE_TERRACOTTA
                                )
                            ),
                            MaterialRules.bandlands()
                        )
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                        MaterialRules.ifTrue(isInOrAboveDeepWaterWithVariationBelow, WHITE_TERRACOTTA)
                    )
                )
            ),
            // Frozen ocean ice surfaces and shallow floors
            MaterialRules.ifTrue(
                MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                MaterialRules.ifTrue(
                    isInOrAboveShallowWater,
                    MaterialRules.sequence(
                        MaterialRules.ifTrue(
                            isFrozenOcean,
                            MaterialRules.ifTrue(
                                isHole,
                                MaterialRules.sequence(
                                    MaterialRules.ifTrue(isAboveWater, AIR),
                                    MaterialRules.ifTrue(MaterialRules.temperature(), ICE),
                                    WATER
                                )
                            )
                        ),
                        shallowFloorSurfaceRules
                    )
                )
            ),
            MaterialRules.ifTrue(
                isInOrAboveDeepWaterWithVariationBelow,
                MaterialRules.sequence(
                    // Fill frozen ocean floor holes with water
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                        MaterialRules.ifTrue(
                            isFrozenOcean,
                            MaterialRules.ifTrue(isHole, WATER)
                        )
                    ),
                    // Under the floor surface rules
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                        underFloorSurfaceRules
                    ),
                    // Place sandstone under sand deep under the floor in sandy shores or ocean
                    MaterialRules.ifTrue(
                        isSandyShoreOrOcean,
                        MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, true, 6, CaveSurface.FLOOR), SANDSTONE)
                    ),
                    // Place sandstone very deep under the floor of deserts
                    MaterialRules.ifTrue(
                        isDesert,
                        MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, true, 30, CaveSurface.FLOOR), SANDSTONE)
                    )
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS),
                        STONE
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN),
                        sandOrSandstoneCeiling
                    ),
                    gravelOrStoneCeiling
                )
            )
        );

        ImmutableList.Builder<MaterialRule> builder = ImmutableList.builder();

        // Append before bedrock surface rules
        builder.addAll(MaterialRuleManager.getDefaultRuleAdditionsForStage(MaterialRuleManager.RuleCategory.OVERWORLD, MaterialRuleManager.RuleStage.BEFORE_BEDROCK, biomes));

        if (bedrockRoof)
            builder.add(MaterialRules.ifTrue(MaterialRules.not(MaterialRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));

        if (bedrockFloor)
            builder.add(MaterialRules.ifTrue(MaterialRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));

        List<MaterialRule> afterBedrockRules = MaterialRuleManager.getDefaultRuleAdditionsForStage(MaterialRuleManager.RuleCategory.OVERWORLD, MaterialRuleManager.RuleStage.AFTER_BEDROCK, biomes);

        // Add after bedrock surface rules. These run before Vanilla's surface rules.
        if (!afterBedrockRules.isEmpty())
        {
            ImmutableList.Builder<MaterialRule> newSurfaceRules = ImmutableList.builder();
            newSurfaceRules.addAll(afterBedrockRules);
            newSurfaceRules.add(surfaceRules);
            surfaceRules = MaterialRules.sequence(newSurfaceRules.build().toArray(MaterialRule[]::new));
        }

        MaterialRule surfaceRulesWithPreliminarySurfaceCheck = MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), surfaceRules);
        builder.add(checkAbovePreliminarySurface ? surfaceRulesWithPreliminarySurfaceCheck : surfaceRules);
        builder.add(MaterialRules.ifTrue(MaterialRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));

        return MaterialRules.sequence(builder.build().toArray((count) ->
        {
            return new MaterialRule[count];
        }));
    }

    public static MaterialRule nether(HolderGetter<Biome> biomes)
    {
        MaterialCondition isAbove31 = MaterialRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
        MaterialCondition isAbove32 = MaterialRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
        MaterialCondition yStart30 = MaterialRules.yStartCheck(VerticalAnchor.absolute(30), 0);
        MaterialCondition isBelow35 = MaterialRules.not(MaterialRules.yStartCheck(VerticalAnchor.absolute(35), 0));
        MaterialCondition isTop5Blocks = MaterialRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
        MaterialCondition isHole = MaterialRules.hole();
        MaterialCondition isSuitableSoulSandNoise = MaterialRules.noiseCondition2d(Noises.SOUL_SAND_LAYER, -0.012D);
        MaterialCondition surfacerules$conditionsource7 = MaterialRules.noiseCondition2d(Noises.GRAVEL_LAYER, -0.012D);
        MaterialCondition isSuitablePatchNoise = MaterialRules.noiseCondition2d(Noises.PATCH, -0.012D);
        MaterialCondition isSuitableNetherrackNoise = MaterialRules.noiseCondition2d(Noises.NETHERRACK, 0.54D);
        MaterialCondition surfacerules$conditionsource10 = MaterialRules.noiseCondition2d(Noises.NETHER_WART, 1.17D);
        MaterialCondition isStateSelectorNoiseSuitable = MaterialRules.noiseCondition2d(Noises.NETHER_STATE_SELECTOR, 0.0D);

        MaterialRule gravelPatchRules = MaterialRules.ifTrue(isSuitablePatchNoise, MaterialRules.ifTrue(yStart30, MaterialRules.ifTrue(isBelow35, GRAVEL)));

        MaterialRule bedrockRules = MaterialRules.sequence(
            MaterialRules.ifTrue(
                    MaterialRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
                    BEDROCK
            ),
            MaterialRules.ifTrue(MaterialRules.not(MaterialRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK)
        );

        MaterialRule surfaceRules = MaterialRules.sequence(
            MaterialRules.ifTrue(isTop5Blocks, NETHERRACK),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.BASALT_DELTAS),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, true, CaveSurface.CEILING), BASALT),
                    MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                        MaterialRules.sequence(
                            gravelPatchRules,
                            MaterialRules.ifTrue(isStateSelectorNoiseSuitable, BASALT),
                            BLACKSTONE
                        )
                    )
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.SOUL_SAND_VALLEY),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, true, CaveSurface.CEILING),
                        MaterialRules.sequence(
                            MaterialRules.ifTrue(isStateSelectorNoiseSuitable, SOUL_SAND), SOUL_SOIL
                        )
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                        MaterialRules.sequence(
                            gravelPatchRules,
                            MaterialRules.ifTrue(isStateSelectorNoiseSuitable, SOUL_SAND),
                            SOUL_SOIL
                        )
                    )
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.not(isAbove32),
                        MaterialRules.ifTrue(isHole, LAVA)
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.WARPED_FOREST),
                        MaterialRules.ifTrue(
                            MaterialRules.not(isSuitableNetherrackNoise),
                            MaterialRules.ifTrue(
                                isAbove31,
                                MaterialRules.sequence(
                                    MaterialRules.ifTrue(surfacerules$conditionsource10, WARPED_WART_BLOCK),
                                    WARPED_NYLIUM
                                )
                            )
                        )
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.isBiome(biomes, Biomes.CRIMSON_FOREST),
                        MaterialRules.ifTrue(
                            MaterialRules.not(isSuitableNetherrackNoise),
                            MaterialRules.ifTrue(isAbove31,
                                MaterialRules.sequence(
                                    MaterialRules.ifTrue(surfacerules$conditionsource10, NETHER_WART_BLOCK),
                                    CRIMSON_NYLIUM
                                )
                            )
                        )
                    )
                )
            ),
            MaterialRules.ifTrue(
                MaterialRules.isBiome(biomes, Biomes.NETHER_WASTES),
                MaterialRules.sequence(
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                        MaterialRules.ifTrue(
                            isSuitableSoulSandNoise,
                            MaterialRules.sequence(
                                MaterialRules.ifTrue(
                                    MaterialRules.not(isHole),
                                    MaterialRules.ifTrue(yStart30, MaterialRules.ifTrue(isBelow35, SOUL_SAND))
                                ),
                                NETHERRACK
                            )
                        )
                    ),
                    MaterialRules.ifTrue(
                        MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                        MaterialRules.ifTrue(
                            isAbove31,
                            MaterialRules.ifTrue(
                                isBelow35,
                                MaterialRules.ifTrue(
                                    surfacerules$conditionsource7,
                                    MaterialRules.sequence(
                                        MaterialRules.ifTrue(isAbove32, GRAVEL),
                                        MaterialRules.ifTrue(MaterialRules.not(isHole), GRAVEL)
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            NETHERRACK
        );

        ImmutableList.Builder<MaterialRule> builder = ImmutableList.builder();
        builder.addAll(MaterialRuleManager.getDefaultRuleAdditionsForStage(MaterialRuleManager.RuleCategory.NETHER, MaterialRuleManager.RuleStage.BEFORE_BEDROCK, biomes));
        builder.add(bedrockRules);
        builder.addAll(MaterialRuleManager.getDefaultRuleAdditionsForStage(MaterialRuleManager.RuleCategory.NETHER, MaterialRuleManager.RuleStage.AFTER_BEDROCK, biomes));
        builder.add(surfaceRules);
        return MaterialRules.sequence(builder.build().toArray(MaterialRule[]::new));
    }

    public static MaterialRule end(HolderGetter<Biome> biomes)
    {
        return ENDSTONE;
    }

    public static MaterialRule air()
    {
        return AIR;
    }

    private static MaterialCondition surfaceNoiseAbove(double value)
    {
        return MaterialRules.noiseCondition2d(Noises.SURFACE, value / 8.25D, Double.MAX_VALUE);
    }
}
