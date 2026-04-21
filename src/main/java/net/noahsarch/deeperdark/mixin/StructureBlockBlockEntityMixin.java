package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockEntity.class)
public abstract class StructureBlockBlockEntityMixin {

    @Shadow private BlockPos offset;
    @Shadow private Vec3i size;

    @Unique
    private static final BlockPos DEFAULT_OFFSET = new BlockPos(0, 1, 0);
    @Unique
    private static final Vec3i DEFAULT_SIZE = Vec3i.ZERO;

    // Increased limits - you can adjust these as needed
    @Unique
    private static final int MAX_STRUCTURE_SIZE = 512;
    @Unique
    private static final int MAX_OFFSET = 512;

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void onReadData(ValueInput view, CallbackInfo ci) {
        // Override the offset with larger limits
        int i = Mth.clamp(view.getInt("posX", DEFAULT_OFFSET.getX()), -MAX_OFFSET, MAX_OFFSET);
        int j = Mth.clamp(view.getInt("posY", DEFAULT_OFFSET.getY()), -MAX_OFFSET, MAX_OFFSET);
        int k = Mth.clamp(view.getInt("posZ", DEFAULT_OFFSET.getZ()), -MAX_OFFSET, MAX_OFFSET);
        this.offset = new BlockPos(i, j, k);

        // Override the size with larger limits
        int l = Mth.clamp(view.getInt("sizeX", DEFAULT_SIZE.getX()), 0, MAX_STRUCTURE_SIZE);
        int m = Mth.clamp(view.getInt("sizeY", DEFAULT_SIZE.getY()), 0, MAX_STRUCTURE_SIZE);
        int n = Mth.clamp(view.getInt("sizeZ", DEFAULT_SIZE.getZ()), 0, MAX_STRUCTURE_SIZE);
        this.size = new Vec3i(l, m, n);
    }
}
