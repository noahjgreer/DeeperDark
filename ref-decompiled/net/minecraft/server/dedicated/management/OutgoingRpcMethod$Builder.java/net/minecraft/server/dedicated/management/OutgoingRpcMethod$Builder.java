/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcMethodInfo;
import net.minecraft.server.dedicated.management.RpcRequestParameter;
import net.minecraft.server.dedicated.management.RpcResponseResult;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static class OutgoingRpcMethod.Builder<Params, Result> {
    public static final OutgoingRpcMethod.Attributes DEFAULT_ATTRIBUTES = new OutgoingRpcMethod.Attributes(true);
    private final OutgoingRpcMethod.Factory<Params, Result> factory;
    private String description = "";
    private @Nullable RpcRequestParameter<Params> requestParameter;
    private @Nullable RpcResponseResult<Result> responseResult;

    public OutgoingRpcMethod.Builder(OutgoingRpcMethod.Factory<Params, Result> factory) {
        this.factory = factory;
    }

    public OutgoingRpcMethod.Builder<Params, Result> description(String description) {
        this.description = description;
        return this;
    }

    public OutgoingRpcMethod.Builder<Params, Result> responseResult(String name, RpcSchema<Result> schema) {
        this.responseResult = new RpcResponseResult<Result>(name, schema);
        return this;
    }

    public OutgoingRpcMethod.Builder<Params, Result> requestParameter(String name, RpcSchema<Params> schema) {
        this.requestParameter = new RpcRequestParameter<Params>(name, schema);
        return this;
    }

    private OutgoingRpcMethod<Params, Result> build() {
        RpcMethodInfo<Params, Result> rpcMethodInfo = new RpcMethodInfo<Params, Result>(this.description, this.requestParameter, this.responseResult);
        return this.factory.create(rpcMethodInfo, DEFAULT_ATTRIBUTES);
    }

    public RegistryEntry.Reference<OutgoingRpcMethod<Params, Result>> buildAndRegisterVanilla(String path) {
        return this.buildAndRegister(Identifier.ofVanilla(OutgoingRpcMethod.NOTIFICATION_PREFIX + path));
    }

    private RegistryEntry.Reference<OutgoingRpcMethod<Params, Result>> buildAndRegister(Identifier id) {
        return Registry.registerReference(Registries.OUTGOING_RPC_METHOD, id, this.build());
    }
}
