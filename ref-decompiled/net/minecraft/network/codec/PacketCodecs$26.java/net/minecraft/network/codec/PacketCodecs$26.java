/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.StringEncoding;

class PacketCodecs.26
implements PacketCodec<ByteBuf, PropertyMap> {
    PacketCodecs.26() {
    }

    @Override
    public PropertyMap decode(ByteBuf byteBuf) {
        int i = PacketCodecs.readCollectionSize(byteBuf, 16);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        for (int j = 0; j < i; ++j) {
            String string = StringEncoding.decode(byteBuf, 64);
            String string2 = StringEncoding.decode(byteBuf, Short.MAX_VALUE);
            String string3 = PacketByteBuf.readNullable(byteBuf, bufx -> StringEncoding.decode(bufx, 1024));
            Property property = new Property(string, string2, string3);
            builder.put((Object)property.name(), (Object)property);
        }
        return new PropertyMap((Multimap)builder.build());
    }

    @Override
    public void encode(ByteBuf byteBuf, PropertyMap propertyMap) {
        PacketCodecs.writeCollectionSize(byteBuf, propertyMap.size(), 16);
        for (Property property : propertyMap.values()) {
            StringEncoding.encode(byteBuf, property.name(), 64);
            StringEncoding.encode(byteBuf, property.value(), Short.MAX_VALUE);
            PacketByteBuf.writeNullable(byteBuf, property.signature(), (bufx, signature) -> StringEncoding.encode(bufx, signature, 1024));
        }
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (PropertyMap)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
