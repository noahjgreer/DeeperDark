/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.loot.function;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;

public static class SetAttributesLootFunction.Builder
extends ConditionalLootFunction.Builder<SetAttributesLootFunction.Builder> {
    private final boolean replace;
    private final List<SetAttributesLootFunction.Attribute> attributes = Lists.newArrayList();

    public SetAttributesLootFunction.Builder(boolean replace) {
        this.replace = replace;
    }

    public SetAttributesLootFunction.Builder() {
        this(false);
    }

    @Override
    protected SetAttributesLootFunction.Builder getThisBuilder() {
        return this;
    }

    public SetAttributesLootFunction.Builder attribute(SetAttributesLootFunction.AttributeBuilder attribute) {
        this.attributes.add(attribute.build());
        return this;
    }

    @Override
    public LootFunction build() {
        return new SetAttributesLootFunction(this.getConditions(), this.attributes, this.replace);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
