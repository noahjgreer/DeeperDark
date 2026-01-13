/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.management;

import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcMethodInfo;

public record OutgoingRpcMethod.Simple(RpcMethodInfo<Void, Void> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Void, Void>
{
}
