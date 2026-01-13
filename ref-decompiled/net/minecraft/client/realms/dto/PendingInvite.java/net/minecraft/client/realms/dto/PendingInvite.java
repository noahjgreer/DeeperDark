/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record PendingInvite(String invitationId, String worldName, String worldOwnerName, UUID worldOwnerUuid, Instant date) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable PendingInvite parse(JsonObject json) {
        try {
            return new PendingInvite(JsonUtils.getNullableStringOr("invitationId", json, ""), JsonUtils.getNullableStringOr("worldName", json, ""), JsonUtils.getNullableStringOr("worldOwnerName", json, ""), JsonUtils.getUuidOr("worldOwnerUuid", json, Util.NIL_UUID), JsonUtils.getInstantOr("date", json));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse PendingInvite", (Throwable)exception);
            return null;
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PendingInvite.class, "invitationId;realmName;realmOwnerName;realmOwnerUuid;date", "invitationId", "worldName", "worldOwnerName", "worldOwnerUuid", "date"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PendingInvite.class, "invitationId;realmName;realmOwnerName;realmOwnerUuid;date", "invitationId", "worldName", "worldOwnerName", "worldOwnerUuid", "date"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PendingInvite.class, "invitationId;realmName;realmOwnerName;realmOwnerUuid;date", "invitationId", "worldName", "worldOwnerName", "worldOwnerUuid", "date"}, this, object);
    }
}
