/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.session.report.ReporterEnvironment;

@Environment(value=EnvType.CLIENT)
public record ReporterEnvironment.Server.Realm(long realmId, int slotId) implements ReporterEnvironment.Server
{
    public ReporterEnvironment.Server.Realm(RealmsServer server) {
        this(server.id, server.activeSlot);
    }
}
