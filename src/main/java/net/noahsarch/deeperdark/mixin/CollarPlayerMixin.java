package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.noahsarch.deeperdark.duck.CollarHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class CollarPlayerMixin extends LivingEntity implements CollarHolder {

    @Unique
    private ItemStack deeperdark$collarItem = ItemStack.EMPTY;

    protected CollarPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public ItemStack deeperdark$getCollarItem() {
        return this.deeperdark$collarItem;
    }

    @Override
    public void deeperdark$setCollarItem(ItemStack stack) {
        this.deeperdark$collarItem = stack == null ? ItemStack.EMPTY : stack;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$saveCollar(ValueOutput output, CallbackInfo ci) {
        if (!this.deeperdark$collarItem.isEmpty()) {
            output.store("deeperdark_collar", ItemStack.CODEC, this.deeperdark$collarItem);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$loadCollar(ValueInput input, CallbackInfo ci) {
        this.deeperdark$collarItem = input.read("deeperdark_collar", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
