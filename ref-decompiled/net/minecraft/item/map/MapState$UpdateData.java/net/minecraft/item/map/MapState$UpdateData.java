/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.item.map;

import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.item.map.MapState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record MapState.UpdateData(int startX, int startZ, int width, int height, byte[] colors) {
    public static final PacketCodec<ByteBuf, Optional<MapState.UpdateData>> CODEC = PacketCodec.ofStatic(MapState.UpdateData::encode, MapState.UpdateData::decode);

    private static void encode(ByteBuf buf, Optional<MapState.UpdateData> updateData) {
        if (updateData.isPresent()) {
            MapState.UpdateData updateData2 = updateData.get();
            buf.writeByte(updateData2.width);
            buf.writeByte(updateData2.height);
            buf.writeByte(updateData2.startX);
            buf.writeByte(updateData2.startZ);
            PacketByteBuf.writeByteArray(buf, updateData2.colors);
        } else {
            buf.writeByte(0);
        }
    }

    private static Optional<MapState.UpdateData> decode(ByteBuf buf) {
        short i = buf.readUnsignedByte();
        if (i > 0) {
            short j = buf.readUnsignedByte();
            short k = buf.readUnsignedByte();
            short l = buf.readUnsignedByte();
            byte[] bs = PacketByteBuf.readByteArray(buf);
            return Optional.of(new MapState.UpdateData(k, l, i, j, bs));
        }
        return Optional.empty();
    }

    public void setColorsTo(MapState mapState) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                mapState.setColor(this.startX + i, this.startZ + j, this.colors[i + j * this.width]);
            }
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MapState.UpdateData.class, "startX;startY;width;height;mapColors", "startX", "startZ", "width", "height", "colors"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MapState.UpdateData.class, "startX;startY;width;height;mapColors", "startX", "startZ", "width", "height", "colors"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MapState.UpdateData.class, "startX;startY;width;height;mapColors", "startX", "startZ", "width", "height", "colors"}, this, object);
    }
}
