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
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.MaterialRuleManager;
import terrablender.worldgen.IExtendedNoiseGeneratorSettings;

@Mixin(NoiseGeneratorSettings.class)
public class MixinNoiseGeneratorSettings implements IExtendedNoiseGeneratorSettings
{
    @Shadow(remap = false)
    private Holder<MaterialRule> materialRule;

    @Unique
    private MaterialRuleManager.RuleCategory ruleCategory = null;
    @Unique
    private Holder<MaterialRule> namespacedSurfaceRuleSource = null;

    @Inject(method = "materialRule", at = @At("HEAD"), cancellable = true, remap = false)
    private void materialRule(CallbackInfoReturnable<Holder<MaterialRule>> cir)
    {
        if (this.ruleCategory != null)
        {
            if (this.namespacedSurfaceRuleSource == null)
                this.namespacedSurfaceRuleSource = Holder.direct(MaterialRuleManager.getNamespacedRules(this.ruleCategory, new MaterialRule.HolderHolder(this.materialRule)));

            cir.setReturnValue(this.namespacedSurfaceRuleSource);
        }
    }

    @Override
    public void setRuleCategory(MaterialRuleManager.RuleCategory ruleCategory)
    {
        this.ruleCategory = ruleCategory;
        this.namespacedSurfaceRuleSource = null;
    }
}
