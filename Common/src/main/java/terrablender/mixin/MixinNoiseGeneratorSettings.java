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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.RegionType;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.IExtendedNoiseGeneratorSettings;

@Mixin(NoiseGeneratorSettings.class)
public class MixinNoiseGeneratorSettings implements IExtendedNoiseGeneratorSettings
{
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRuleManager.RuleCategory ruleCategory = null;
    @Unique
    private SurfaceRules.RuleSource namespacedSurfaceRuleSource = null;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir)
    {
        if (this.ruleCategory != null)
        {
            if (this.namespacedSurfaceRuleSource == null)
                this.namespacedSurfaceRuleSource = SurfaceRuleManager.getNamespacedRules(this.ruleCategory, this.surfaceRule);

            cir.setReturnValue(this.namespacedSurfaceRuleSource);
        }
    }

    @Override
    public void setRuleCategory(SurfaceRuleManager.RuleCategory ruleCategory)
    {
        this.ruleCategory = ruleCategory;
    }
}
