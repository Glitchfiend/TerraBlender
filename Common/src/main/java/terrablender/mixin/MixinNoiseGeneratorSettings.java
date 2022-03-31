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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.RegionType;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.IExtendedNoiseGeneratorSettings;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mixin(NoiseGeneratorSettings.class)
public class MixinNoiseGeneratorSettings implements IExtendedNoiseGeneratorSettings
{
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    private RegionType regionType = null;
    private SurfaceRules.RuleSource namespacedSurfaceRuleSource = null;

    @Inject(method = "overworld", at = @At("RETURN"))
    private static void onOverworld(boolean amplified, boolean largeBiomes, CallbackInfoReturnable<NoiseGeneratorSettings> cir)
    {
        NoiseGeneratorSettings settings = cir.getReturnValue();
        ((IExtendedNoiseGeneratorSettings)(Object)settings).setRegionType(RegionType.OVERWORLD);
    }

    @Inject(method = "nether", at = @At("RETURN"))
    private static void onNether(CallbackInfoReturnable<NoiseGeneratorSettings> cir)
    {
        NoiseGeneratorSettings settings = cir.getReturnValue();
        ((IExtendedNoiseGeneratorSettings)(Object)settings).setRegionType(RegionType.NETHER);
    }

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir)
    {
        if (this.regionType != null)
        {
            if (this.namespacedSurfaceRuleSource == null)
                this.namespacedSurfaceRuleSource = regionType == RegionType.NETHER ? SurfaceRuleManager.getNamespacedRules(SurfaceRuleManager.RuleCategory.NETHER, this.surfaceRule) : SurfaceRuleManager.getNamespacedRules(SurfaceRuleManager.RuleCategory.OVERWORLD, this.surfaceRule);

            cir.setReturnValue(this.namespacedSurfaceRuleSource);
        }
    }

    @Override
    public void setRegionType(RegionType regionType)
    {
        this.regionType = regionType;
    }

    @Override
    public RegionType getRegionType()
    {
        return this.regionType;
    }
}
