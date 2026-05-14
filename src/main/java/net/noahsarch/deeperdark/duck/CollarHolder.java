package net.noahsarch.deeperdark.duck;

import net.minecraft.world.item.ItemStack;

public interface CollarHolder {
    ItemStack deeperdark$getCollarItem();
    void deeperdark$setCollarItem(ItemStack stack);
    boolean deeperdark$isArrowFromCollar();
    void deeperdark$setArrowFromCollar(boolean val);
}
