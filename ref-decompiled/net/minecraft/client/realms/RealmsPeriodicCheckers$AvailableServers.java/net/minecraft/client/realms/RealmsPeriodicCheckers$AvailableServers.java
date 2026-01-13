/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(value=EnvType.CLIENT)
public record RealmsPeriodicCheckers.AvailableServers(List<RealmsServer> serverList, List<RealmsServer> availableSnapshotServers) {
}
