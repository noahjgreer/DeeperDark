/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management;

import java.util.function.Function;
import net.minecraft.registry.Registry;
import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.RpcMethodInfo;
import net.minecraft.server.dedicated.management.RpcRequestParameter;
import net.minecraft.server.dedicated.management.RpcResponseResult;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static class IncomingRpcMethod.Builder<Params, Result> {
    private String description = "";
    private @Nullable RpcRequestParameter<Params> params;
    private @Nullable RpcResponseResult<Result> result;
    private boolean runOnMainThread = true;
    private boolean discoverable = true;
    private @Nullable IncomingRpcMethod.ParameterlessHandler<Result> parameterlessHandler;
    private @Nullable IncomingRpcMethod.ParameterizedHandler<Params, Result> parameterizedHandler;

    public IncomingRpcMethod.Builder(IncomingRpcMethod.ParameterlessHandler<Result> parameterlessHandler) {
        this.parameterlessHandler = parameterlessHandler;
    }

    public IncomingRpcMethod.Builder(IncomingRpcMethod.ParameterizedHandler<Params, Result> parameterizedHandler) {
        this.parameterizedHandler = parameterizedHandler;
    }

    public IncomingRpcMethod.Builder(Function<ManagementHandlerDispatcher, Result> parameterlessHandler) {
        this.parameterlessHandler = (dispatcher, remote) -> parameterlessHandler.apply(dispatcher);
    }

    public IncomingRpcMethod.Builder<Params, Result> description(String description) {
        this.description = description;
        return this;
    }

    public IncomingRpcMethod.Builder<Params, Result> result(String name, RpcSchema<Result> schema) {
        this.result = new RpcResponseResult<Result>(name, schema.copy());
        return this;
    }

    public IncomingRpcMethod.Builder<Params, Result> parameter(String name, RpcSchema<Params> schema) {
        this.params = new RpcRequestParameter<Params>(name, schema.copy());
        return this;
    }

    public IncomingRpcMethod.Builder<Params, Result> noRequireMainThread() {
        this.runOnMainThread = false;
        return this;
    }

    public IncomingRpcMethod.Builder<Params, Result> notDiscoverable() {
        this.discoverable = false;
        return this;
    }

    public IncomingRpcMethod<Params, Result> build() {
        if (this.result == null) {
            throw new IllegalStateException("No response defined");
        }
        IncomingRpcMethod.Attributes attributes = new IncomingRpcMethod.Attributes(this.discoverable, this.runOnMainThread);
        RpcMethodInfo<Params, Result> rpcMethodInfo = new RpcMethodInfo<Params, Result>(this.description, this.params, this.result);
        if (this.parameterlessHandler != null) {
            return new IncomingRpcMethod.Parameterless<Params, Result>(rpcMethodInfo, attributes, this.parameterlessHandler);
        }
        if (this.parameterizedHandler != null) {
            if (this.params == null) {
                throw new IllegalStateException("No param schema defined");
            }
            return new IncomingRpcMethod.Parameterized<Params, Result>(rpcMethodInfo, attributes, this.parameterizedHandler);
        }
        throw new IllegalStateException("No method defined");
    }

    public IncomingRpcMethod<?, ?> buildAndRegisterVanilla(Registry<IncomingRpcMethod<?, ?>> registry, String path) {
        return this.buildAndRegister(registry, Identifier.ofVanilla(path));
    }

    private IncomingRpcMethod<?, ?> buildAndRegister(Registry<IncomingRpcMethod<?, ?>> registry, Identifier id) {
        return Registry.register(registry, id, this.build());
    }
}
