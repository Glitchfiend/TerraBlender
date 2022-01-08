/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import terrablender.worldgen.TBClimate;
import terrablender.worldgen.TBNoiseBasedChunkGenerator;

public class CommandBiomeParameters
{
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.terrablender.biomeparams.failed"));

    static ArgumentBuilder<CommandSourceStack, ?> register()
    {
        return Commands.literal("biomeparams")
            .executes(ctx -> biomeParams(ctx.getSource()));
    }

    private static int biomeParams(CommandSourceStack cs) throws CommandSyntaxException
    {
        BlockPos pos = new BlockPos(cs.getPosition());
        ServerLevel level = cs.getLevel();
        ChunkGenerator generator = level.getChunkSource().getGenerator();

        if (!(generator instanceof TBNoiseBasedChunkGenerator))
            throw ERROR_FAILED.create();

        BiomeManager biomeManager = level.getBiomeManager();
        TBNoiseBasedChunkGenerator noiseBasedChunkGenerator = (TBNoiseBasedChunkGenerator)generator;
        TBClimate.Sampler sampler = (TBClimate.Sampler)noiseBasedChunkGenerator.climateSampler();
        BlockPos adjustedPos = getAdjustedPos(pos, biomeManager.biomeZoomSeed);
        TBClimate.TargetPoint target = sampler.sampleTB(adjustedPos.getX(), adjustedPos.getY(), adjustedPos.getZ());
        cs.sendSuccess(new TranslatableComponent("commands.terrablender.biomeparams.success", pos.getX(), pos.getY(), pos.getZ(), target.toString().replace("TargetPoint[", "").replace("]", " ")), true);
        return 1;
    }

    private static BlockPos getAdjustedPos(BlockPos pos, long seed)
    {
        int i = pos.getX() - 2;
        int j = pos.getY() - 2;
        int k = pos.getZ() - 2;
        int l = i >> 2;
        int i1 = j >> 2;
        int j1 = k >> 2;
        double d0 = (double)(i & 3) / 4.0D;
        double d1 = (double)(j & 3) / 4.0D;
        double d2 = (double)(k & 3) / 4.0D;
        int k1 = 0;
        double d3 = Double.POSITIVE_INFINITY;

        for(int l1 = 0; l1 < 8; ++l1) {
            boolean flag = (l1 & 4) == 0;
            boolean flag1 = (l1 & 2) == 0;
            boolean flag2 = (l1 & 1) == 0;
            int i2 = flag ? l : l + 1;
            int j2 = flag1 ? i1 : i1 + 1;
            int k2 = flag2 ? j1 : j1 + 1;
            double d4 = flag ? d0 : d0 - 1.0D;
            double d5 = flag1 ? d1 : d1 - 1.0D;
            double d6 = flag2 ? d2 : d2 - 1.0D;
            double d7 = BiomeManager.getFiddledDistance(seed, i2, j2, k2, d4, d5, d6);
            if (d3 > d7) {
                k1 = l1;
                d3 = d7;
            }
        }

        int l2 = (k1 & 4) == 0 ? l : l + 1;
        int i3 = (k1 & 2) == 0 ? i1 : i1 + 1;
        int j3 = (k1 & 1) == 0 ? j1 : j1 + 1;
        return new BlockPos(l2, i3, j3);
    }
}
