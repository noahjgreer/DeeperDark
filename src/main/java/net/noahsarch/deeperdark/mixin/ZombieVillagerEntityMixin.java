package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin {

    @Shadow private void setConverting(@Nullable UUID uuid, int delay) {}

    @Unique
    private boolean deeperdark$isExplosiveCure = false;

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("DeeperDarkExplosiveCure", deeperdark$isExplosiveCure);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ReadView view, CallbackInfo ci) {
        deeperdark$isExplosiveCure = view.getBoolean("DeeperDarkExplosiveCure", false);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void deeperdark$interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            ZombieVillagerEntity self = (ZombieVillagerEntity) (Object) this;
            if (self.hasStatusEffect(StatusEffects.WEAKNESS)) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                World world = ((EntityAccessor)self).deeperdark$getWorld();
                if (!world.isClient()) {
                    // Start conversion with random delay (same as vanilla)
                    // Vanilla uses: this.random.nextInt(2401) + 3600
                    this.setConverting(player.getUuid(), self.getRandom().nextInt(2401) + 3600);

                    // Mark as explosive
                    this.deeperdark$isExplosiveCure = true;

                    // Play cure sound
                    self.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
                }
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "finishConversion", at = @At("HEAD"), cancellable = true)
    private void deeperdark$finishConversion(ServerWorld world, CallbackInfo ci) {
        if (deeperdark$isExplosiveCure) {
            ZombieVillagerEntity self = (ZombieVillagerEntity) (Object) this;

            // Create explosion
            // Power 6.0F is larger than TNT (4.0F)
            world.createExplosion(self, self.getX(), self.getY(), self.getZ(), 128.0F, World.ExplosionSourceType.TNT);

            // Discard the entity
            self.discard();

            // Cancel the conversion to villager
            ci.cancel();
        }
    }
}
