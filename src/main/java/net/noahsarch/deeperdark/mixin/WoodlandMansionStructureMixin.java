package net.noahsarch.deeperdark.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.WoodlandMansionStructure;
import net.noahsarch.deeperdark.worldgen.PaleMansionProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WoodlandMansionStructure.class)
public class WoodlandMansionStructureMixin {

    @Inject(method = "addPieces", at = @At("TAIL"))
    private void deeperdark$replaceWithPaleOak(StructurePiecesCollector collector, Structure.Context context, BlockPos pos, BlockRotation rotation, CallbackInfo ci) {
        List<StructurePiece> pieces = ((StructurePiecesCollectorAccessor) collector).getPieces();
        boolean isPaleMansion = false;

        for (StructurePiece piece : pieces) {
            BlockPos center = piece.getBoundingBox().getCenter();
            RegistryEntry<Biome> biomeEntry = context.chunkGenerator().getBiomeSource().getBiome(
                    BiomeCoords.fromBlock(center.getX()),
                    BiomeCoords.fromBlock(center.getY()),
                    BiomeCoords.fromBlock(center.getZ()),
                    context.noiseConfig().getMultiNoiseSampler()
            );
            if (biomeEntry.matchesKey(BiomeKeys.PALE_GARDEN)) {
                isPaleMansion = true;
                break;
            }
        }

        if (isPaleMansion) {
            for (StructurePiece piece : pieces) {
                if (piece instanceof SimpleStructurePiece simplePiece) {
                    ((SimpleStructurePieceAccessor) simplePiece).deeperdark$getPlacementData().addProcessor(PaleMansionProcessor.INSTANCE);

                }
            }
        }
    }
}

