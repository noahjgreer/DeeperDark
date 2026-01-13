/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

record ContainerComponent.Slot(int index, ItemStack item) {
    public static final Codec<ContainerComponent.Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)255).fieldOf("slot").forGetter(ContainerComponent.Slot::index), (App)ItemStack.CODEC.fieldOf("item").forGetter(ContainerComponent.Slot::item)).apply((Applicative)instance, ContainerComponent.Slot::new));
}
