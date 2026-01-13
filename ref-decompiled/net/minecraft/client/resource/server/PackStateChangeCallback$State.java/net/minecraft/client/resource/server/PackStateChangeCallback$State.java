/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class PackStateChangeCallback.State
extends Enum<PackStateChangeCallback.State> {
    public static final /* enum */ PackStateChangeCallback.State ACCEPTED = new PackStateChangeCallback.State();
    public static final /* enum */ PackStateChangeCallback.State DOWNLOADED = new PackStateChangeCallback.State();
    private static final /* synthetic */ PackStateChangeCallback.State[] field_47701;

    public static PackStateChangeCallback.State[] values() {
        return (PackStateChangeCallback.State[])field_47701.clone();
    }

    public static PackStateChangeCallback.State valueOf(String string) {
        return Enum.valueOf(PackStateChangeCallback.State.class, string);
    }

    private static /* synthetic */ PackStateChangeCallback.State[] method_55621() {
        return new PackStateChangeCallback.State[]{ACCEPTED, DOWNLOADED};
    }

    static {
        field_47701 = PackStateChangeCallback.State.method_55621();
    }
}
