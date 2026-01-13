/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.attribute;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class EntityAttributeModifier.Operation
extends Enum<EntityAttributeModifier.Operation>
implements StringIdentifiable {
    public static final /* enum */ EntityAttributeModifier.Operation ADD_VALUE = new EntityAttributeModifier.Operation("add_value", 0);
    public static final /* enum */ EntityAttributeModifier.Operation ADD_MULTIPLIED_BASE = new EntityAttributeModifier.Operation("add_multiplied_base", 1);
    public static final /* enum */ EntityAttributeModifier.Operation ADD_MULTIPLIED_TOTAL = new EntityAttributeModifier.Operation("add_multiplied_total", 2);
    public static final IntFunction<EntityAttributeModifier.Operation> ID_TO_VALUE;
    public static final PacketCodec<ByteBuf, EntityAttributeModifier.Operation> PACKET_CODEC;
    public static final Codec<EntityAttributeModifier.Operation> CODEC;
    private final String name;
    private final int id;
    private static final /* synthetic */ EntityAttributeModifier.Operation[] field_6333;

    public static EntityAttributeModifier.Operation[] values() {
        return (EntityAttributeModifier.Operation[])field_6333.clone();
    }

    public static EntityAttributeModifier.Operation valueOf(String string) {
        return Enum.valueOf(EntityAttributeModifier.Operation.class, string);
    }

    private EntityAttributeModifier.Operation(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ EntityAttributeModifier.Operation[] method_36614() {
        return new EntityAttributeModifier.Operation[]{ADD_VALUE, ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL};
    }

    static {
        field_6333 = EntityAttributeModifier.Operation.method_36614();
        ID_TO_VALUE = ValueLists.createIndexToValueFunction(EntityAttributeModifier.Operation::getId, EntityAttributeModifier.Operation.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, EntityAttributeModifier.Operation::getId);
        CODEC = StringIdentifiable.createCodec(EntityAttributeModifier.Operation::values);
    }
}
