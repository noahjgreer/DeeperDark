/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

protected static final class ProfileComponent.Data
extends Record {
    final Optional<String> name;
    final Optional<UUID> id;
    final PropertyMap properties;
    public static final ProfileComponent.Data EMPTY = new ProfileComponent.Data(Optional.empty(), Optional.empty(), PropertyMap.EMPTY);
    static final MapCodec<ProfileComponent.Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ProfileComponent.Data::name), (App)Uuids.INT_STREAM_CODEC.optionalFieldOf("id").forGetter(ProfileComponent.Data::id), (App)Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("properties", (Object)PropertyMap.EMPTY).forGetter(ProfileComponent.Data::properties)).apply((Applicative)instance, ProfileComponent.Data::new));
    public static final PacketCodec<ByteBuf, ProfileComponent.Data> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.PLAYER_NAME.collect(PacketCodecs::optional), ProfileComponent.Data::name, Uuids.PACKET_CODEC.collect(PacketCodecs::optional), ProfileComponent.Data::id, PacketCodecs.PROPERTY_MAP, ProfileComponent.Data::properties, ProfileComponent.Data::new);

    protected ProfileComponent.Data(Optional<String> name, Optional<UUID> id, PropertyMap properties) {
        this.name = name;
        this.id = id;
        this.properties = properties;
    }

    private GameProfile createGameProfile() {
        return ProfileComponent.createGameProfile(this.name, this.id, this.properties);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProfileComponent.Data.class, "name;id;properties", "name", "id", "properties"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProfileComponent.Data.class, "name;id;properties", "name", "id", "properties"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProfileComponent.Data.class, "name;id;properties", "name", "id", "properties"}, this, object);
    }

    public Optional<String> name() {
        return this.name;
    }

    public Optional<UUID> id() {
        return this.id;
    }

    public PropertyMap properties() {
        return this.properties;
    }
}
