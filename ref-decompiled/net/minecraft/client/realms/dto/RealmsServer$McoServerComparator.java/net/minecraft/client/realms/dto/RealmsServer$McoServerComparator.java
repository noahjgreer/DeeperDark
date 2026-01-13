/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(value=EnvType.CLIENT)
public static class RealmsServer.McoServerComparator
implements Comparator<RealmsServer> {
    private final String refOwner;

    public RealmsServer.McoServerComparator(String owner) {
        this.refOwner = owner;
    }

    @Override
    public int compare(RealmsServer realmsServer, RealmsServer realmsServer2) {
        return ComparisonChain.start().compareTrueFirst(realmsServer.isPrerelease(), realmsServer2.isPrerelease()).compareTrueFirst(realmsServer.state == RealmsServer.State.UNINITIALIZED, realmsServer2.state == RealmsServer.State.UNINITIALIZED).compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial).compareTrueFirst(Objects.equals(realmsServer.owner, this.refOwner), Objects.equals(realmsServer2.owner, this.refOwner)).compareFalseFirst(realmsServer.expired, realmsServer2.expired).compareTrueFirst(realmsServer.state == RealmsServer.State.OPEN, realmsServer2.state == RealmsServer.State.OPEN).compare(realmsServer.id, realmsServer2.id).result();
    }

    @Override
    public /* synthetic */ int compare(Object one, Object two) {
        return this.compare((RealmsServer)one, (RealmsServer)two);
    }
}
