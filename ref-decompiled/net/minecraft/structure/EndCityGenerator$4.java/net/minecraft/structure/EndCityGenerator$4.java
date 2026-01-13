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

class EndCityGenerator.4
implements EndCityGenerator.Part {
    EndCityGenerator.4() {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
        BlockRotation blockRotation = root.getPlacementData().getRotation();
        EndCityGenerator.Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true));
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true));
        for (int i = 0; i < 2 && random.nextInt(3) != 0; ++i) {
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true));
            for (Pair<BlockRotation, BlockPos> pair : FAT_TOWER_BRIDGE_ATTACHMENTS) {
                if (!random.nextBoolean()) continue;
                EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true));
                EndCityGenerator.createPart(manager, BRIDGE_PIECE, depth + 1, piece2, null, pieces, random);
            }
        }
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true));
        return true;
    }
}
