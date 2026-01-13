/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.tree.ArgumentCommandNode;

public record SignedArgumentList.ParsedArgument<S>(ArgumentCommandNode<S, ?> node, String value) {
    public String getNodeName() {
        return this.node.getName();
    }
}
