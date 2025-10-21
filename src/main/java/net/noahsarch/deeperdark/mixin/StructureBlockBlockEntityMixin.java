package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockBlockEntity.class)
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

    @Inject(method = "readData", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;updateBlockMode()V"))
    private void onReadData(ReadView view, CallbackInfo ci) {
        // Override the offset with larger limits
        int i = MathHelper.clamp(view.getInt("posX", DEFAULT_OFFSET.getX()), -MAX_OFFSET, MAX_OFFSET);
        int j = MathHelper.clamp(view.getInt("posY", DEFAULT_OFFSET.getY()), -MAX_OFFSET, MAX_OFFSET);
        int k = MathHelper.clamp(view.getInt("posZ", DEFAULT_OFFSET.getZ()), -MAX_OFFSET, MAX_OFFSET);
        this.offset = new BlockPos(i, j, k);

        // Override the size with larger limits
        int l = MathHelper.clamp(view.getInt("sizeX", DEFAULT_SIZE.getX()), 0, MAX_STRUCTURE_SIZE);
        int m = MathHelper.clamp(view.getInt("sizeY", DEFAULT_SIZE.getY()), 0, MAX_STRUCTURE_SIZE);
        int n = MathHelper.clamp(view.getInt("sizeZ", DEFAULT_SIZE.getZ()), 0, MAX_STRUCTURE_SIZE);
        this.size = new Vec3i(l, m, n);
    }
}
