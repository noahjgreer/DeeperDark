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
public static final class RealmsClient.CompatibleVersionResponse
extends Enum<RealmsClient.CompatibleVersionResponse> {
    public static final /* enum */ RealmsClient.CompatibleVersionResponse COMPATIBLE = new RealmsClient.CompatibleVersionResponse();
    public static final /* enum */ RealmsClient.CompatibleVersionResponse OUTDATED = new RealmsClient.CompatibleVersionResponse();
    public static final /* enum */ RealmsClient.CompatibleVersionResponse OTHER = new RealmsClient.CompatibleVersionResponse();
    private static final /* synthetic */ RealmsClient.CompatibleVersionResponse[] field_19585;

    public static RealmsClient.CompatibleVersionResponse[] values() {
        return (RealmsClient.CompatibleVersionResponse[])field_19585.clone();
    }

    public static RealmsClient.CompatibleVersionResponse valueOf(String name) {
        return Enum.valueOf(RealmsClient.CompatibleVersionResponse.class, name);
    }

    private static /* synthetic */ RealmsClient.CompatibleVersionResponse[] method_36846() {
        return new RealmsClient.CompatibleVersionResponse[]{COMPATIBLE, OUTDATED, OTHER};
    }

    static {
        field_19585 = RealmsClient.CompatibleVersionResponse.method_36846();
    }
}
