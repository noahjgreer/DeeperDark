/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Locale;
import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.RpcEncodingException;
import net.minecraft.server.dedicated.management.RpcException;
import net.minecraft.server.dedicated.management.RpcMethodInfo;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;
import org.jspecify.annotations.Nullable;

public record IncomingRpcMethod.Parameterized<Params, Result>(RpcMethodInfo<Params, Result> info, IncomingRpcMethod.Attributes attributes, IncomingRpcMethod.ParameterizedHandler<Params, Result> handler) implements IncomingRpcMethod<Params, Result>
{
    @Override
    public JsonElement handle(ManagementHandlerDispatcher dispatcher, @Nullable JsonElement parameters, ManagementConnectionId remote) {
        JsonElement jsonElement2;
        if (parameters == null || !parameters.isJsonArray() && !parameters.isJsonObject()) {
            throw new RpcException("Expected params as array or named");
        }
        if (this.info.params().isEmpty()) {
            throw new IllegalArgumentException("Method defined as having parameters without describing them");
        }
        if (parameters.isJsonObject()) {
            String string = this.info.params().get().name();
            JsonElement jsonElement = parameters.getAsJsonObject().get(string);
            if (jsonElement == null) {
                throw new RpcException(String.format(Locale.ROOT, "Params passed by-name, but expected param [%s] does not exist", string));
            }
            jsonElement2 = jsonElement;
        } else {
            JsonArray jsonArray = parameters.getAsJsonArray();
            if (jsonArray.isEmpty() || jsonArray.size() > 1) {
                throw new RpcException("Expected exactly one element in the params array");
            }
            jsonElement2 = jsonArray.get(0);
        }
        Object object = this.info.params().get().schema().codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement2).getOrThrow(RpcException::new);
        Result object2 = this.handler.apply(dispatcher, object, remote);
        if (this.info.result().isEmpty()) {
            throw new IllegalStateException("No result codec defined");
        }
        return (JsonElement)this.info.result().get().schema().codec().encodeStart((DynamicOps)JsonOps.INSTANCE, object2).getOrThrow(RpcEncodingException::new);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{IncomingRpcMethod.Parameterized.class, "info;attributes;function", "info", "attributes", "handler"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IncomingRpcMethod.Parameterized.class, "info;attributes;function", "info", "attributes", "handler"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IncomingRpcMethod.Parameterized.class, "info;attributes;function", "info", "attributes", "handler"}, this, object);
    }
}
