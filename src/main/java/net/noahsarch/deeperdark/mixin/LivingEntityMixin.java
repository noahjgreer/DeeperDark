package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import net.noahsarch.deeperdark.util.BabySpiderAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    private Optional<BlockPos> lastClimbablePos;

    @Unique
    private int deeperdark$ladderSoundTick = 0;

    @Inject(method = "travelInFluid", at = @At("RETURN"))
    private void applyDepthStriderToLava(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.isInLava() && !self.isInWater()) {
            double waterEfficiency = self.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);

            if (waterEfficiency > 0.0) {
                Vec3 velocity = self.getDeltaMovement();

                double boostFactor = 1.0 + (waterEfficiency * 0.73);

                self.setDeltaMovement(
                    velocity.x * boostFactor,
                    velocity.y,
                    velocity.z * boostFactor
                );
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void deeperdark$tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.isSpectator()) return;

        Optional<BlockPos> climbPos = self.getLastClimbablePos();
        if (climbPos.isEmpty() || climbPos.get().equals(self.blockPosition())) {
            // Empty or vanilla front-side — vanilla handles sounds there.
            deeperdark$ladderSoundTick = 0;
            return;
        }

        // Back-side: entity is adjacent to the ladder (climbPos != blockPosition).
        Vec3 movement = self.getDeltaMovement();
        if (movement.lengthSqr() < 1.0E-4) {
            deeperdark$ladderSoundTick = 0;
            return;
        }

        // Fire at the same cadence as vanilla ladder stepping (~8 ticks at y=0.2/tick).
        if (++deeperdark$ladderSoundTick >= 8) {
            deeperdark$ladderSoundTick = 0;
            BlockState ladderState = self.level().getBlockState(climbPos.get());
            SoundType sound = ladderState.getSoundType();
            self.playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
        }
    }

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void deeperdark$onClimbable(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.isSpectator()) return;
        BlockPos pos = self.blockPosition();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos adjacentPos = pos.relative(dir);
            BlockState adjacentState = self.level().getBlockState(adjacentPos);
            // dir points from player toward the adjacent block; if the ladder's FACING equals dir,
            // the ladder faces away from the player — the player is on its back side.
            if (adjacentState.is(Blocks.LADDER) && adjacentState.getValue(LadderBlock.FACING) == dir) {
                this.lastClimbablePos = Optional.of(adjacentPos);
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
    private void deeperdark$getVoicePitch(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Skeleton && self instanceof BabySkeletonAccessor accessor) {
            if (accessor.deeperdark$isBaby()) {
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }

        if (self instanceof Creeper && self instanceof BabyCreeperAccessor accessor) {
            if (accessor.deeperdark$isBabyCreeper()) {
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }

        if (self instanceof Spider && self instanceof BabySpiderAccessor accessor) {
            if (accessor.deeperdark$isBabySpider()) {
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }
    }
}
