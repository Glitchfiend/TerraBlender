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
package terrablender.mixin;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import terrablender.api.SurfaceRuleManager;

@Mixin(NoiseGeneratorSettings.class)
public class MixinNoiseGeneratorSettings
{
    @Redirect(method="overworld", at=@At(value="INVOKE", target="net/minecraft/data/worldgen/SurfaceRuleData.overworld()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;"))
    private static SurfaceRules.RuleSource replaceOverworldRules()
    {
        return SurfaceRuleManager.getNamespacedRules(SurfaceRuleManager.RuleCategory.OVERWORLD);
    }

    @Redirect(method="nether", at=@At(value="INVOKE", target="net/minecraft/data/worldgen/SurfaceRuleData.nether()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;"))
    private static SurfaceRules.RuleSource replaceNetherRules()
    {
        return SurfaceRuleManager.getNamespacedRules(SurfaceRuleManager.RuleCategory.NETHER);
    }
}
