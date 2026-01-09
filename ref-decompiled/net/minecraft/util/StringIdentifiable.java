package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public interface StringIdentifiable {
   int CACHED_MAP_THRESHOLD = 16;

   String asString();

   static EnumCodec createCodec(Supplier enumValues) {
      return createCodec(enumValues, (id) -> {
         return id;
      });
   }

   static EnumCodec createCodec(Supplier enumValues, Function valueNameTransformer) {
      Enum[] enums = (Enum[])enumValues.get();
      Function function = createMapper(enums, valueNameTransformer);
      return new EnumCodec(enums, function);
   }

   static Codec createBasicCodec(Supplier values) {
      StringIdentifiable[] stringIdentifiables = (StringIdentifiable[])values.get();
      Function function = createMapper(stringIdentifiables, (valueName) -> {
         return valueName;
      });
      ToIntFunction toIntFunction = Util.lastIndexGetter(Arrays.asList(stringIdentifiables));
      return new BasicCodec(stringIdentifiables, function, toIntFunction);
   }

   static Function createMapper(StringIdentifiable[] values, Function valueNameTransformer) {
      if (values.length > 16) {
         Map map = (Map)Arrays.stream(values).collect(Collectors.toMap((value) -> {
            return (String)valueNameTransformer.apply(value.asString());
         }, (value) -> {
            return value;
         }));
         return (name) -> {
            return name == null ? null : (StringIdentifiable)map.get(name);
         };
      } else {
         return (name) -> {
            StringIdentifiable[] var3 = values;
            int var4 = values.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               StringIdentifiable stringIdentifiable = var3[var5];
               if (((String)valueNameTransformer.apply(stringIdentifiable.asString())).equals(name)) {
                  return stringIdentifiable;
               }
            }

            return null;
         };
      }
   }

   static Keyable toKeyable(final StringIdentifiable[] values) {
      return new Keyable() {
         public Stream keys(DynamicOps ops) {
            Stream var10000 = Arrays.stream(values).map(StringIdentifiable::asString);
            Objects.requireNonNull(ops);
            return var10000.map(ops::createString);
         }
      };
   }

   public static class EnumCodec extends BasicCodec {
      private final Function idToIdentifiable;

      public EnumCodec(Enum[] values, Function idToIdentifiable) {
         super(values, idToIdentifiable, (enum_) -> {
            return ((Enum)enum_).ordinal();
         });
         this.idToIdentifiable = idToIdentifiable;
      }

      @Nullable
      public Enum byId(@Nullable String id) {
         return (Enum)this.idToIdentifiable.apply(id);
      }

      public Enum byId(@Nullable String id, Enum fallback) {
         return (Enum)Objects.requireNonNullElse(this.byId(id), fallback);
      }

      public Enum byId(@Nullable String id, Supplier fallbackSupplier) {
         return (Enum)Objects.requireNonNullElseGet(this.byId(id), fallbackSupplier);
      }
   }

   public static class BasicCodec implements Codec {
      private final Codec codec;

      public BasicCodec(StringIdentifiable[] values, Function idToIdentifiable, ToIntFunction identifiableToOrdinal) {
         this.codec = Codecs.orCompressed(Codec.stringResolver(StringIdentifiable::asString, idToIdentifiable), Codecs.rawIdChecked(identifiableToOrdinal, (ordinal) -> {
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : null;
         }, -1));
      }

      public DataResult decode(DynamicOps ops, Object input) {
         return this.codec.decode(ops, input);
      }

      public DataResult encode(StringIdentifiable stringIdentifiable, DynamicOps dynamicOps, Object object) {
         return this.codec.encode(stringIdentifiable, dynamicOps, object);
      }

      // $FF: synthetic method
      public DataResult encode(final Object input, final DynamicOps ops, final Object prefix) {
         return this.encode((StringIdentifiable)input, ops, prefix);
      }
   }
}
