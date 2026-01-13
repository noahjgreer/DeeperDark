/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static interface CommandTreeS2CPacket.CommandNodeInspector<S> {
    public @Nullable Identifier getSuggestionProviderId(ArgumentCommandNode<S, ?> var1);

    public boolean isExecutable(CommandNode<S> var1);

    public boolean hasRequiredLevel(CommandNode<S> var1);
}
