/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.context;

import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

public static final class LootContext.EntityReference
extends Enum<LootContext.EntityReference>
implements StringIdentifiable,
LootEntityValueSource.ContextBased<Entity> {
    public static final /* enum */ LootContext.EntityReference THIS = new LootContext.EntityReference("this", LootContextParameters.THIS_ENTITY);
    public static final /* enum */ LootContext.EntityReference ATTACKER = new LootContext.EntityReference("attacker", LootContextParameters.ATTACKING_ENTITY);
    public static final /* enum */ LootContext.EntityReference DIRECT_ATTACKER = new LootContext.EntityReference("direct_attacker", LootContextParameters.DIRECT_ATTACKING_ENTITY);
    public static final /* enum */ LootContext.EntityReference ATTACKING_PLAYER = new LootContext.EntityReference("attacking_player", LootContextParameters.LAST_DAMAGE_PLAYER);
    public static final /* enum */ LootContext.EntityReference TARGET_ENTITY = new LootContext.EntityReference("target_entity", LootContextParameters.TARGET_ENTITY);
    public static final /* enum */ LootContext.EntityReference INTERACTING_ENTITY = new LootContext.EntityReference("interacting_entity", LootContextParameters.INTERACTING_ENTITY);
    public static final StringIdentifiable.EnumCodec<LootContext.EntityReference> CODEC;
    private final String type;
    private final ContextParameter<? extends Entity> parameter;
    private static final /* synthetic */ LootContext.EntityReference[] field_940;

    public static LootContext.EntityReference[] values() {
        return (LootContext.EntityReference[])field_940.clone();
    }

    public static LootContext.EntityReference valueOf(String string) {
        return Enum.valueOf(LootContext.EntityReference.class, string);
    }

    private LootContext.EntityReference(String type, ContextParameter<? extends Entity> parameter) {
        this.type = type;
        this.parameter = parameter;
    }

    @Override
    public ContextParameter<? extends Entity> contextParam() {
        return this.parameter;
    }

    public static LootContext.EntityReference fromString(String type) {
        LootContext.EntityReference entityReference = CODEC.byId(type);
        if (entityReference != null) {
            return entityReference;
        }
        throw new IllegalArgumentException("Invalid entity target " + type);
    }

    @Override
    public String asString() {
        return this.type;
    }

    private static /* synthetic */ LootContext.EntityReference[] method_36793() {
        return new LootContext.EntityReference[]{THIS, ATTACKER, DIRECT_ATTACKER, ATTACKING_PLAYER, TARGET_ENTITY, INTERACTING_ENTITY};
    }

    static {
        field_940 = LootContext.EntityReference.method_36793();
        CODEC = StringIdentifiable.createCodec(LootContext.EntityReference::values);
    }
}
