/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

static class CommandTreeS2CPacket.CommandTree<S> {
    private final CommandRegistryAccess commandRegistryAccess;
    private final CommandTreeS2CPacket.NodeFactory<S> nodeFactory;
    private final List<CommandTreeS2CPacket.CommandNodeData> nodeDatas;
    private final List<CommandNode<S>> nodes;

    CommandTreeS2CPacket.CommandTree(CommandRegistryAccess commandRegistryAccess, CommandTreeS2CPacket.NodeFactory<S> nodeFactory, List<CommandTreeS2CPacket.CommandNodeData> nodeDatas) {
        this.commandRegistryAccess = commandRegistryAccess;
        this.nodeFactory = nodeFactory;
        this.nodeDatas = nodeDatas;
        ObjectArrayList objectArrayList = new ObjectArrayList();
        objectArrayList.size(nodeDatas.size());
        this.nodes = objectArrayList;
    }

    public CommandNode<S> getNode(int index) {
        RootCommandNode commandNode2;
        CommandNode<S> commandNode = this.nodes.get(index);
        if (commandNode != null) {
            return commandNode;
        }
        CommandTreeS2CPacket.CommandNodeData commandNodeData = this.nodeDatas.get(index);
        if (commandNodeData.suggestableNode == null) {
            commandNode2 = new RootCommandNode();
        } else {
            ArgumentBuilder<S, ?> argumentBuilder = commandNodeData.suggestableNode.createArgumentBuilder(this.commandRegistryAccess, this.nodeFactory);
            if ((commandNodeData.flags & 8) != 0) {
                argumentBuilder.redirect(this.getNode(commandNodeData.redirectNodeIndex));
            }
            boolean bl = (commandNodeData.flags & 4) != 0;
            boolean bl2 = (commandNodeData.flags & 0x20) != 0;
            commandNode2 = this.nodeFactory.modifyNode(argumentBuilder, bl, bl2).build();
        }
        this.nodes.set(index, (CommandNode<S>)commandNode2);
        for (int i : commandNodeData.childNodeIndices) {
            CommandNode<S> commandNode3 = this.getNode(i);
            if (commandNode3 instanceof RootCommandNode) continue;
            commandNode2.addChild(commandNode3);
        }
        return commandNode2;
    }
}
