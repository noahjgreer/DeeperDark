package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;
import net.noahsarch.deeperdark.worldgen.PaleMansionProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;

@Mixin(WoodlandMansionStructure.class)
public class WoodlandMansionStructureMixin {

    @Inject(method = "addPieces", at = @At("TAIL"))
    private void deeperdark$replaceWithPaleOak(StructurePiecesBuilder collector, GenerationContext context, BlockPos pos, Rotation rotation, CallbackInfo ci) {
        List<StructurePiece> pieces = ((StructurePiecesCollectorAccessor) collector).getPieces();
        boolean isPaleMansion = false;

        for (StructurePiece piece : pieces) {
            BlockPos center = piece.getBoundingBox().getCenter();
            Holder<Biome> biomeEntry = context.chunkGenerator().getBiomeSource().getBiome(
                    QuartPos.fromBlock(center.getX()),
                    QuartPos.fromBlock(center.getY()),
                    QuartPos.fromBlock(center.getZ()),
                    context.randomState().getMultiNoiseSampler()
            );
            if (biomeEntry.matchesKey(Biomes.PALE_GARDEN)) {
                isPaleMansion = true;
                break;
            }
        }

        if (isPaleMansion) {
            for (StructurePiece piece : pieces) {
                if (piece instanceof TemplateStructurePiece simplePiece) {
                    ((SimpleStructurePieceAccessor) simplePiece).deeperdark$getPlacementData().addProcessor(PaleMansionProcessor.INSTANCE);

                }
            }
        }
    }
}

