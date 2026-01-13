/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class RealmsAvailability.Type
extends Enum<RealmsAvailability.Type> {
    public static final /* enum */ RealmsAvailability.Type SUCCESS = new RealmsAvailability.Type();
    public static final /* enum */ RealmsAvailability.Type INCOMPATIBLE_CLIENT = new RealmsAvailability.Type();
    public static final /* enum */ RealmsAvailability.Type NEEDS_PARENTAL_CONSENT = new RealmsAvailability.Type();
    public static final /* enum */ RealmsAvailability.Type AUTHENTICATION_ERROR = new RealmsAvailability.Type();
    public static final /* enum */ RealmsAvailability.Type UNEXPECTED_ERROR = new RealmsAvailability.Type();
    private static final /* synthetic */ RealmsAvailability.Type[] field_45190;

    public static RealmsAvailability.Type[] values() {
        return (RealmsAvailability.Type[])field_45190.clone();
    }

    public static RealmsAvailability.Type valueOf(String string) {
        return Enum.valueOf(RealmsAvailability.Type.class, string);
    }

    private static /* synthetic */ RealmsAvailability.Type[] method_52629() {
        return new RealmsAvailability.Type[]{SUCCESS, INCOMPATIBLE_CLIENT, NEEDS_PARENTAL_CONSENT, AUTHENTICATION_ERROR, UNEXPECTED_ERROR};
    }

    static {
        field_45190 = RealmsAvailability.Type.method_52629();
    }
}
