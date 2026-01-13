/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.management;

import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;

@FunctionalInterface
public static interface IncomingRpcMethod.ParameterizedHandler<Params, Result> {
    public Result apply(ManagementHandlerDispatcher var1, Params var2, ManagementConnectionId var3);
}
