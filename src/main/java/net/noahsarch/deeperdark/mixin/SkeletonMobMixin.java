package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class SkeletonMobMixin implements BabySkeletonAccessor, BabyCreeperAccessor {

    @Unique
    private static final Identifier BABY_SPEED_MODIFIER_ID = Identifier.of("deeperdark", "baby_skeleton_speed");
    @Unique
    private static final EntityAttributeModifier BABY_SPEED_MODIFIER = new EntityAttributeModifier(
        BABY_SPEED_MODIFIER_ID, 0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
    );

    @Unique
    private static final Identifier BABY_CREEPER_SPEED_MODIFIER_ID = Identifier.of("deeperdark", "baby_creeper_speed");
    @Unique
    private static final EntityAttributeModifier BABY_CREEPER_SPEED_MODIFIER = new EntityAttributeModifier(
        BABY_CREEPER_SPEED_MODIFIER_ID, 0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
    );

    @Unique
    private boolean deeperdark$isBaby = false;

    @Unique
    private boolean deeperdark$isBabyCreeper = false;

    @Override
    public boolean deeperdark$isBaby() {
        return this.deeperdark$isBaby;
    }

    @Override
    public void deeperdark$setBaby(boolean isBaby) {
        this.deeperdark$setBabyInternal(isBaby);
    }

    @Override
    public boolean deeperdark$isBabyCreeper() {
        return this.deeperdark$isBabyCreeper;
    }

    @Override
    public void deeperdark$setBabyCreeper(boolean isBaby) {
        this.deeperdark$setBabyCreeperInternal(isBaby);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void deeperdark$initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        MobEntity self = (MobEntity)(Object)this;

        // Handle baby skeletons
        if (self instanceof SkeletonEntity) {
            // Check if baby skeletons are enabled in config
            if (!DeeperDarkConfig.get().babySkeletonsEnabled) return;

            // 5% chance to be a baby
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabyInternal(true);
            }
        }

        // Handle baby creepers
        if (self instanceof CreeperEntity) {
            // Check if baby creepers are enabled in config
            if (!DeeperDarkConfig.get().babyCreepersEnabled) return;

            // 5% chance to be a baby
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabyCreeperInternal(true);
            }
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(WriteView view, CallbackInfo ci) {
        MobEntity self = (MobEntity)(Object)this;
        if (self instanceof SkeletonEntity) {
            view.putBoolean("DeeperDarkIsBaby", this.deeperdark$isBaby);
        }
        if (self instanceof CreeperEntity) {
            view.putBoolean("DeeperDarkIsBabyCreeper", this.deeperdark$isBabyCreeper);
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ReadView view, CallbackInfo ci) {
        MobEntity self = (MobEntity)(Object)this;
        if (self instanceof SkeletonEntity) {
            this.deeperdark$setBabyInternal(view.getBoolean("DeeperDarkIsBaby", false));
        }
        if (self instanceof CreeperEntity) {
            this.deeperdark$setBabyCreeperInternal(view.getBoolean("DeeperDarkIsBabyCreeper", false));
        }
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

    @Unique
    private void deeperdark$setBabyCreeperInternal(boolean isBaby) {
        this.deeperdark$isBabyCreeper = isBaby;
        MobEntity self = (MobEntity) (Object) this;

        var scaleAttr = self.getAttributeInstance(EntityAttributes.SCALE);
        var speedAttr = self.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);

        if (isBaby) {
            // Set scale to half
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(0.5);
            }

            // Add speed modifier if not present
            if (speedAttr != null && !speedAttr.hasModifier(BABY_CREEPER_SPEED_MODIFIER_ID)) {
                speedAttr.addPersistentModifier(BABY_CREEPER_SPEED_MODIFIER);
            }
        } else {
            // Reset scale
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(1.0);
            }

            // Remove speed modifier
            if (speedAttr != null) {
                speedAttr.removeModifier(BABY_CREEPER_SPEED_MODIFIER_ID);
            }
        }
    }
}
