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
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record Subscription(Instant startDate, int daysLeft, SubscriptionType type) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Subscription parse(String json) {
        try {
            JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
            return new Subscription(JsonUtils.getInstantOr("startDate", jsonObject), JsonUtils.getIntOr("daysLeft", jsonObject, 0), Subscription.typeFrom(JsonUtils.getNullableStringOr("subscriptionType", jsonObject, null)));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Subscription", (Throwable)exception);
            return new Subscription(Instant.EPOCH, 0, SubscriptionType.NORMAL);
        }
    }

    private static SubscriptionType typeFrom(@Nullable String subscriptionType) {
        try {
            if (subscriptionType != null) {
                return SubscriptionType.valueOf(subscriptionType);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return SubscriptionType.NORMAL;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class SubscriptionType
    extends Enum<SubscriptionType> {
        public static final /* enum */ SubscriptionType NORMAL = new SubscriptionType();
        public static final /* enum */ SubscriptionType RECURRING = new SubscriptionType();
        private static final /* synthetic */ SubscriptionType[] field_19445;

        public static SubscriptionType[] values() {
            return (SubscriptionType[])field_19445.clone();
        }

        public static SubscriptionType valueOf(String name) {
            return Enum.valueOf(SubscriptionType.class, name);
        }

        private static /* synthetic */ SubscriptionType[] method_36850() {
            return new SubscriptionType[]{NORMAL, RECURRING};
        }

        static {
            field_19445 = SubscriptionType.method_36850();
        }
    }
}
