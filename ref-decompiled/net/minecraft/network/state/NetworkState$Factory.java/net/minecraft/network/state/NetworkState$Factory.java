/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.state;

import net.minecraft.network.state.NetworkState;

public static interface NetworkState.Factory {
    public NetworkState.Unbound buildUnbound();
}
