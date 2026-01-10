package net.noahsarch.deeperdark.mixin;

import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(StructurePiecesCollector.class)
public interface StructurePiecesCollectorAccessor {
    @Accessor("pieces")
    List<StructurePiece> getPieces();
}

