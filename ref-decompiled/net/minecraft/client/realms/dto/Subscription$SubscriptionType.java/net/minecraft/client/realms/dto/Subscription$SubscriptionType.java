/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class Subscription.SubscriptionType
extends Enum<Subscription.SubscriptionType> {
    public static final /* enum */ Subscription.SubscriptionType NORMAL = new Subscription.SubscriptionType();
    public static final /* enum */ Subscription.SubscriptionType RECURRING = new Subscription.SubscriptionType();
    private static final /* synthetic */ Subscription.SubscriptionType[] field_19445;

    public static Subscription.SubscriptionType[] values() {
        return (Subscription.SubscriptionType[])field_19445.clone();
    }

    public static Subscription.SubscriptionType valueOf(String name) {
        return Enum.valueOf(Subscription.SubscriptionType.class, name);
    }

    private static /* synthetic */ Subscription.SubscriptionType[] method_36850() {
        return new Subscription.SubscriptionType[]{NORMAL, RECURRING};
    }

    static {
        field_19445 = Subscription.SubscriptionType.method_36850();
    }
}
