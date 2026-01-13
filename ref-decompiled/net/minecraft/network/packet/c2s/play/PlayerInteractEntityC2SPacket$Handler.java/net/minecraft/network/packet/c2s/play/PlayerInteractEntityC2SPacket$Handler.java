/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public static interface PlayerInteractEntityC2SPacket.Handler {
    public void interact(Hand var1);

    public void interactAt(Hand var1, Vec3d var2);

    public void attack();
}
