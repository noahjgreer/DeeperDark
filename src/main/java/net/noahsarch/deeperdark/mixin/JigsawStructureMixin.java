package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {
    @ModifyConstant(method = "lambda$static$0", constant = @Constant(intValue = 20), remap = false)
    private static int modifySizeLimit(int constant) {
        return 128;
    }
}

