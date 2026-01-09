package net.minecraft.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record MessageType(Decoration chat, Decoration narration) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Decoration.CODEC.fieldOf("chat").forGetter(MessageType::chat), Decoration.CODEC.fieldOf("narration").forGetter(MessageType::narration)).apply(instance, MessageType::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;
   public static final Decoration CHAT_TEXT_DECORATION;
   public static final RegistryKey CHAT;
   public static final RegistryKey SAY_COMMAND;
   public static final RegistryKey MSG_COMMAND_INCOMING;
   public static final RegistryKey MSG_COMMAND_OUTGOING;
   public static final RegistryKey TEAM_MSG_COMMAND_INCOMING;
   public static final RegistryKey TEAM_MSG_COMMAND_OUTGOING;
   public static final RegistryKey EMOTE_COMMAND;

   public MessageType(Decoration decoration, Decoration decoration2) {
      this.chat = decoration;
      this.narration = decoration2;
   }

   private static RegistryKey register(String id) {
      return RegistryKey.of(RegistryKeys.MESSAGE_TYPE, Identifier.ofVanilla(id));
   }

   public static void bootstrap(Registerable messageTypeRegisterable) {
      messageTypeRegisterable.register(CHAT, new MessageType(CHAT_TEXT_DECORATION, Decoration.ofChat("chat.type.text.narrate")));
      messageTypeRegisterable.register(SAY_COMMAND, new MessageType(Decoration.ofChat("chat.type.announcement"), Decoration.ofChat("chat.type.text.narrate")));
      messageTypeRegisterable.register(MSG_COMMAND_INCOMING, new MessageType(Decoration.ofIncomingMessage("commands.message.display.incoming"), Decoration.ofChat("chat.type.text.narrate")));
      messageTypeRegisterable.register(MSG_COMMAND_OUTGOING, new MessageType(Decoration.ofOutgoingMessage("commands.message.display.outgoing"), Decoration.ofChat("chat.type.text.narrate")));
      messageTypeRegisterable.register(TEAM_MSG_COMMAND_INCOMING, new MessageType(Decoration.ofTeamMessage("chat.type.team.text"), Decoration.ofChat("chat.type.text.narrate")));
      messageTypeRegisterable.register(TEAM_MSG_COMMAND_OUTGOING, new MessageType(Decoration.ofTeamMessage("chat.type.team.sent"), Decoration.ofChat("chat.type.text.narrate")));
      messageTypeRegisterable.register(EMOTE_COMMAND, new MessageType(Decoration.ofChat("chat.type.emote"), Decoration.ofChat("chat.type.emote")));
   }

   public static Parameters params(RegistryKey typeKey, Entity entity) {
      return params(typeKey, entity.getWorld().getRegistryManager(), entity.getDisplayName());
   }

   public static Parameters params(RegistryKey typeKey, ServerCommandSource source) {
      return params(typeKey, source.getRegistryManager(), source.getDisplayName());
   }

   public static Parameters params(RegistryKey typeKey, DynamicRegistryManager registryManager, Text name) {
      Registry registry = registryManager.getOrThrow(RegistryKeys.MESSAGE_TYPE);
      return new Parameters(registry.getOrThrow(typeKey), name);
   }

   public Decoration chat() {
      return this.chat;
   }

   public Decoration narration() {
      return this.narration;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(Decoration.PACKET_CODEC, MessageType::chat, Decoration.PACKET_CODEC, MessageType::narration, MessageType::new);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.MESSAGE_TYPE, PACKET_CODEC);
      CHAT_TEXT_DECORATION = Decoration.ofChat("chat.type.text");
      CHAT = register("chat");
      SAY_COMMAND = register("say_command");
      MSG_COMMAND_INCOMING = register("msg_command_incoming");
      MSG_COMMAND_OUTGOING = register("msg_command_outgoing");
      TEAM_MSG_COMMAND_INCOMING = register("team_msg_command_incoming");
      TEAM_MSG_COMMAND_OUTGOING = register("team_msg_command_outgoing");
      EMOTE_COMMAND = register("emote_command");
   }

   public static record Parameters(RegistryEntry type, Text name, Optional targetName) {
      public static final PacketCodec CODEC;

      Parameters(RegistryEntry type, Text name) {
         this(type, name, Optional.empty());
      }

      public Parameters(RegistryEntry registryEntry, Text text, Optional optional) {
         this.type = registryEntry;
         this.name = text;
         this.targetName = optional;
      }

      public Text applyChatDecoration(Text content) {
         return ((MessageType)this.type.value()).chat().apply(content, this);
      }

      public Text applyNarrationDecoration(Text content) {
         return ((MessageType)this.type.value()).narration().apply(content, this);
      }

      public Parameters withTargetName(Text targetName) {
         return new Parameters(this.type, this.name, Optional.of(targetName));
      }

      public RegistryEntry type() {
         return this.type;
      }

      public Text name() {
         return this.name;
      }

      public Optional targetName() {
         return this.targetName;
      }

      static {
         CODEC = PacketCodec.tuple(MessageType.ENTRY_PACKET_CODEC, Parameters::type, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Parameters::name, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, Parameters::targetName, Parameters::new);
      }
   }
}
