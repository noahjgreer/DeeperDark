/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.management;

import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcMethodInfo;

@FunctionalInterface
public static interface OutgoingRpcMethod.Factory<Params, Result> {
    public OutgoingRpcMethod<Params, Result> create(RpcMethodInfo<Params, Result> var1, OutgoingRpcMethod.Attributes var2);
}
