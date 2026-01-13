/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class AttributeModifiersComponent.Display.Type
extends Enum<AttributeModifiersComponent.Display.Type>
implements StringIdentifiable {
    public static final /* enum */ AttributeModifiersComponent.Display.Type DEFAULT = new AttributeModifiersComponent.Display.Type("default", 0, AttributeModifiersComponent.Display.Default.CODEC, AttributeModifiersComponent.Display.Default.PACKET_CODEC);
    public static final /* enum */ AttributeModifiersComponent.Display.Type HIDDEN = new AttributeModifiersComponent.Display.Type("hidden", 1, AttributeModifiersComponent.Display.Hidden.CODEC, AttributeModifiersComponent.Display.Hidden.PACKET_CODEC);
    public static final /* enum */ AttributeModifiersComponent.Display.Type OVERRIDE = new AttributeModifiersComponent.Display.Type("override", 2, AttributeModifiersComponent.Display.Override.CODEC, AttributeModifiersComponent.Display.Override.PACKET_CODEC);
    static final Codec<AttributeModifiersComponent.Display.Type> CODEC;
    private static final IntFunction<AttributeModifiersComponent.Display.Type> INDEX_MAPPER;
    static final PacketCodec<ByteBuf, AttributeModifiersComponent.Display.Type> PACKET_CODEC;
    private final String id;
    private final int index;
    final MapCodec<? extends AttributeModifiersComponent.Display> codec;
    private final PacketCodec<RegistryByteBuf, ? extends AttributeModifiersComponent.Display> packetCodec;
    private static final /* synthetic */ AttributeModifiersComponent.Display.Type[] field_59749;

    public static AttributeModifiersComponent.Display.Type[] values() {
        return (AttributeModifiersComponent.Display.Type[])field_59749.clone();
    }

    public static AttributeModifiersComponent.Display.Type valueOf(String string) {
        return Enum.valueOf(AttributeModifiersComponent.Display.Type.class, string);
    }

    private AttributeModifiersComponent.Display.Type(String id, int index, MapCodec<? extends AttributeModifiersComponent.Display> codec, PacketCodec<RegistryByteBuf, ? extends AttributeModifiersComponent.Display> packetCodec) {
        this.id = id;
        this.index = index;
        this.codec = codec;
        this.packetCodec = packetCodec;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private int getIndex() {
        return this.index;
    }

    private PacketCodec<RegistryByteBuf, ? extends AttributeModifiersComponent.Display> getPacketCodec() {
        return this.packetCodec;
    }

    private static /* synthetic */ AttributeModifiersComponent.Display.Type[] method_70738() {
        return new AttributeModifiersComponent.Display.Type[]{DEFAULT, HIDDEN, OVERRIDE};
    }

    static {
        field_59749 = AttributeModifiersComponent.Display.Type.method_70738();
        CODEC = StringIdentifiable.createCodec(AttributeModifiersComponent.Display.Type::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(AttributeModifiersComponent.Display.Type::getIndex, AttributeModifiersComponent.Display.Type.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, AttributeModifiersComponent.Display.Type::getIndex);
    }
}
