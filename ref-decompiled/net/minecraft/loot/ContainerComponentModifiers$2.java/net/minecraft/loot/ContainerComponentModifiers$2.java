/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;

class ContainerComponentModifiers.2
implements ContainerComponentModifier<BundleContentsComponent> {
    ContainerComponentModifiers.2() {
    }

    @Override
    public ComponentType<BundleContentsComponent> getComponentType() {
        return DataComponentTypes.BUNDLE_CONTENTS;
    }

    @Override
    public BundleContentsComponent getDefault() {
        return BundleContentsComponent.DEFAULT;
    }

    @Override
    public Stream<ItemStack> stream(BundleContentsComponent bundleContentsComponent) {
        return bundleContentsComponent.stream();
    }

    @Override
    public BundleContentsComponent apply(BundleContentsComponent bundleContentsComponent, Stream<ItemStack> stream) {
        BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent).clear();
        stream.forEach(builder::add);
        return builder.build();
    }

    @Override
    public /* synthetic */ Object getDefault() {
        return this.getDefault();
    }
}
