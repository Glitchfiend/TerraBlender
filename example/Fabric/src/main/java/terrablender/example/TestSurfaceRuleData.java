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
package terrablender.example;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public class TestSurfaceRuleData
{
    public static final ResourceKey<MaterialRule> SURFACE = ResourceKey.create(Registries.MATERIAL_RULE, Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "surface"));

    public static void bootstrap(BootstrapContext<MaterialRule> context)
    {
        context.register(SURFACE, makeRules(context.lookup(Registries.BIOME)));
    }

    private static final MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRule RED_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.red());
    private static final MaterialRule BLUE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.blue());

    protected static MaterialRule makeRules(HolderGetter<Biome> biomes)
    {
        MaterialCondition isAtOrAboveWaterLevel = MaterialRules.waterBlockCheck(-1, 0);
        MaterialRule grassSurface = MaterialRules.sequence(MaterialRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);

        return MaterialRules.sequence(
            MaterialRules.ifTrue(MaterialRules.isBiome(biomes, TestBiomes.HOT_RED), RED_TERRACOTTA),
            MaterialRules.ifTrue(MaterialRules.isBiome(biomes, TestBiomes.COLD_BLUE), BLUE_TERRACOTTA),

            // Default to a grass and dirt surface
            MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), grassSurface)
        );
    }

    private static MaterialRule makeStateRule(Block block)
    {
        return MaterialRules.state(block.defaultBlockState());
    }
}
