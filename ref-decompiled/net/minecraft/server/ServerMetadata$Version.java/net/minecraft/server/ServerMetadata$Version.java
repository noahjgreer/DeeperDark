/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;

public record ServerMetadata.Version(String gameVersion, int protocolVersion) {
    public static final Codec<ServerMetadata.Version> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("name").forGetter(ServerMetadata.Version::gameVersion), (App)Codec.INT.fieldOf("protocol").forGetter(ServerMetadata.Version::protocolVersion)).apply((Applicative)instance, ServerMetadata.Version::new));

    public static ServerMetadata.Version create() {
        GameVersion gameVersion = SharedConstants.getGameVersion();
        return new ServerMetadata.Version(gameVersion.name(), gameVersion.protocolVersion());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerMetadata.Version.class, "name;protocol", "gameVersion", "protocolVersion"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerMetadata.Version.class, "name;protocol", "gameVersion", "protocolVersion"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerMetadata.Version.class, "name;protocol", "gameVersion", "protocolVersion"}, this, object);
    }
}
