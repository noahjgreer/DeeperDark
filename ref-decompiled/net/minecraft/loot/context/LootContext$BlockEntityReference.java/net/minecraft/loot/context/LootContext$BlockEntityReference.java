/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.context;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

public static final class LootContext.BlockEntityReference
extends Enum<LootContext.BlockEntityReference>
implements StringIdentifiable,
LootEntityValueSource.ContextBased<BlockEntity> {
    public static final /* enum */ LootContext.BlockEntityReference BLOCK_ENTITY = new LootContext.BlockEntityReference("block_entity", LootContextParameters.BLOCK_ENTITY);
    private final String id;
    private final ContextParameter<? extends BlockEntity> parameter;
    private static final /* synthetic */ LootContext.BlockEntityReference[] field_49439;

    public static LootContext.BlockEntityReference[] values() {
        return (LootContext.BlockEntityReference[])field_49439.clone();
    }

    public static LootContext.BlockEntityReference valueOf(String string) {
        return Enum.valueOf(LootContext.BlockEntityReference.class, string);
    }

    private LootContext.BlockEntityReference(String id, ContextParameter<? extends BlockEntity> parameter) {
        this.id = id;
        this.parameter = parameter;
    }

    @Override
    public ContextParameter<? extends BlockEntity> contextParam() {
        return this.parameter;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ LootContext.BlockEntityReference[] method_57645() {
        return new LootContext.BlockEntityReference[]{BLOCK_ENTITY};
    }

    static {
        field_49439 = LootContext.BlockEntityReference.method_57645();
    }
}
