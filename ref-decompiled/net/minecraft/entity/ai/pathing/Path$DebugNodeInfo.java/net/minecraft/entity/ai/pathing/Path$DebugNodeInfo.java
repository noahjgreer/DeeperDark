/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.pathing;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.network.PacketByteBuf;

public static final class Path.DebugNodeInfo
extends Record {
    private final PathNode[] openSet;
    private final PathNode[] closedSet;
    final Set<TargetPathNode> targetNodes;

    public Path.DebugNodeInfo(PathNode[] openSet, PathNode[] closedSet, Set<TargetPathNode> targetNodes) {
        this.openSet = openSet;
        this.closedSet = closedSet;
        this.targetNodes = targetNodes;
    }

    public void write(PacketByteBuf buf2) {
        buf2.writeCollection(this.targetNodes, (buf, node) -> node.write((PacketByteBuf)((Object)buf)));
        Path.write(buf2, this.openSet);
        Path.write(buf2, this.closedSet);
    }

    public static Path.DebugNodeInfo fromBuf(PacketByteBuf buf) {
        HashSet hashSet = buf.readCollection(HashSet::new, TargetPathNode::fromBuffer);
        PathNode[] pathNodes = Path.nodesFromBuf(buf);
        PathNode[] pathNodes2 = Path.nodesFromBuf(buf);
        return new Path.DebugNodeInfo(pathNodes, pathNodes2, hashSet);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Path.DebugNodeInfo.class, "openSet;closedSet;targetNodes", "openSet", "closedSet", "targetNodes"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Path.DebugNodeInfo.class, "openSet;closedSet;targetNodes", "openSet", "closedSet", "targetNodes"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Path.DebugNodeInfo.class, "openSet;closedSet;targetNodes", "openSet", "closedSet", "targetNodes"}, this, object);
    }

    public PathNode[] openSet() {
        return this.openSet;
    }

    public PathNode[] closedSet() {
        return this.closedSet;
    }

    public Set<TargetPathNode> targetNodes() {
        return this.targetNodes;
    }
}
