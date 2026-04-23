package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TemplateStructurePiece.class)
public interface SimpleStructurePieceAccessor {
    @Accessor("placeSettings")
    StructurePlaceSettings deeperdark$getPlacementData();
}

