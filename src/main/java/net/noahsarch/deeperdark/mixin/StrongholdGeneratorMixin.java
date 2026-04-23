package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StrongholdPieces.class)
public class StrongholdGeneratorMixin {
    @ModifyConstant(method = "generateAndAddPiece", constant = @Constant(intValue = 50))
    private static int modifyDepthLimit(int constant) {
        return 750;
    }

    @ModifyConstant(method = "generateAndAddPiece", constant = @Constant(intValue = 112))
    private static int modifyRadiusLimit(int constant) {
        return 1680;
    }
}

