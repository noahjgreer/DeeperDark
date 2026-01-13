/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.Ping
 *  net.minecraft.client.realms.Ping$Region
 *  net.minecraft.client.realms.dto.RegionPingResult
 *  net.minecraft.util.Util
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.client.realms;

import com.google.common.collect.Lists;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Ping {
    public static List<RegionPingResult> ping(Region ... regions) {
        for (Region region : regions) {
            Ping.ping((String)region.endpoint);
        }
        ArrayList list = Lists.newArrayList();
        for (Region region2 : regions) {
            list.add(new RegionPingResult(region2.name, Ping.ping((String)region2.endpoint)));
        }
        list.sort(Comparator.comparingInt(RegionPingResult::ping));
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int ping(String host) {
        int i = 700;
        long l = 0L;
        Socket socket = null;
        for (int j = 0; j < 5; ++j) {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(host, 80);
                socket = new Socket();
                long m = Ping.now();
                socket.connect(socketAddress, 700);
                l += Ping.now() - m;
                IOUtils.closeQuietly((Socket)socket);
                continue;
            }
            catch (Exception exception) {
                l += 700L;
                continue;
            }
            finally {
                IOUtils.closeQuietly(socket);
            }
        }
        return (int)((double)l / 5.0);
    }

    private static long now() {
        return Util.getMeasuringTimeMs();
    }

    public static List<RegionPingResult> pingAllRegions() {
        return Ping.ping((Region[])Region.values());
    }
}

