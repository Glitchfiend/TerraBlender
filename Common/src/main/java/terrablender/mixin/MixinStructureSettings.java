/**
 * Copyright (C) Glitchfiend
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.mixin;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import terrablender.core.TerraBlender;
import terrablender.data.TBCodec;

import java.util.Optional;
import java.util.function.Function;

@Mixin(StructureSettings.class)
public class MixinStructureSettings
{
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"), index = 0)
    private static Function<RecordCodecBuilder.Instance<StructureSettings>, ? extends App<RecordCodecBuilder.Mu<StructureSettings>, StructureSettings>> modifyCodec(Function<RecordCodecBuilder.Instance<StructureSettings>, ? extends App<RecordCodecBuilder.Mu<StructureSettings>, StructureSettings>> current)
    {
        return (builder) -> {
            return builder.group(StrongholdConfiguration.CODEC.optionalFieldOf("stronghold").forGetter((settings) -> {
                return Optional.ofNullable(settings.stronghold);
            }), TBCodec.lenientSimpleMap(Registry.STRUCTURE_FEATURE.byNameCodec(), StructureFeatureConfiguration.CODEC, Registry.STRUCTURE_FEATURE).fieldOf("structures").forGetter((structureSettings) -> {
                return structureSettings.structureConfig;
            })).apply(builder, StructureSettings::new);
        };
    }
}
