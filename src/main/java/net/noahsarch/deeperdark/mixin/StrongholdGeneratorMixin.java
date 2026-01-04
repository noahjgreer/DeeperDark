package net.noahsarch.deeperdark.mixin;

import net.minecraft.structure.StrongholdGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StrongholdGenerator.class)
public class StrongholdGeneratorMixin {
    @ModifyConstant(method = "pieceGenerator", constant = @Constant(intValue = 50))
    private static int modifyDepthLimit(int constant) {
        return 750;
    }

    @ModifyConstant(method = "pieceGenerator", constant = @Constant(intValue = 112))
    private static int modifyRadiusLimit(int constant) {
        return 1680;
    }
}

