/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.context;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

public static final class LootContext.ItemStackReference
extends Enum<LootContext.ItemStackReference>
implements StringIdentifiable,
LootEntityValueSource.ContextBased<ItemStack> {
    public static final /* enum */ LootContext.ItemStackReference TOOL = new LootContext.ItemStackReference("tool", LootContextParameters.TOOL);
    private final String id;
    private final ContextParameter<? extends ItemStack> parameter;
    private static final /* synthetic */ LootContext.ItemStackReference[] field_63057;

    public static LootContext.ItemStackReference[] values() {
        return (LootContext.ItemStackReference[])field_63057.clone();
    }

    public static LootContext.ItemStackReference valueOf(String string) {
        return Enum.valueOf(LootContext.ItemStackReference.class, string);
    }

    private LootContext.ItemStackReference(String id, ContextParameter<? extends ItemStack> parameter) {
        this.id = id;
        this.parameter = parameter;
    }

    @Override
    public ContextParameter<? extends ItemStack> contextParam() {
        return this.parameter;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ LootContext.ItemStackReference[] method_74900() {
        return new LootContext.ItemStackReference[]{TOOL};
    }

    static {
        field_63057 = LootContext.ItemStackReference.method_74900();
    }
}
