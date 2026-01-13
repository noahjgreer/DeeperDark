/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

static abstract class NetherFortressGenerator.Piece
extends StructurePiece {
    protected NetherFortressGenerator.Piece(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
        super(structurePieceType, i, blockBox);
    }

    public NetherFortressGenerator.Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
    }

    private int checkRemainingPieces(List<NetherFortressGenerator.PieceData> possiblePieces) {
        boolean bl = false;
        int i = 0;
        for (NetherFortressGenerator.PieceData pieceData : possiblePieces) {
            if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
                bl = true;
            }
            i += pieceData.weight;
        }
        return bl ? i : -1;
    }

    private @Nullable NetherFortressGenerator.Piece pickPiece(NetherFortressGenerator.Start start, List<NetherFortressGenerator.PieceData> possiblePieces, StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        int i = this.checkRemainingPieces(possiblePieces);
        boolean bl = i > 0 && chainLength <= 30;
        int j = 0;
        block0: while (j < 5 && bl) {
            ++j;
            int k = random.nextInt(i);
            for (NetherFortressGenerator.PieceData pieceData : possiblePieces) {
                if ((k -= pieceData.weight) >= 0) continue;
                if (!pieceData.canGenerate(chainLength) || pieceData == start.lastPiece && !pieceData.repeatable) continue block0;
                NetherFortressGenerator.Piece piece = NetherFortressGenerator.createPiece(pieceData, holder, random, x, y, z, orientation, chainLength);
                if (piece == null) continue;
                ++pieceData.generatedCount;
                start.lastPiece = pieceData;
                if (!pieceData.canGenerate()) {
                    possiblePieces.remove(pieceData);
                }
                return piece;
            }
        }
        return NetherFortressGenerator.BridgeEnd.create(holder, random, x, y, z, orientation, chainLength);
    }

    private @Nullable StructurePiece pieceGenerator(NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength, boolean inside) {
        NetherFortressGenerator.Piece structurePiece;
        if (Math.abs(x - start.getBoundingBox().getMinX()) > 112 || Math.abs(z - start.getBoundingBox().getMinZ()) > 112) {
            return NetherFortressGenerator.BridgeEnd.create(holder, random, x, y, z, orientation, chainLength);
        }
        List<NetherFortressGenerator.PieceData> list = start.bridgePieces;
        if (inside) {
            list = start.corridorPieces;
        }
        if ((structurePiece = this.pickPiece(start, list, holder, random, x, y, z, orientation, chainLength + 1)) != null) {
            holder.addPiece(structurePiece);
            start.pieces.add(structurePiece);
        }
        return structurePiece;
    }

    protected @Nullable StructurePiece fillForwardOpening(NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int leftRightOffset, int heightOffset, boolean inside) {
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                case NORTH: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() - 1, direction, this.getChainLength(), inside);
                }
                case SOUTH: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMaxZ() + 1, direction, this.getChainLength(), inside);
                }
                case WEST: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, direction, this.getChainLength(), inside);
                }
                case EAST: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, direction, this.getChainLength(), inside);
                }
            }
        }
        return null;
    }

    protected @Nullable StructurePiece fillNWOpening(NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset, boolean inside) {
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                case NORTH: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.WEST, this.getChainLength(), inside);
                }
                case SOUTH: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.WEST, this.getChainLength(), inside);
                }
                case WEST: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() - 1, Direction.NORTH, this.getChainLength(), inside);
                }
                case EAST: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() - 1, Direction.NORTH, this.getChainLength(), inside);
                }
            }
        }
        return null;
    }

    protected @Nullable StructurePiece fillSEOpening(NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset, boolean inside) {
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                case NORTH: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.EAST, this.getChainLength(), inside);
                }
                case SOUTH: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.EAST, this.getChainLength(), inside);
                }
                case WEST: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, this.getChainLength(), inside);
                }
                case EAST: {
                    return this.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, this.getChainLength(), inside);
                }
            }
        }
        return null;
    }

    protected static boolean isInBounds(BlockBox boundingBox) {
        return boundingBox.getMinY() > 10;
    }
}
