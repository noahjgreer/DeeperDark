/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.util.List;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

class EndCityGenerator.3
implements EndCityGenerator.Part {
    public boolean shipGenerated;

    EndCityGenerator.3() {
    }

    @Override
    public void init() {
        this.shipGenerated = false;
    }

    @Override
    public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
        BlockRotation blockRotation = root.getPlacementData().getRotation();
        int i = random.nextInt(4) + 1;
        EndCityGenerator.Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(0, 0, -4), "bridge_piece", blockRotation, true));
        piece.setChainLength(-1);
        int j = 0;
        for (int k = 0; k < i; ++k) {
            if (random.nextBoolean()) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -4), "bridge_piece", blockRotation, true));
                j = 0;
                continue;
            }
            piece = random.nextBoolean() ? EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -4), "bridge_steep_stairs", blockRotation, true)) : EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -8), "bridge_gentle_stairs", blockRotation, true));
            j = 4;
        }
        if (this.shipGenerated || random.nextInt(10 - depth) != 0) {
            if (!EndCityGenerator.createPart(manager, BUILDING, depth + 1, piece, new BlockPos(-3, j + 1, -11), pieces, random)) {
                return false;
            }
        } else {
            EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-8 + random.nextInt(8), j, -70 + random.nextInt(10)), "ship", blockRotation, true));
            this.shipGenerated = true;
        }
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(4, j, 0), "bridge_end", blockRotation.rotate(BlockRotation.CLOCKWISE_180), true));
        piece.setChainLength(-1);
        return true;
    }
}
