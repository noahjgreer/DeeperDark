/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class ClientPlayNetworkHandler.CommandRunResult
extends Enum<ClientPlayNetworkHandler.CommandRunResult> {
    public static final /* enum */ ClientPlayNetworkHandler.CommandRunResult NO_ISSUES = new ClientPlayNetworkHandler.CommandRunResult();
    public static final /* enum */ ClientPlayNetworkHandler.CommandRunResult PARSE_ERRORS = new ClientPlayNetworkHandler.CommandRunResult();
    public static final /* enum */ ClientPlayNetworkHandler.CommandRunResult SIGNATURE_REQUIRED = new ClientPlayNetworkHandler.CommandRunResult();
    public static final /* enum */ ClientPlayNetworkHandler.CommandRunResult PERMISSIONS_REQUIRED = new ClientPlayNetworkHandler.CommandRunResult();
    private static final /* synthetic */ ClientPlayNetworkHandler.CommandRunResult[] field_60790;

    public static ClientPlayNetworkHandler.CommandRunResult[] values() {
        return (ClientPlayNetworkHandler.CommandRunResult[])field_60790.clone();
    }

    public static ClientPlayNetworkHandler.CommandRunResult valueOf(String string) {
        return Enum.valueOf(ClientPlayNetworkHandler.CommandRunResult.class, string);
    }

    private static /* synthetic */ ClientPlayNetworkHandler.CommandRunResult[] method_71931() {
        return new ClientPlayNetworkHandler.CommandRunResult[]{NO_ISSUES, PARSE_ERRORS, SIGNATURE_REQUIRED, PERMISSIONS_REQUIRED};
    }

    static {
        field_60790 = ClientPlayNetworkHandler.CommandRunResult.method_71931();
    }
}
