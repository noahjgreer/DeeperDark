package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CommandTreeS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(CommandTreeS2CPacket::write, CommandTreeS2CPacket::new);
   private static final byte NODE_TYPE_MASK = 3;
   private static final byte EXECUTABLE = 4;
   private static final byte HAS_REDIRECT = 8;
   private static final byte HAS_SUGGESTION_PROVIDER = 16;
   private static final byte REQUIRES_LEVEL = 32;
   private static final byte NODE_TYPE_ROOT = 0;
   private static final byte NODE_TYPE_LITERAL = 1;
   private static final byte NODE_TYPE_ARGUMENT = 2;
   private final int rootSize;
   private final List nodes;

   public CommandTreeS2CPacket(RootCommandNode rootIndex, CommandNodeInspector inspector) {
      Object2IntMap object2IntMap = traverse(rootIndex);
      this.nodes = collectNodes(object2IntMap, inspector);
      this.rootSize = object2IntMap.getInt(rootIndex);
   }

   private CommandTreeS2CPacket(PacketByteBuf buf) {
      this.nodes = buf.readList(CommandTreeS2CPacket::readCommandNode);
      this.rootSize = buf.readVarInt();
      validate(this.nodes);
   }

   private void write(PacketByteBuf buf) {
      buf.writeCollection(this.nodes, (buf2, node) -> {
         node.write(buf2);
      });
      buf.writeVarInt(this.rootSize);
   }

   private static void validate(List nodeDatas, BiPredicate validator) {
      IntSet intSet = new IntOpenHashSet(IntSets.fromTo(0, nodeDatas.size()));

      boolean bl;
      do {
         if (intSet.isEmpty()) {
            return;
         }

         bl = intSet.removeIf((i) -> {
            return validator.test((CommandNodeData)nodeDatas.get(i), intSet);
         });
      } while(bl);

      throw new IllegalStateException("Server sent an impossible command tree");
   }

   private static void validate(List nodeDatas) {
      validate(nodeDatas, CommandNodeData::validateRedirectNodeIndex);
      validate(nodeDatas, CommandNodeData::validateChildNodeIndices);
   }

   private static Object2IntMap traverse(RootCommandNode commandTree) {
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      Queue queue = new ArrayDeque();
      queue.add(commandTree);

      CommandNode commandNode;
      while((commandNode = (CommandNode)queue.poll()) != null) {
         if (!object2IntMap.containsKey(commandNode)) {
            int i = object2IntMap.size();
            object2IntMap.put(commandNode, i);
            queue.addAll(commandNode.getChildren());
            if (commandNode.getRedirect() != null) {
               queue.add(commandNode.getRedirect());
            }
         }
      }

      return object2IntMap;
   }

   private static List collectNodes(Object2IntMap nodeOrdinals, CommandNodeInspector inspector) {
      ObjectArrayList objectArrayList = new ObjectArrayList(nodeOrdinals.size());
      objectArrayList.size(nodeOrdinals.size());
      ObjectIterator var3 = Object2IntMaps.fastIterable(nodeOrdinals).iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry entry = (Object2IntMap.Entry)var3.next();
         objectArrayList.set(entry.getIntValue(), createNodeData((CommandNode)entry.getKey(), inspector, nodeOrdinals));
      }

      return objectArrayList;
   }

   private static CommandNodeData readCommandNode(PacketByteBuf buf) {
      byte b = buf.readByte();
      int[] is = buf.readIntArray();
      int i = (b & 8) != 0 ? buf.readVarInt() : 0;
      SuggestableNode suggestableNode = readArgumentBuilder(buf, b);
      return new CommandNodeData(suggestableNode, b, i, is);
   }

   @Nullable
   private static SuggestableNode readArgumentBuilder(PacketByteBuf buf, byte flags) {
      int i = flags & 3;
      String string;
      if (i == 2) {
         string = buf.readString();
         int j = buf.readVarInt();
         ArgumentSerializer argumentSerializer = (ArgumentSerializer)Registries.COMMAND_ARGUMENT_TYPE.get(j);
         if (argumentSerializer == null) {
            return null;
         } else {
            ArgumentSerializer.ArgumentTypeProperties argumentTypeProperties = argumentSerializer.fromPacket(buf);
            Identifier identifier = (flags & 16) != 0 ? buf.readIdentifier() : null;
            return new ArgumentNode(string, argumentTypeProperties, identifier);
         }
      } else if (i == 1) {
         string = buf.readString();
         return new LiteralNode(string);
      } else {
         return null;
      }
   }

   private static CommandNodeData createNodeData(CommandNode node, CommandNodeInspector inspector, Object2IntMap nodeOrdinals) {
      int i = 0;
      int j;
      if (node.getRedirect() != null) {
         i |= 8;
         j = nodeOrdinals.getInt(node.getRedirect());
      } else {
         j = 0;
      }

      if (inspector.isExecutable(node)) {
         i |= 4;
      }

      if (inspector.hasRequiredLevel(node)) {
         i |= 32;
      }

      Objects.requireNonNull(node);
      byte var7 = 0;
      Object suggestableNode;
      switch (node.typeSwitch<invokedynamic>(node, var7)) {
         case 0:
            RootCommandNode rootCommandNode = (RootCommandNode)node;
            i |= 0;
            suggestableNode = null;
            break;
         case 1:
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)node;
            Identifier identifier = inspector.getSuggestionProviderId(argumentCommandNode);
            suggestableNode = new ArgumentNode(argumentCommandNode.getName(), ArgumentTypes.getArgumentTypeProperties(argumentCommandNode.getType()), identifier);
            i |= 2;
            if (identifier != null) {
               i |= 16;
            }
            break;
         case 2:
            LiteralCommandNode literalCommandNode = (LiteralCommandNode)node;
            suggestableNode = new LiteralNode(literalCommandNode.getLiteral());
            i |= 1;
            break;
         default:
            throw new UnsupportedOperationException("Unknown node type " + String.valueOf(node));
      }

      Stream var10000 = node.getChildren().stream();
      Objects.requireNonNull(nodeOrdinals);
      int[] is = var10000.mapToInt(nodeOrdinals::getInt).toArray();
      return new CommandNodeData((SuggestableNode)suggestableNode, i, j, is);
   }

   public PacketType getPacketType() {
      return PlayPackets.COMMANDS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onCommandTree(this);
   }

   public RootCommandNode getCommandTree(CommandRegistryAccess commandRegistryAccess, NodeFactory nodeFactory) {
      return (RootCommandNode)(new CommandTree(commandRegistryAccess, nodeFactory, this.nodes)).getNode(this.rootSize);
   }

   public interface CommandNodeInspector {
      @Nullable
      Identifier getSuggestionProviderId(ArgumentCommandNode node);

      boolean isExecutable(CommandNode node);

      boolean hasRequiredLevel(CommandNode node);
   }

   private static record CommandNodeData(@Nullable SuggestableNode suggestableNode, int flags, int redirectNodeIndex, int[] childNodeIndices) {
      @Nullable
      final SuggestableNode suggestableNode;
      final int flags;
      final int redirectNodeIndex;
      final int[] childNodeIndices;

      CommandNodeData(@Nullable SuggestableNode suggestableNode, int flags, int redirectNodeIndex, int[] childNodeIndices) {
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
         } else {
            return true;
         }
      }

      public boolean validateChildNodeIndices(IntSet indices) {
         int[] var2 = this.childNodeIndices;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int i = var2[var4];
            if (indices.contains(i)) {
               return false;
            }
         }

         return true;
      }

      @Nullable
      public SuggestableNode suggestableNode() {
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

   private interface SuggestableNode {
      ArgumentBuilder createArgumentBuilder(CommandRegistryAccess commandRegistryAccess, NodeFactory nodeFactory);

      void write(PacketByteBuf buf);
   }

   private static record ArgumentNode(String name, ArgumentSerializer.ArgumentTypeProperties properties, @Nullable Identifier id) implements SuggestableNode {
      ArgumentNode(String string, ArgumentSerializer.ArgumentTypeProperties argumentTypeProperties, @Nullable Identifier identifier) {
         this.name = string;
         this.properties = argumentTypeProperties;
         this.id = identifier;
      }

      public ArgumentBuilder createArgumentBuilder(CommandRegistryAccess commandRegistryAccess, NodeFactory nodeFactory) {
         ArgumentType argumentType = this.properties.createType(commandRegistryAccess);
         return nodeFactory.argument(this.name, argumentType, this.id);
      }

      public void write(PacketByteBuf buf) {
         buf.writeString(this.name);
         write(buf, this.properties);
         if (this.id != null) {
            buf.writeIdentifier(this.id);
         }

      }

      private static void write(PacketByteBuf buf, ArgumentSerializer.ArgumentTypeProperties properties) {
         write(buf, properties.getSerializer(), properties);
      }

      private static void write(PacketByteBuf buf, ArgumentSerializer serializer, ArgumentSerializer.ArgumentTypeProperties properties) {
         buf.writeVarInt(Registries.COMMAND_ARGUMENT_TYPE.getRawId(serializer));
         serializer.writePacket(properties, buf);
      }

      public String name() {
         return this.name;
      }

      public ArgumentSerializer.ArgumentTypeProperties properties() {
         return this.properties;
      }

      @Nullable
      public Identifier id() {
         return this.id;
      }
   }

   private static record LiteralNode(String literal) implements SuggestableNode {
      LiteralNode(String literal) {
         this.literal = literal;
      }

      public ArgumentBuilder createArgumentBuilder(CommandRegistryAccess commandRegistryAccess, NodeFactory nodeFactory) {
         return nodeFactory.literal(this.literal);
      }

      public void write(PacketByteBuf buf) {
         buf.writeString(this.literal);
      }

      public String literal() {
         return this.literal;
      }
   }

   private static class CommandTree {
      private final CommandRegistryAccess commandRegistryAccess;
      private final NodeFactory nodeFactory;
      private final List nodeDatas;
      private final List nodes;

      CommandTree(CommandRegistryAccess commandRegistryAccess, NodeFactory nodeFactory, List nodeDatas) {
         this.commandRegistryAccess = commandRegistryAccess;
         this.nodeFactory = nodeFactory;
         this.nodeDatas = nodeDatas;
         ObjectArrayList objectArrayList = new ObjectArrayList();
         objectArrayList.size(nodeDatas.size());
         this.nodes = objectArrayList;
      }

      public CommandNode getNode(int index) {
         CommandNode commandNode = (CommandNode)this.nodes.get(index);
         if (commandNode != null) {
            return commandNode;
         } else {
            CommandNodeData commandNodeData = (CommandNodeData)this.nodeDatas.get(index);
            Object commandNode2;
            if (commandNodeData.suggestableNode == null) {
               commandNode2 = new RootCommandNode();
            } else {
               ArgumentBuilder argumentBuilder = commandNodeData.suggestableNode.createArgumentBuilder(this.commandRegistryAccess, this.nodeFactory);
               if ((commandNodeData.flags & 8) != 0) {
                  argumentBuilder.redirect(this.getNode(commandNodeData.redirectNodeIndex));
               }

               boolean bl = (commandNodeData.flags & 4) != 0;
               boolean bl2 = (commandNodeData.flags & 32) != 0;
               commandNode2 = this.nodeFactory.modifyNode(argumentBuilder, bl, bl2).build();
            }

            this.nodes.set(index, commandNode2);
            int[] var10 = commandNodeData.childNodeIndices;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               int i = var10[var12];
               CommandNode commandNode3 = this.getNode(i);
               if (!(commandNode3 instanceof RootCommandNode)) {
                  ((CommandNode)commandNode2).addChild(commandNode3);
               }
            }

            return (CommandNode)commandNode2;
         }
      }
   }

   public interface NodeFactory {
      ArgumentBuilder literal(String name);

      ArgumentBuilder argument(String name, ArgumentType type, @Nullable Identifier suggestionProviderId);

      ArgumentBuilder modifyNode(ArgumentBuilder arg, boolean disableExecution, boolean requireTrusted);
   }
}
