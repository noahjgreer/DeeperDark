/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;

class ContainerComponentModifiers.1
implements ContainerComponentModifier<ContainerComponent> {
    ContainerComponentModifiers.1() {
    }

    @Override
    public ComponentType<ContainerComponent> getComponentType() {
        return DataComponentTypes.CONTAINER;
    }

    @Override
    public Stream<ItemStack> stream(ContainerComponent containerComponent) {
        return containerComponent.stream();
    }

    @Override
    public ContainerComponent getDefault() {
        return ContainerComponent.DEFAULT;
    }

    @Override
    public ContainerComponent apply(ContainerComponent containerComponent, Stream<ItemStack> stream) {
        return ContainerComponent.fromStacks(stream.toList());
    }

    @Override
    public /* synthetic */ Object getDefault() {
        return this.getDefault();
    }
}
