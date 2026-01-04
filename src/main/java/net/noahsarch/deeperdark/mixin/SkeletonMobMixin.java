package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class SkeletonMobMixin implements BabySkeletonAccessor {

    @Unique
    private static final Identifier BABY_SPEED_MODIFIER_ID = Identifier.of("deeperdark", "baby_skeleton_speed");
    @Unique
    private static final EntityAttributeModifier BABY_SPEED_MODIFIER = new EntityAttributeModifier(
        BABY_SPEED_MODIFIER_ID, 0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
    );

    @Unique
    private boolean deeperdark$isBaby = false;

    @Override
    public boolean deeperdark$isBaby() {
        return this.deeperdark$isBaby;
    }

    @Override
    public void deeperdark$setBaby(boolean isBaby) {
        this.deeperdark$setBabyInternal(isBaby);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void deeperdark$initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        MobEntity self = (MobEntity)(Object)this;
        if (!(self instanceof SkeletonEntity)) return;

        // 5% chance to be a baby
        if (world.getRandom().nextFloat() < 0.05f) {
            this.deeperdark$setBabyInternal(true);
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(WriteView view, CallbackInfo ci) {
        MobEntity self = (MobEntity)(Object)this;
        if (!(self instanceof SkeletonEntity)) return;
        view.putBoolean("DeeperDarkIsBaby", this.deeperdark$isBaby);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ReadView view, CallbackInfo ci) {
        MobEntity self = (MobEntity)(Object)this;
        if (!(self instanceof SkeletonEntity)) return;
        this.deeperdark$setBabyInternal(view.getBoolean("DeeperDarkIsBaby", false));
    }

    @Unique
    private void deeperdark$setBabyInternal(boolean isBaby) {
        this.deeperdark$isBaby = isBaby;
        MobEntity self = (MobEntity) (Object) this;

        var scaleAttr = self.getAttributeInstance(EntityAttributes.SCALE);
        var speedAttr = self.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);

        if (isBaby) {
            // Set scale to half
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(0.5);
            }

            // Add speed modifier if not present
            if (speedAttr != null && !speedAttr.hasModifier(BABY_SPEED_MODIFIER_ID)) {
                speedAttr.addPersistentModifier(BABY_SPEED_MODIFIER);
            }
        } else {
            // Reset scale
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(1.0);
            }

            // Remove speed modifier
            if (speedAttr != null) {
                speedAttr.removeModifier(BABY_SPEED_MODIFIER_ID);
            }
        }
    }
}
