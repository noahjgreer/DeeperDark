package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.noahsarch.deeperdark.duck.WitchConversionAccessor;

@Mixin(MobEntity.class)
public abstract class WitchMobMixin {

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void writeCustomData(WriteView nbt, CallbackInfo ci) {
        if ((Object)this instanceof WitchEntity) {
            WitchConversionAccessor witch = (WitchConversionAccessor) (Object) this;
            if (witch.deeperdark$isConverting()) {
                nbt.putInt("ConversionTime", witch.deeperdark$getConversionTimer());
            } else {
                nbt.putInt("ConversionTime", -1);
            }
            if (witch.deeperdark$getConverter() != null) {
                nbt.putString("ConversionPlayer", witch.deeperdark$getConverter().toString());
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void readCustomData(ReadView nbt, CallbackInfo ci) {
        if ((Object)this instanceof WitchEntity) {
            WitchConversionAccessor witch = (WitchConversionAccessor) (Object) this;
            nbt.getOptionalInt("ConversionTime").ifPresent(time -> {
                if (time > -1) {
                    java.util.UUID converterUuid = null;
                    if (nbt.contains("ConversionPlayer")) {
                        try {
                            converterUuid = java.util.UUID.fromString(nbt.getString("ConversionPlayer", ""));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    witch.deeperdark$setConverting(converterUuid, time);
                }
            });
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if ((Object)this instanceof WitchEntity) {
            WitchEntity self = (WitchEntity) (Object) this;
            WitchConversionAccessor witch = (WitchConversionAccessor) self;
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.GOLDEN_APPLE)) {
                if (self.hasStatusEffect(StatusEffects.WEAKNESS)) {
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    if (!self.getWorld().isClient) {
                         witch.deeperdark$setConverting(player.getUuid(), self.getRandom().nextInt(2401) + 3600);
                    }
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }
}

