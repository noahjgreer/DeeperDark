package net.noahsarch.deeperdark.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SoftGroundSpeedMixin {

    /**
     * Applies a 0.4× speed factor (identical to soul sand) when a non-leather-boot wearer
     * walks on quicksand or powder snow. Leather boots grant full speed; no boots mean the
     * entity sinks and is slowed by makeStuckInBlock instead.
     *
     * Mirrors the soul sand mechanism exactly: soul sand has speedFactor(0.4F) on the block
     * and Entity.move() multiplies delta movement by getBlockSpeedFactor() after each step.
     */
    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    private void deeperdark$softGroundSpeedFactor(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Only applies to entities wearing non-leather boots.
        var boots = self.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty() || boots.is(Items.LEATHER_BOOTS)) return;

        // Check the block this entity is standing on, using the same position vanilla uses
        // for block speed/jump factors.
        BlockState below = self.level().getBlockState(self.getBlockPosBelowThatAffectsMyMovement());
        if (!below.is(ModBlocks.QUICKSAND) && !below.is(Blocks.POWDER_SNOW)) return;

        // Apply MOVEMENT_EFFICIENCY lerp exactly as LivingEntity.getBlockSpeedFactor() does.
        float efficiency = (float) self.getAttributeValue(Attributes.MOVEMENT_EFFICIENCY);
        cir.setReturnValue(Mth.lerp(efficiency, 0.4F, 1.0F));
    }
}
