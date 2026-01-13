/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(value=EnvType.CLIENT)
public static interface ReporterEnvironment.Server {

    @Environment(value=EnvType.CLIENT)
    public record Realm(long realmId, int slotId) implements ReporterEnvironment.Server
    {
        public Realm(RealmsServer server) {
            this(server.id, server.activeSlot);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ThirdParty
    extends Record
    implements ReporterEnvironment.Server {
        final String ip;

        public ThirdParty(String ip) {
            this.ip = ip;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this, object);
        }

        public String ip() {
            return this.ip;
        }
    }
}
