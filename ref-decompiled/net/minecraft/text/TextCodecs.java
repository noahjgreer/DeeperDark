package net.minecraft.text;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public class TextCodecs {
   public static final Codec CODEC = Codec.recursive("Component", TextCodecs::createCodec);
   public static final PacketCodec REGISTRY_PACKET_CODEC;
   public static final PacketCodec OPTIONAL_PACKET_CODEC;
   public static final PacketCodec UNLIMITED_REGISTRY_PACKET_CODEC;
   public static final PacketCodec OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC;
   public static final PacketCodec PACKET_CODEC;

   public static Codec withJsonLengthLimit(final int maxLength) {
      return new Codec() {
         public DataResult decode(DynamicOps ops, Object value) {
            return TextCodecs.CODEC.decode(ops, value).flatMap((pair) -> {
               return this.isTooLarge(ops, (Text)pair.getFirst()) ? DataResult.error(() -> {
                  return "Component was too large: greater than max size " + maxLength;
               }) : DataResult.success(pair);
            });
         }

         public DataResult encode(Text text, DynamicOps dynamicOps, Object object) {
            return TextCodecs.CODEC.encodeStart(dynamicOps, text);
         }

         private boolean isTooLarge(DynamicOps ops, Text text) {
            DataResult dataResult = TextCodecs.CODEC.encodeStart(toJsonOps(ops), text);
            return dataResult.isSuccess() && JsonHelper.isTooLarge((JsonElement)dataResult.getOrThrow(), maxLength);
         }

         private static DynamicOps toJsonOps(DynamicOps ops) {
            if (ops instanceof RegistryOps registryOps) {
               return registryOps.withDelegate(JsonOps.INSTANCE);
            } else {
               return JsonOps.INSTANCE;
            }
         }

         // $FF: synthetic method
         public DataResult encode(final Object input, final DynamicOps ops, final Object prefix) {
            return this.encode((Text)input, ops, prefix);
         }
      };
   }

   private static MutableText combine(List texts) {
      MutableText mutableText = ((Text)texts.get(0)).copy();

      for(int i = 1; i < texts.size(); ++i) {
         mutableText.append((Text)texts.get(i));
      }

      return mutableText;
   }

   public static MapCodec dispatchingCodec(StringIdentifiable[] types, Function typeToCodec, Function valueToType, String dispatchingKey) {
      MapCodec mapCodec = new FuzzyCodec(Stream.of(types).map(typeToCodec).toList(), (object) -> {
         return (MapEncoder)typeToCodec.apply((StringIdentifiable)valueToType.apply(object));
      });
      Codec codec = StringIdentifiable.createBasicCodec(() -> {
         return types;
      });
      MapCodec mapCodec2 = codec.dispatchMap(dispatchingKey, valueToType, typeToCodec);
      MapCodec mapCodec3 = new DispatchingCodec(dispatchingKey, mapCodec2, mapCodec);
      return Codecs.orCompressed((MapCodec)mapCodec3, (MapCodec)mapCodec2);
   }

   private static Codec createCodec(Codec selfCodec) {
      TextContent.Type[] types = new TextContent.Type[]{PlainTextContent.TYPE, TranslatableTextContent.TYPE, KeybindTextContent.TYPE, ScoreTextContent.TYPE, SelectorTextContent.TYPE, NbtTextContent.TYPE};
      MapCodec mapCodec = dispatchingCodec(types, TextContent.Type::codec, TextContent::getType, "type");
      Codec codec = RecordCodecBuilder.create((instance) -> {
         return instance.group(mapCodec.forGetter(Text::getContent), Codecs.nonEmptyList(selfCodec.listOf()).optionalFieldOf("extra", List.of()).forGetter(Text::getSiblings), Style.Codecs.MAP_CODEC.forGetter(Text::getStyle)).apply(instance, MutableText::new);
      });
      return Codec.either(Codec.either(Codec.STRING, Codecs.nonEmptyList(selfCodec.listOf())), codec).xmap((either) -> {
         return (Text)either.map((either2) -> {
            return (Text)either2.map(Text::literal, TextCodecs::combine);
         }, (text) -> {
            return text;
         });
      }, (text) -> {
         String string = text.getLiteralString();
         return string != null ? Either.left(Either.left(string)) : Either.right(text);
      });
   }

   static {
      REGISTRY_PACKET_CODEC = PacketCodecs.registryCodec(CODEC);
      OPTIONAL_PACKET_CODEC = REGISTRY_PACKET_CODEC.collect(PacketCodecs::optional);
      UNLIMITED_REGISTRY_PACKET_CODEC = PacketCodecs.unlimitedRegistryCodec(CODEC);
      OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC = UNLIMITED_REGISTRY_PACKET_CODEC.collect(PacketCodecs::optional);
      PACKET_CODEC = PacketCodecs.unlimitedCodec(CODEC);
   }

   static class FuzzyCodec extends MapCodec {
      private final List codecs;
      private final Function codecGetter;

      public FuzzyCodec(List codecs, Function codecGetter) {
         this.codecs = codecs;
         this.codecGetter = codecGetter;
      }

      public DataResult decode(DynamicOps ops, MapLike input) {
         Iterator var3 = this.codecs.iterator();

         DataResult dataResult;
         do {
            if (!var3.hasNext()) {
               return DataResult.error(() -> {
                  return "No matching codec found";
               });
            }

            MapDecoder mapDecoder = (MapDecoder)var3.next();
            dataResult = mapDecoder.decode(ops, input);
         } while(!dataResult.result().isPresent());

         return dataResult;
      }

      public RecordBuilder encode(Object input, DynamicOps ops, RecordBuilder prefix) {
         MapEncoder mapEncoder = (MapEncoder)this.codecGetter.apply(input);
         return mapEncoder.encode(input, ops, prefix);
      }

      public Stream keys(DynamicOps ops) {
         return this.codecs.stream().flatMap((codec) -> {
            return codec.keys(ops);
         }).distinct();
      }

      public String toString() {
         return "FuzzyCodec[" + String.valueOf(this.codecs) + "]";
      }
   }

   static class DispatchingCodec extends MapCodec {
      private final String dispatchingKey;
      private final MapCodec withKeyCodec;
      private final MapCodec withoutKeyCodec;

      public DispatchingCodec(String dispatchingKey, MapCodec withKeyCodec, MapCodec withoutKeyCodec) {
         this.dispatchingKey = dispatchingKey;
         this.withKeyCodec = withKeyCodec;
         this.withoutKeyCodec = withoutKeyCodec;
      }

      public DataResult decode(DynamicOps ops, MapLike input) {
         return input.get(this.dispatchingKey) != null ? this.withKeyCodec.decode(ops, input) : this.withoutKeyCodec.decode(ops, input);
      }

      public RecordBuilder encode(Object input, DynamicOps ops, RecordBuilder prefix) {
         return this.withoutKeyCodec.encode(input, ops, prefix);
      }

      public Stream keys(DynamicOps ops) {
         return Stream.concat(this.withKeyCodec.keys(ops), this.withoutKeyCodec.keys(ops)).distinct();
      }
   }
}
