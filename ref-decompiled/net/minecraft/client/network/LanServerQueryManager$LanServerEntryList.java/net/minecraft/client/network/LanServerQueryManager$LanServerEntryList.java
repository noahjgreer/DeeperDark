/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerPinger;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class LanServerQueryManager.LanServerEntryList {
    private final List<LanServerInfo> serverEntries = Lists.newArrayList();
    private boolean dirty;

    public synchronized @Nullable List<LanServerInfo> getEntriesIfUpdated() {
        if (this.dirty) {
            List<LanServerInfo> list = List.copyOf(this.serverEntries);
            this.dirty = false;
            return list;
        }
        return null;
    }

    public synchronized void addServer(String announcement, InetAddress address) {
        String string = LanServerPinger.parseAnnouncementMotd(announcement);
        Object string2 = LanServerPinger.parseAnnouncementAddressPort(announcement);
        if (string2 == null) {
            return;
        }
        string2 = address.getHostAddress() + ":" + (String)string2;
        boolean bl = false;
        for (LanServerInfo lanServerInfo : this.serverEntries) {
            if (!lanServerInfo.getAddressPort().equals(string2)) continue;
            lanServerInfo.updateLastTime();
            bl = true;
            break;
        }
        if (!bl) {
            this.serverEntries.add(new LanServerInfo(string, (String)string2));
            this.dirty = true;
        }
    }
}
