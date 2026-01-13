/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
record QuickPlayLogger.Log(QuickPlayLogger.QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameMode gameMode) {
    public static final Codec<QuickPlayLogger.Log> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)QuickPlayLogger.QuickPlayWorld.CODEC.forGetter(QuickPlayLogger.Log::quickPlayWorld), (App)Codecs.INSTANT.fieldOf("lastPlayedTime").forGetter(QuickPlayLogger.Log::lastPlayedTime), (App)GameMode.CODEC.fieldOf("gamemode").forGetter(QuickPlayLogger.Log::gameMode)).apply((Applicative)instance, QuickPlayLogger.Log::new));

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{QuickPlayLogger.Log.class, "quickPlayWorld;lastPlayedTime;gamemode", "quickPlayWorld", "lastPlayedTime", "gameMode"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{QuickPlayLogger.Log.class, "quickPlayWorld;lastPlayedTime;gamemode", "quickPlayWorld", "lastPlayedTime", "gameMode"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{QuickPlayLogger.Log.class, "quickPlayWorld;lastPlayedTime;gamemode", "quickPlayWorld", "lastPlayedTime", "gameMode"}, this, object);
    }
}
