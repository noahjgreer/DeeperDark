/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ClientInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$RealmInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ThirdPartyServerInfo
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.session.report.ReporterEnvironment
 *  net.minecraft.client.session.report.ReporterEnvironment$Server
 *  net.minecraft.client.session.report.ReporterEnvironment$Server$Realm
 *  net.minecraft.client.session.report.ReporterEnvironment$Server$ThirdParty
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.session.report.ReporterEnvironment;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record ReporterEnvironment(String clientVersion, // Could not load outer class - annotation placement on inner may be incorrect
@Nullable ReporterEnvironment.Server server) {
    private final String clientVersion;
    private final // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ReporterEnvironment.Server server;

    public ReporterEnvironment(String clientVersion, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ReporterEnvironment.Server server) {
        this.clientVersion = clientVersion;
        this.server = server;
    }

    public static ReporterEnvironment ofIntegratedServer() {
        return ReporterEnvironment.ofServer(null);
    }

    public static ReporterEnvironment ofThirdPartyServer(String ip) {
        return ReporterEnvironment.ofServer((Server)new Server.ThirdParty(ip));
    }

    public static ReporterEnvironment ofRealm(RealmsServer server) {
        return ReporterEnvironment.ofServer((Server)new Server.Realm(server));
    }

    public static ReporterEnvironment ofServer(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ReporterEnvironment.Server server) {
        return new ReporterEnvironment(ReporterEnvironment.getVersion(), server);
    }

    public AbuseReportRequest.ClientInfo toClientInfo() {
        return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable AbuseReportRequest.ThirdPartyServerInfo toThirdPartyServerInfo() {
        Server server = this.server;
        if (server instanceof Server.ThirdParty) {
            Server.ThirdParty thirdParty = (Server.ThirdParty)server;
            return new AbuseReportRequest.ThirdPartyServerInfo(thirdParty.ip);
        }
        return null;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable AbuseReportRequest.RealmInfo toRealmInfo() {
        Server server = this.server;
        if (server instanceof Server.Realm) {
            Server.Realm realm = (Server.Realm)server;
            return new AbuseReportRequest.RealmInfo(String.valueOf(realm.realmId()), realm.slotId());
        }
        return null;
    }

    private static String getVersion() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SharedConstants.getGameVersion().id());
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append(" (modded)");
        }
        return stringBuilder.toString();
    }

    public String clientVersion() {
        return this.clientVersion;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ReporterEnvironment.Server server() {
        return this.server;
    }
}

