/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.server.dedicated.management;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcMethodInfo;

public record OutgoingRpcMethod.Parameterless<Result>(RpcMethodInfo<Void, Result> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Void, Result>
{
    @Override
    public Result decodeResult(JsonElement result) {
        if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
        }
        return (Result)this.info.result().get().schema().codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)result).getOrThrow();
    }
}
