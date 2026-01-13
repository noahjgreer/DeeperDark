/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.Locale;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class RealmsClient.Environment
extends Enum<RealmsClient.Environment> {
    public static final /* enum */ RealmsClient.Environment PRODUCTION = new RealmsClient.Environment("pc.realms.minecraft.net", "java.frontendlegacy.realms.minecraft-services.net", "https");
    public static final /* enum */ RealmsClient.Environment STAGE = new RealmsClient.Environment("pc-stage.realms.minecraft.net", "java.frontendlegacy.stage-c2a40e62.realms.minecraft-services.net", "https");
    public static final /* enum */ RealmsClient.Environment LOCAL = new RealmsClient.Environment("localhost:8080", "localhost:8080", "http");
    public final String baseUrl;
    public final String aksUrl;
    public final String protocol;
    private static final /* synthetic */ RealmsClient.Environment[] field_19591;

    public static RealmsClient.Environment[] values() {
        return (RealmsClient.Environment[])field_19591.clone();
    }

    public static RealmsClient.Environment valueOf(String name) {
        return Enum.valueOf(RealmsClient.Environment.class, name);
    }

    private RealmsClient.Environment(String baseUrl, String aksUrl, String protocol) {
        this.baseUrl = baseUrl;
        this.aksUrl = aksUrl;
        this.protocol = protocol;
    }

    public static Optional<RealmsClient.Environment> fromName(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "production" -> Optional.of(PRODUCTION);
            case "local" -> Optional.of(LOCAL);
            case "stage", "staging" -> Optional.of(STAGE);
            default -> Optional.empty();
        };
    }

    private static /* synthetic */ RealmsClient.Environment[] method_36847() {
        return new RealmsClient.Environment[]{PRODUCTION, STAGE, LOCAL};
    }

    static {
        field_19591 = RealmsClient.Environment.method_36847();
    }
}
