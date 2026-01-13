/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.PacketByteBuf;
import org.jspecify.annotations.Nullable;

static final class CommandTreeS2CPacket.CommandNodeData
extends Record {
    final  @Nullable CommandTreeS2CPacket.SuggestableNode suggestableNode;
    final int flags;
    final int redirectNodeIndex;
    final int[] childNodeIndices;

    CommandTreeS2CPacket.CommandNodeData( @Nullable CommandTreeS2CPacket.SuggestableNode suggestableNode, int flags, int redirectNodeIndex, int[] childNodeIndices) {
        this.suggestableNode = suggestableNode;
        this.flags = flags;
        this.redirectNodeIndex = redirectNodeIndex;
        this.childNodeIndices = childNodeIndices;
    }

    public void write(PacketByteBuf buf) {
        buf.writeByte(this.flags);
        buf.writeIntArray(this.childNodeIndices);
        if ((this.flags & 8) != 0) {
            buf.writeVarInt(this.redirectNodeIndex);
        }
        if (this.suggestableNode != null) {
            this.suggestableNode.write(buf);
        }
    }

    public boolean validateRedirectNodeIndex(IntSet indices) {
        if ((this.flags & 8) != 0) {
            return !indices.contains(this.redirectNodeIndex);
        }
        return true;
    }

    public boolean validateChildNodeIndices(IntSet indices) {
        for (int i : this.childNodeIndices) {
            if (!indices.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CommandTreeS2CPacket.CommandNodeData.class, "stub;flags;redirect;children", "suggestableNode", "flags", "redirectNodeIndex", "childNodeIndices"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CommandTreeS2CPacket.CommandNodeData.class, "stub;flags;redirect;children", "suggestableNode", "flags", "redirectNodeIndex", "childNodeIndices"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CommandTreeS2CPacket.CommandNodeData.class, "stub;flags;redirect;children", "suggestableNode", "flags", "redirectNodeIndex", "childNodeIndices"}, this, object);
    }

    public  @Nullable CommandTreeS2CPacket.SuggestableNode suggestableNode() {
        return this.suggestableNode;
    }

    public int flags() {
        return this.flags;
    }

    public int redirectNodeIndex() {
        return this.redirectNodeIndex;
    }

    public int[] childNodeIndices() {
        return this.childNodeIndices;
    }
}
