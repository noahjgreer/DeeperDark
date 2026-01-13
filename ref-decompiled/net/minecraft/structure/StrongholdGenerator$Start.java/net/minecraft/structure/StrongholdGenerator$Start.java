/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public static class StrongholdGenerator.Start
extends StrongholdGenerator.SpiralStaircase {
    public  @Nullable StrongholdGenerator.PieceData lastPiece;
    public  @Nullable StrongholdGenerator.PortalRoom portalRoom;
    public final List<StructurePiece> pieces = Lists.newArrayList();

    public StrongholdGenerator.Start(Random random, int i, int j) {
        super(StructurePieceType.STRONGHOLD_START, 0, i, j, StrongholdGenerator.Start.getRandomHorizontalDirection(random));
    }

    public StrongholdGenerator.Start(NbtCompound nbtCompound) {
        super(StructurePieceType.STRONGHOLD_START, nbtCompound);
    }

    @Override
    public BlockPos getCenter() {
        if (this.portalRoom != null) {
            return this.portalRoom.getCenter();
        }
        return super.getCenter();
    }
}
