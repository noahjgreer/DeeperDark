/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public static class NetherFortressGenerator.Start
extends NetherFortressGenerator.BridgeCrossing {
     @Nullable NetherFortressGenerator.PieceData lastPiece;
    final List<NetherFortressGenerator.PieceData> bridgePieces = new ArrayList<NetherFortressGenerator.PieceData>();
    final List<NetherFortressGenerator.PieceData> corridorPieces = new ArrayList<NetherFortressGenerator.PieceData>();
    public final List<StructurePiece> pieces = Lists.newArrayList();

    public NetherFortressGenerator.Start(Random random, int x, int z) {
        super(x, z, NetherFortressGenerator.Start.getRandomHorizontalDirection(random));
        for (NetherFortressGenerator.PieceData pieceData : ALL_BRIDGE_PIECES) {
            pieceData.generatedCount = 0;
            this.bridgePieces.add(pieceData);
        }
        for (NetherFortressGenerator.PieceData pieceData : ALL_CORRIDOR_PIECES) {
            pieceData.generatedCount = 0;
            this.corridorPieces.add(pieceData);
        }
    }

    public NetherFortressGenerator.Start(NbtCompound nbtCompound) {
        super(StructurePieceType.NETHER_FORTRESS_START, nbtCompound);
    }
}
