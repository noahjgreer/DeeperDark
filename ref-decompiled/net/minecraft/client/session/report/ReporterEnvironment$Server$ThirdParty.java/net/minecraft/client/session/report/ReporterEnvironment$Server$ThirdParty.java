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
import net.minecraft.client.session.report.ReporterEnvironment;

@Environment(value=EnvType.CLIENT)
public static final class ReporterEnvironment.Server.ThirdParty
extends Record
implements ReporterEnvironment.Server {
    final String ip;

    public ReporterEnvironment.Server.ThirdParty(String ip) {
        this.ip = ip;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReporterEnvironment.Server.ThirdParty.class, "ip", "ip"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReporterEnvironment.Server.ThirdParty.class, "ip", "ip"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReporterEnvironment.Server.ThirdParty.class, "ip", "ip"}, this, object);
    }

    public String ip() {
        return this.ip;
    }
}
