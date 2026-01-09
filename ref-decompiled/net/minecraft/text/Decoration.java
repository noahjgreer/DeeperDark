package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.message.MessageType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public record Decoration(String translationKey, List parameters, Style style) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.STRING.fieldOf("translation_key").forGetter(Decoration::translationKey), Decoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(Decoration::parameters), Style.Codecs.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(Decoration::style)).apply(instance, Decoration::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public Decoration(String string, List list, Style style) {
      this.translationKey = string;
      this.parameters = list;
      this.style = style;
   }

   public static Decoration ofChat(String translationKey) {
      return new Decoration(translationKey, List.of(Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), Style.EMPTY);
   }

   public static Decoration ofIncomingMessage(String translationKey) {
      Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
      return new Decoration(translationKey, List.of(Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), style);
   }

   public static Decoration ofOutgoingMessage(String translationKey) {
      Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
      return new Decoration(translationKey, List.of(Decoration.Parameter.TARGET, Decoration.Parameter.CONTENT), style);
   }

   public static Decoration ofTeamMessage(String translationKey) {
      return new Decoration(translationKey, List.of(Decoration.Parameter.TARGET, Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), Style.EMPTY);
   }

   public Text apply(Text content, MessageType.Parameters params) {
      Object[] objects = this.collectArguments(content, params);
      return Text.translatable(this.translationKey, objects).fillStyle(this.style);
   }

   private Text[] collectArguments(Text content, MessageType.Parameters params) {
      Text[] texts = new Text[this.parameters.size()];

      for(int i = 0; i < texts.length; ++i) {
         Parameter parameter = (Parameter)this.parameters.get(i);
         texts[i] = parameter.apply(content, params);
      }

      return texts;
   }

   public String translationKey() {
      return this.translationKey;
   }

   public List parameters() {
      return this.parameters;
   }

   public Style style() {
      return this.style;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, Decoration::translationKey, Decoration.Parameter.PACKET_CODEC.collect(PacketCodecs.toList()), Decoration::parameters, Style.Codecs.PACKET_CODEC, Decoration::style, Decoration::new);
   }

   public static enum Parameter implements StringIdentifiable {
      SENDER(0, "sender", (content, params) -> {
         return params.name();
      }),
      TARGET(1, "target", (content, params) -> {
         return (Text)params.targetName().orElse(ScreenTexts.EMPTY);
      }),
      CONTENT(2, "content", (content, params) -> {
         return content;
      });

      private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction((parameter) -> {
         return parameter.id;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final Codec CODEC = StringIdentifiable.createCodec(Parameter::values);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(BY_ID, (parameter) -> {
         return parameter.id;
      });
      private final int id;
      private final String name;
      private final Selector selector;

      private Parameter(final int id, final String name, final Selector selector) {
         this.id = id;
         this.name = name;
         this.selector = selector;
      }

      public Text apply(Text content, MessageType.Parameters params) {
         return this.selector.select(content, params);
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Parameter[] method_43836() {
         return new Parameter[]{SENDER, TARGET, CONTENT};
      }

      public interface Selector {
         Text select(Text content, MessageType.Parameters params);
      }
   }
}
