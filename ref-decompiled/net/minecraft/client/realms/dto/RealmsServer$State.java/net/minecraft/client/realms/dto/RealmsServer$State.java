/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class RealmsServer.State
extends Enum<RealmsServer.State> {
    public static final /* enum */ RealmsServer.State CLOSED = new RealmsServer.State();
    public static final /* enum */ RealmsServer.State OPEN = new RealmsServer.State();
    public static final /* enum */ RealmsServer.State UNINITIALIZED = new RealmsServer.State();
    private static final /* synthetic */ RealmsServer.State[] field_19436;

    public static RealmsServer.State[] values() {
        return (RealmsServer.State[])field_19436.clone();
    }

    public static RealmsServer.State valueOf(String name) {
        return Enum.valueOf(RealmsServer.State.class, name);
    }

    private static /* synthetic */ RealmsServer.State[] method_36848() {
        return new RealmsServer.State[]{CLOSED, OPEN, UNINITIALIZED};
    }

    static {
        field_19436 = RealmsServer.State.method_36848();
    }
}
