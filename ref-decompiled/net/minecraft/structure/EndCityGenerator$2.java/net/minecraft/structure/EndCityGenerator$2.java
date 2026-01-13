/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.util.List;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

class EndCityGenerator.2
implements EndCityGenerator.Part {
    EndCityGenerator.2() {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
        BlockRotation blockRotation = root.getPlacementData().getRotation();
        EndCityGenerator.Piece piece = root;
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation, true));
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 7, 0), "tower_piece", blockRotation, true));
        EndCityGenerator.Piece piece2 = random.nextInt(3) == 0 ? piece : null;
        int i = 1 + random.nextInt(3);
        for (int j = 0; j < i; ++j) {
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 4, 0), "tower_piece", blockRotation, true));
            if (j >= i - 1 || !random.nextBoolean()) continue;
            piece2 = piece;
        }
        if (piece2 != null) {
            for (Pair<BlockRotation, BlockPos> pair : SMALL_TOWER_BRIDGE_ATTACHMENTS) {
                if (!random.nextBoolean()) continue;
                EndCityGenerator.Piece piece3 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece2, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true));
                EndCityGenerator.createPart(manager, BRIDGE_PIECE, depth + 1, piece3, null, pieces, random);
            }
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
        } else if (depth == 7) {
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
        } else {
            return EndCityGenerator.createPart(manager, FAT_TOWER, depth + 1, piece, null, pieces, random);
        }
        return true;
    }
}
