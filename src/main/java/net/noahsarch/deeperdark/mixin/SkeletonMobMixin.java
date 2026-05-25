package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.goal.BabySpiderLeapGoal;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import net.noahsarch.deeperdark.util.BabySpiderAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class SkeletonMobMixin implements BabySkeletonAccessor, BabyCreeperAccessor, BabySpiderAccessor {

    @Unique private static final Identifier BABY_SPEED_MODIFIER_ID =
        Identifier.fromNamespaceAndPath("deeperdark", "baby_skeleton_speed");
    @Unique private static final AttributeModifier BABY_SPEED_MODIFIER =
        new AttributeModifier(BABY_SPEED_MODIFIER_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    @Unique private static final Identifier BABY_CREEPER_SPEED_MODIFIER_ID =
        Identifier.fromNamespaceAndPath("deeperdark", "baby_creeper_speed");
    @Unique private static final AttributeModifier BABY_CREEPER_SPEED_MODIFIER =
        new AttributeModifier(BABY_CREEPER_SPEED_MODIFIER_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    @Unique private static final Identifier BABY_SPIDER_SPEED_MODIFIER_ID =
        Identifier.fromNamespaceAndPath("deeperdark", "baby_spider_speed");
    @Unique private static final AttributeModifier BABY_SPIDER_SPEED_MODIFIER =
        new AttributeModifier(BABY_SPIDER_SPEED_MODIFIER_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    @Unique private boolean deeperdark$isBaby = false;
    @Unique private boolean deeperdark$isBabyCreeper = false;
    @Unique private boolean deeperdark$isBabySpider = false;

    @Override public boolean deeperdark$isBaby() { return this.deeperdark$isBaby; }
    @Override public void deeperdark$setBaby(boolean isBaby) { this.deeperdark$setBabyInternal(isBaby); }

    @Override public boolean deeperdark$isBabyCreeper() { return this.deeperdark$isBabyCreeper; }
    @Override public void deeperdark$setBabyCreeper(boolean isBaby) { this.deeperdark$setBabyCreeperInternal(isBaby); }

    @Override public boolean deeperdark$isBabySpider() { return this.deeperdark$isBabySpider; }
    @Override public void deeperdark$setBabySpider(boolean isBaby) { this.deeperdark$setBabySpiderInternal(isBaby); }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void deeperdark$initialize(ServerLevelAccessor world, DifficultyInstance difficulty,
            EntitySpawnReason spawnReason, SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob self = (Mob)(Object)this;

        if (self instanceof Skeleton && DeeperDarkConfig.get().babySkeletonsEnabled) {
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabyInternal(true);
            }
        }

        if (self instanceof Creeper && DeeperDarkConfig.get().babyCreepersEnabled) {
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabyCreeperInternal(true);
            }
        }

        if (self instanceof Spider && DeeperDarkConfig.get().babySpidersEnabled) {
            if (world.getRandom().nextFloat() < 0.05f) {
                this.deeperdark$setBabySpiderInternal(true);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(ValueOutput view, CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        if (self instanceof Skeleton) {
            view.putBoolean("IsBaby", this.deeperdark$isBaby);
        }
        if (self instanceof Creeper) {
            view.putBoolean("IsBaby", this.deeperdark$isBabyCreeper);
        }
        if (self instanceof Spider) {
            view.putBoolean("IsBaby", this.deeperdark$isBabySpider);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ValueInput view, CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        if (self instanceof Skeleton) {
            // Fall back to old key for worlds saved before the IsBaby migration
            boolean val = view.getBooleanOr("IsBaby", view.getBooleanOr("DeeperDarkIsBaby", false));
            this.deeperdark$setBabyInternal(val);
        }
        if (self instanceof Creeper) {
            boolean val = view.getBooleanOr("IsBaby", view.getBooleanOr("DeeperDarkIsBabyCreeper", false));
            this.deeperdark$setBabyCreeperInternal(val);
        }
        if (self instanceof Spider) {
            this.deeperdark$setBabySpiderInternal(view.getBooleanOr("IsBaby", false));
        }
    }

    @Unique
    private void deeperdark$setBabyInternal(boolean isBaby) {
        this.deeperdark$isBaby = isBaby;
        Mob self = (Mob)(Object)this;
        var scaleAttr = self.getAttribute(Attributes.SCALE);
        var speedAttr = self.getAttribute(Attributes.MOVEMENT_SPEED);
        if (isBaby) {
            if (scaleAttr != null) scaleAttr.setBaseValue(0.5);
            if (speedAttr != null && !speedAttr.hasModifier(BABY_SPEED_MODIFIER_ID)) {
                speedAttr.addPermanentModifier(BABY_SPEED_MODIFIER);
            }
        } else {
            if (scaleAttr != null) scaleAttr.setBaseValue(1.0);
            if (speedAttr != null) speedAttr.removeModifier(BABY_SPEED_MODIFIER_ID);
        }
    }

    @Unique
    private void deeperdark$setBabyCreeperInternal(boolean isBaby) {
        this.deeperdark$isBabyCreeper = isBaby;
        Mob self = (Mob)(Object)this;
        var scaleAttr = self.getAttribute(Attributes.SCALE);
        var speedAttr = self.getAttribute(Attributes.MOVEMENT_SPEED);
        if (isBaby) {
            if (scaleAttr != null) scaleAttr.setBaseValue(0.5);
            if (speedAttr != null && !speedAttr.hasModifier(BABY_CREEPER_SPEED_MODIFIER_ID)) {
                speedAttr.addPermanentModifier(BABY_CREEPER_SPEED_MODIFIER);
            }
        } else {
            if (scaleAttr != null) scaleAttr.setBaseValue(1.0);
            if (speedAttr != null) speedAttr.removeModifier(BABY_CREEPER_SPEED_MODIFIER_ID);
        }
    }

    @Unique
    private void deeperdark$setBabySpiderInternal(boolean isBaby) {
        this.deeperdark$isBabySpider = isBaby;
        Mob self = (Mob)(Object)this;
        var scaleAttr = self.getAttribute(Attributes.SCALE);
        var speedAttr = self.getAttribute(Attributes.MOVEMENT_SPEED);
        var goalSelector = ((MobEntityAccessor) self).getGoalSelector();
        if (isBaby) {
            if (scaleAttr != null) scaleAttr.setBaseValue(0.5);
            if (speedAttr != null && !speedAttr.hasModifier(BABY_SPIDER_SPEED_MODIFIER_ID)) {
                speedAttr.addPermanentModifier(BABY_SPIDER_SPEED_MODIFIER);
            }
            // Swap vanilla leap goal for the baby version that fires twice as often
            goalSelector.removeAllGoals(g -> g instanceof LeapAtTargetGoal);
            goalSelector.addGoal(3, new BabySpiderLeapGoal(self));
        } else {
            if (scaleAttr != null) scaleAttr.setBaseValue(1.0);
            if (speedAttr != null) speedAttr.removeModifier(BABY_SPIDER_SPEED_MODIFIER_ID);
            goalSelector.removeAllGoals(g -> g instanceof BabySpiderLeapGoal);
            goalSelector.addGoal(3, new LeapAtTargetGoal(self, 0.4F));
        }
    }
}
