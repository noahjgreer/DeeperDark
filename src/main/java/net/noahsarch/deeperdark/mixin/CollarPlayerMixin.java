package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class CollarPlayerMixin extends LivingEntity implements CollarHolder {

    @Unique private ItemStack deeperdark$collarItem = ItemStack.EMPTY;
    @Unique private boolean deeperdark$arrowFromCollar = false;

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

    @Override
    public boolean deeperdark$isArrowFromCollar() { return this.deeperdark$arrowFromCollar; }

    @Override
    public void deeperdark$setArrowFromCollar(boolean val) { this.deeperdark$arrowFromCollar = val; }

    @Inject(method = "getProjectile", at = @At("RETURN"), cancellable = true)
    private void deeperdark$checkCollarForArrow(ItemStack heldWeapon, CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            this.deeperdark$arrowFromCollar = false;
            return;
        }
        if (this.hasInfiniteMaterials()) return;
        if (!(heldWeapon.getItem() instanceof ProjectileWeaponItem pwi)) return;

        ItemStack collar = this.deeperdark$collarItem;
        if (collar.isEmpty() || !(collar.getItem() instanceof CollarItem)) return;

        ItemContainerContents contents = collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> trinkets = NonNullList.withSize(5, ItemStack.EMPTY);
        contents.copyInto(trinkets);

        for (ItemStack trinket : trinkets) {
            if (!trinket.isEmpty() && pwi.getAllSupportedProjectiles().test(trinket)) {
                this.deeperdark$arrowFromCollar = true;
                cir.setReturnValue(trinket.copyWithCount(1));
                return;
            }
        }
        this.deeperdark$arrowFromCollar = false;
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
