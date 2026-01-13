/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.Subscription
 *  net.minecraft.client.realms.dto.Subscription$SubscriptionType
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.util.LenientJsonParser
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record Subscription(Instant startDate, int daysLeft, SubscriptionType type) {
    private final Instant startDate;
    private final int daysLeft;
    private final SubscriptionType type;
    private static final Logger LOGGER = LogUtils.getLogger();

    public Subscription(Instant startDate, int daysLeft, SubscriptionType type) {
        this.startDate = startDate;
        this.daysLeft = daysLeft;
        this.type = type;
    }

    public static Subscription parse(String json) {
        try {
            JsonObject jsonObject = LenientJsonParser.parse((String)json).getAsJsonObject();
            return new Subscription(JsonUtils.getInstantOr((String)"startDate", (JsonObject)jsonObject), JsonUtils.getIntOr((String)"daysLeft", (JsonObject)jsonObject, (int)0), Subscription.typeFrom((String)JsonUtils.getNullableStringOr((String)"subscriptionType", (JsonObject)jsonObject, null)));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Subscription", (Throwable)exception);
            return new Subscription(Instant.EPOCH, 0, SubscriptionType.NORMAL);
        }
    }

    private static SubscriptionType typeFrom(@Nullable String subscriptionType) {
        try {
            if (subscriptionType != null) {
                return SubscriptionType.valueOf((String)subscriptionType);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return SubscriptionType.NORMAL;
    }

    public Instant startDate() {
        return this.startDate;
    }

    public int daysLeft() {
        return this.daysLeft;
    }

    public SubscriptionType type() {
        return this.type;
    }
}

