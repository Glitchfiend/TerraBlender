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

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.core.TerraBlender;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mixin(BuiltInRegistries.class)
public abstract class MixinBuiltInRegistries
{
    @Shadow
    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, BuiltInRegistries.RegistryBootstrap<T> bootstrap) { return null; }

    @Inject(method="registerSimple(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/registries/BuiltInRegistries$RegistryBootstrap;)Lnet/minecraft/core/Registry;", at=@At("HEAD"), cancellable = true)
    private static void registerSimple(ResourceKey key, BuiltInRegistries.RegistryBootstrap bootstrap, CallbackInfoReturnable<Registry> cir)
    {
        if (key == Registries.MATERIAL_RULE)
        {
            cir.setReturnValue(registerSimple(key, Lifecycle.stable(), (registry -> {
                // Run the Vanilla bootstrap
                bootstrap.run(registry);

                // Run our bootstrap
                return Registry.register(registry, new ResourceLocation(TerraBlender.MOD_ID, "merged"), NamespacedSurfaceRuleSource.CODEC.codec());
            })));
        }
    }
}
