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

class EndCityGenerator.1
implements EndCityGenerator.Part {
    EndCityGenerator.1() {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
        if (depth > 8) {
            return false;
        }
        BlockRotation blockRotation = root.getPlacementData().getRotation();
        EndCityGenerator.Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, pos, "base_floor", blockRotation, true));
        int i = random.nextInt(3);
        if (i == 0) {
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "base_roof", blockRotation, true));
        } else if (i == 1) {
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 8, -1), "second_roof", blockRotation, false));
            EndCityGenerator.createPart(manager, SMALL_TOWER, depth + 1, piece, null, pieces, random);
        } else if (i == 2) {
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "third_floor_2", blockRotation, false));
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
            EndCityGenerator.createPart(manager, SMALL_TOWER, depth + 1, piece, null, pieces, random);
        }
        return true;
    }
}
