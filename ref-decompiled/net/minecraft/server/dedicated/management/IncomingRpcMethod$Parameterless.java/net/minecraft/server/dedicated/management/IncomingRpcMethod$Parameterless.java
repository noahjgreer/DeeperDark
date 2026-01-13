/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.RpcException;
import net.minecraft.server.dedicated.management.RpcMethodInfo;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;
import org.jspecify.annotations.Nullable;

public record IncomingRpcMethod.Parameterless<Params, Result>(RpcMethodInfo<Params, Result> info, IncomingRpcMethod.Attributes attributes, IncomingRpcMethod.ParameterlessHandler<Result> handler) implements IncomingRpcMethod<Params, Result>
{
    @Override
    public JsonElement handle(ManagementHandlerDispatcher dispatcher, @Nullable JsonElement parameters, ManagementConnectionId remote) {
        if (!(parameters == null || parameters.isJsonArray() && parameters.getAsJsonArray().isEmpty())) {
            throw new RpcException("Expected no params, or an empty array");
        }
        if (this.info.params().isPresent()) {
            throw new IllegalArgumentException("Parameterless method unexpectedly has parameter description");
        }
        Result object = this.handler.apply(dispatcher, remote);
        if (this.info.result().isEmpty()) {
            throw new IllegalStateException("No result codec defined");
        }
        return (JsonElement)this.info.result().get().schema().codec().encodeStart((DynamicOps)JsonOps.INSTANCE, object).getOrThrow(RpcException::new);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{IncomingRpcMethod.Parameterless.class, "info;attributes;supplier", "info", "attributes", "handler"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IncomingRpcMethod.Parameterless.class, "info;attributes;supplier", "info", "attributes", "handler"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IncomingRpcMethod.Parameterless.class, "info;attributes;supplier", "info", "attributes", "handler"}, this, object);
    }
}
