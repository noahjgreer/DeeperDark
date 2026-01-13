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
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcMethodInfo;
import org.jspecify.annotations.Nullable;

public record OutgoingRpcMethod.Notification<Params>(RpcMethodInfo<Params, Void> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Params, Void>
{
    @Override
    public @Nullable JsonElement encodeParams(Params params) {
        if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
        }
        return (JsonElement)this.info.params().get().schema().codec().encodeStart((DynamicOps)JsonOps.INSTANCE, params).getOrThrow();
    }
}
