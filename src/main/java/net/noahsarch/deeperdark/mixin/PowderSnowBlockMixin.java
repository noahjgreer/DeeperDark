package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    /**
     * Allow ANY boots (not just leather) to walk on powder snow.
     * The speed debuff for non-leather boots is applied separately via BootsOnSoftGroundHandler.
     */
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void deeperdark$anyBootsWalkOnSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity le && !le.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Return a solid collision shape for any boot-wearing entity above powder snow,
     * bypassing the vanilla `!context.isDescending()` guard that would make them sink
     * while walking downhill.
     */
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void deeperdark$anyBootsCollisionShape(
            BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!context.isPlacement() && context instanceof EntityCollisionContext entityCtx) {
            Entity entity = entityCtx.getEntity();
            if (entity != null
                    && PowderSnowBlock.canEntityWalkOnPowderSnow(entity)
                    && context.isAbove(Shapes.block(), pos, false)) {
                cir.setReturnValue(Shapes.block());
            }
        }
    }
}
