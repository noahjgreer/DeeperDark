/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;

class ContainerComponentModifiers.3
implements ContainerComponentModifier<ChargedProjectilesComponent> {
    ContainerComponentModifiers.3() {
    }

    @Override
    public ComponentType<ChargedProjectilesComponent> getComponentType() {
        return DataComponentTypes.CHARGED_PROJECTILES;
    }

    @Override
    public ChargedProjectilesComponent getDefault() {
        return ChargedProjectilesComponent.DEFAULT;
    }

    @Override
    public Stream<ItemStack> stream(ChargedProjectilesComponent chargedProjectilesComponent) {
        return chargedProjectilesComponent.getProjectiles().stream();
    }

    @Override
    public ChargedProjectilesComponent apply(ChargedProjectilesComponent chargedProjectilesComponent, Stream<ItemStack> stream) {
        return ChargedProjectilesComponent.of(stream.toList());
    }

    @Override
    public /* synthetic */ Object getDefault() {
        return this.getDefault();
    }
}
