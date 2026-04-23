package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
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

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerEntityMixin {

    @Shadow private void startConverting(@Nullable UUID uuid, int delay) {}

    @Unique
    private boolean deeperdark$isExplosiveCure = false;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(ValueOutput view, CallbackInfo ci) {
        view.putBoolean("DeeperDarkExplosiveCure", deeperdark$isExplosiveCure);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ValueInput view, CallbackInfo ci) {
        deeperdark$isExplosiveCure = view.getBooleanOr("DeeperDarkExplosiveCure", false);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void deeperdark$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            ZombieVillager self = (ZombieVillager) (Object) this;
            if (self.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                Level world = ((EntityAccessor)self).deeperdark$getWorld();
                if (!world.isClientSide()) {
                    this.startConverting(player.getUUID(), self.getRandom().nextInt(2401) + 3600);

                    this.deeperdark$isExplosiveCure = true;

                    self.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "finishConversion", at = @At("HEAD"), cancellable = true)
    private void deeperdark$finishConversion(ServerLevel world, CallbackInfo ci) {
        if (deeperdark$isExplosiveCure) {
            ZombieVillager self = (ZombieVillager) (Object) this;

            world.explode(self, self.getX(), self.getY(), self.getZ(), 128.0F, Level.ExplosionInteraction.TNT);

            self.discard();

            ci.cancel();
        }
    }
}
