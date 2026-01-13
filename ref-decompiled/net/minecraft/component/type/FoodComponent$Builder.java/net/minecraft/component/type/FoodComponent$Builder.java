/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component.type;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.HungerConstants;

public static class FoodComponent.Builder {
    private int nutrition;
    private float saturationModifier;
    private boolean canAlwaysEat;

    public FoodComponent.Builder nutrition(int nutrition) {
        this.nutrition = nutrition;
        return this;
    }

    public FoodComponent.Builder saturationModifier(float saturationModifier) {
        this.saturationModifier = saturationModifier;
        return this;
    }

    public FoodComponent.Builder alwaysEdible() {
        this.canAlwaysEat = true;
        return this;
    }

    public FoodComponent build() {
        float f = HungerConstants.calculateSaturation(this.nutrition, this.saturationModifier);
        return new FoodComponent(this.nutrition, f, this.canAlwaysEat);
    }
}
