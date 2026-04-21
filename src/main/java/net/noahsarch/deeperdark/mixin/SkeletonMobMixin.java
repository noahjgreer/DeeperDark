package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class SkeletonMobMixin implements BabySkeletonAccessor, BabyCreeperAccessor {

    @Unique
    private static final Identifier BABY_SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath("deeperdark", "baby_skeleton_speed");
    @Unique
    private static final AttributeModifier BABY_SPEED_MODIFIER = new AttributeModifier(
        BABY_SPEED_MODIFIER_ID, 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
    );

    @Unique
    private static final Identifier BABY_CREEPER_SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath("deeperdark", "baby_creeper_speed");
    @Unique
    private static final AttributeModifier BABY_CREEPER_SPEED_MODIFIER = new AttributeModifier(
        BABY_CREEPER_SPEED_MODIFIER_ID, 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
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
    private void deeperdark$initialize(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob self = (Mob)(Object)this;

        // Handle baby skeletons
        if (self instanceof Skeleton) {
            // Check if baby skeletons are enabled in config
            if (!DeeperDarkConfig.get().babySkeletonsEnabled) return;

            // 5% chance to be a baby
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabyInternal(true);
            }
        }

        // Handle baby creepers
        if (self instanceof Creeper) {
            // Check if baby creepers are enabled in config
            if (!DeeperDarkConfig.get().babyCreepersEnabled) return;

            // 5% chance to be a baby
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabyCreeperInternal(true);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(ValueOutput view, CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        if (self instanceof Skeleton) {
            view.putBoolean("DeeperDarkIsBaby", this.deeperdark$isBaby);
        }
        if (self instanceof Creeper) {
            view.putBoolean("DeeperDarkIsBabyCreeper", this.deeperdark$isBabyCreeper);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ValueInput view, CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        if (self instanceof Skeleton) {
            this.deeperdark$setBabyInternal(view.getBoolean("DeeperDarkIsBaby", false));
        }
        if (self instanceof Creeper) {
            this.deeperdark$setBabyCreeperInternal(view.getBoolean("DeeperDarkIsBabyCreeper", false));
        }
    }

    @Unique
    private void deeperdark$setBabyInternal(boolean isBaby) {
        this.deeperdark$isBaby = isBaby;
        Mob self = (Mob) (Object) this;

        var scaleAttr = self.getAttributeInstance(Attributes.SCALE);
        var speedAttr = self.getAttributeInstance(Attributes.MOVEMENT_SPEED);

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
        Mob self = (Mob) (Object) this;

        var scaleAttr = self.getAttributeInstance(Attributes.SCALE);
        var speedAttr = self.getAttributeInstance(Attributes.MOVEMENT_SPEED);

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
