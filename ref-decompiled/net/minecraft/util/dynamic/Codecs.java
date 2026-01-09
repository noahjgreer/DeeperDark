package net.minecraft.util.dynamic;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.UnsignedBytes;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class Codecs {
   public static final Codec JSON_ELEMENT;
   public static final Codec BASIC_OBJECT;
   public static final Codec NBT_ELEMENT;
   public static final Codec VECTOR_2F;
   public static final Codec VECTOR_3F;
   public static final Codec VECTOR_3I;
   public static final Codec VECTOR_4F;
   public static final Codec QUATERNION_F;
   public static final Codec AXIS_ANGLE_4F;
   public static final Codec ROTATION;
   public static final Codec MATRIX_4F;
   public static final Codec RGB;
   public static final Codec ARGB;
   public static final Codec UNSIGNED_BYTE;
   public static final Codec NON_NEGATIVE_INT;
   public static final Codec POSITIVE_INT;
   public static final Codec NON_NEGATIVE_FLOAT;
   public static final Codec POSITIVE_FLOAT;
   public static final Codec REGULAR_EXPRESSION;
   public static final Codec INSTANT;
   public static final Codec BASE_64;
   public static final Codec ESCAPED_STRING;
   public static final Codec TAG_ENTRY_ID;
   public static final Function OPTIONAL_OF_LONG_TO_OPTIONAL_LONG;
   public static final Function OPTIONAL_LONG_TO_OPTIONAL_OF_LONG;
   public static final Codec BIT_SET;
   private static final Codec GAME_PROFILE_PROPERTY;
   public static final Codec GAME_PROFILE_PROPERTY_MAP;
   public static final Codec PLAYER_NAME;
   private static final MapCodec GAME_PROFILE;
   public static final Codec GAME_PROFILE_WITH_PROPERTIES;
   public static final Codec NON_EMPTY_STRING;
   public static final Codec CODEPOINT;
   public static final Codec IDENTIFIER_PATH;
   public static final Codec URI;
   public static final Codec CHAT_TEXT;

   public static Codec fromOps(DynamicOps ops) {
      return Codec.PASSTHROUGH.xmap((dynamic) -> {
         return dynamic.convert(ops).getValue();
      }, (object) -> {
         return new Dynamic(ops, object);
      });
   }

   public static Codec createCodecForPairObject(Codec codec, String leftFieldName, String rightFieldName, BiFunction combineFunction, Function leftFunction, Function rightFunction) {
      Codec codec2 = Codec.list(codec).comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 2).flatMap((listx) -> {
            Object object = listx.get(0);
            Object object2 = listx.get(1);
            return (DataResult)combineFunction.apply(object, object2);
         });
      }, (pair) -> {
         return ImmutableList.of(leftFunction.apply(pair), rightFunction.apply(pair));
      });
      Codec codec3 = RecordCodecBuilder.create((instance) -> {
         return instance.group(codec.fieldOf(leftFieldName).forGetter(Pair::getFirst), codec.fieldOf(rightFieldName).forGetter(Pair::getSecond)).apply(instance, Pair::of);
      }).comapFlatMap((pair) -> {
         return (DataResult)combineFunction.apply(pair.getFirst(), pair.getSecond());
      }, (pair) -> {
         return Pair.of(leftFunction.apply(pair), rightFunction.apply(pair));
      });
      Codec codec4 = Codec.withAlternative(codec2, codec3);
      return Codec.either(codec, codec4).comapFlatMap((either) -> {
         return (DataResult)either.map((object) -> {
            return (DataResult)combineFunction.apply(object, object);
         }, DataResult::success);
      }, (pair) -> {
         Object object = leftFunction.apply(pair);
         Object object2 = rightFunction.apply(pair);
         return Objects.equals(object, object2) ? Either.left(object) : Either.right(pair);
      });
   }

   public static Codec.ResultFunction orElsePartial(final Object object) {
      return new Codec.ResultFunction() {
         public DataResult apply(DynamicOps ops, Object input, DataResult result) {
            MutableObject mutableObject = new MutableObject();
            Objects.requireNonNull(mutableObject);
            Optional optional = result.resultOrPartial(mutableObject::setValue);
            return optional.isPresent() ? result : DataResult.error(() -> {
               return "(" + (String)mutableObject.getValue() + " -> using default)";
            }, Pair.of(object, input));
         }

         public DataResult coApply(DynamicOps ops, Object input, DataResult result) {
            return result;
         }

         public String toString() {
            return "OrElsePartial[" + String.valueOf(object) + "]";
         }
      };
   }

   public static Codec rawIdChecked(ToIntFunction elementToRawId, IntFunction rawIdToElement, int errorRawId) {
      return Codec.INT.flatXmap((rawId) -> {
         return (DataResult)Optional.ofNullable(rawIdToElement.apply(rawId)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown element id: " + rawId;
            });
         });
      }, (element) -> {
         int j = elementToRawId.applyAsInt(element);
         return j == errorRawId ? DataResult.error(() -> {
            return "Element with unknown id: " + String.valueOf(element);
         }) : DataResult.success(j);
      });
   }

   public static Codec idChecked(Codec idCodec, Function idToElement, Function elementToId) {
      return idCodec.flatXmap((id) -> {
         Object object = idToElement.apply(id);
         return object == null ? DataResult.error(() -> {
            return "Unknown element id: " + String.valueOf(id);
         }) : DataResult.success(object);
      }, (element) -> {
         Object object = elementToId.apply(element);
         return object == null ? DataResult.error(() -> {
            return "Element with unknown id: " + String.valueOf(element);
         }) : DataResult.success(object);
      });
   }

   public static Codec orCompressed(final Codec uncompressedCodec, final Codec compressedCodec) {
      return new Codec() {
         public DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return ops.compressMaps() ? compressedCodec.encode(input, ops, prefix) : uncompressedCodec.encode(input, ops, prefix);
         }

         public DataResult decode(DynamicOps ops, Object input) {
            return ops.compressMaps() ? compressedCodec.decode(ops, input) : uncompressedCodec.decode(ops, input);
         }

         public String toString() {
            String var10000 = String.valueOf(uncompressedCodec);
            return var10000 + " orCompressed " + String.valueOf(compressedCodec);
         }
      };
   }

   public static MapCodec orCompressed(final MapCodec uncompressedCodec, final MapCodec compressedCodec) {
      return new MapCodec() {
         public RecordBuilder encode(Object input, DynamicOps ops, RecordBuilder prefix) {
            return ops.compressMaps() ? compressedCodec.encode(input, ops, prefix) : uncompressedCodec.encode(input, ops, prefix);
         }

         public DataResult decode(DynamicOps ops, MapLike input) {
            return ops.compressMaps() ? compressedCodec.decode(ops, input) : uncompressedCodec.decode(ops, input);
         }

         public Stream keys(DynamicOps ops) {
            return compressedCodec.keys(ops);
         }

         public String toString() {
            String var10000 = String.valueOf(uncompressedCodec);
            return var10000 + " orCompressed " + String.valueOf(compressedCodec);
         }
      };
   }

   public static Codec withLifecycle(Codec originalCodec, final Function entryLifecycleGetter, final Function lifecycleGetter) {
      return originalCodec.mapResult(new Codec.ResultFunction() {
         public DataResult apply(DynamicOps ops, Object input, DataResult result) {
            return (DataResult)result.result().map((pair) -> {
               return result.setLifecycle((Lifecycle)entryLifecycleGetter.apply(pair.getFirst()));
            }).orElse(result);
         }

         public DataResult coApply(DynamicOps ops, Object input, DataResult result) {
            return result.setLifecycle((Lifecycle)lifecycleGetter.apply(input));
         }

         public String toString() {
            String var10000 = String.valueOf(entryLifecycleGetter);
            return "WithLifecycle[" + var10000 + " " + String.valueOf(lifecycleGetter) + "]";
         }
      });
   }

   public static Codec withLifecycle(Codec originalCodec, Function lifecycleGetter) {
      return withLifecycle(originalCodec, lifecycleGetter, lifecycleGetter);
   }

   public static StrictUnboundedMapCodec strictUnboundedMap(Codec keyCodec, Codec elementCodec) {
      return new StrictUnboundedMapCodec(keyCodec, elementCodec);
   }

   public static Codec listOrSingle(Codec entryCodec) {
      return listOrSingle(entryCodec, entryCodec.listOf());
   }

   public static Codec listOrSingle(Codec entryCodec, Codec listCodec) {
      return Codec.either(listCodec, entryCodec).xmap((either) -> {
         return (List)either.map((list) -> {
            return list;
         }, List::of);
      }, (list) -> {
         return list.size() == 1 ? Either.right(list.getFirst()) : Either.left(list);
      });
   }

   private static Codec rangedInt(int min, int max, Function messageFactory) {
      return Codec.INT.validate((value) -> {
         return value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> {
            return (String)messageFactory.apply(value);
         });
      });
   }

   public static Codec rangedInt(int min, int max) {
      return rangedInt(min, max, (value) -> {
         return "Value must be within range [" + min + ";" + max + "]: " + value;
      });
   }

   private static Codec rangedInclusiveFloat(float minInclusive, float maxInclusive, Function messageFactory) {
      return Codec.FLOAT.validate((value) -> {
         return value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> {
            return (String)messageFactory.apply(value);
         });
      });
   }

   private static Codec rangedFloat(float minExclusive, float maxInclusive, Function messageFactory) {
      return Codec.FLOAT.validate((value) -> {
         return value.compareTo(minExclusive) > 0 && value.compareTo(maxInclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> {
            return (String)messageFactory.apply(value);
         });
      });
   }

   public static Codec rangedInclusiveFloat(float minInclusive, float maxInclusive) {
      return rangedInclusiveFloat(minInclusive, maxInclusive, (value) -> {
         return "Value must be within range [" + minInclusive + ";" + maxInclusive + "]: " + value;
      });
   }

   public static Codec nonEmptyList(Codec originalCodec) {
      return originalCodec.validate((list) -> {
         return list.isEmpty() ? DataResult.error(() -> {
            return "List must have contents";
         }) : DataResult.success(list);
      });
   }

   public static Codec nonEmptyEntryList(Codec originalCodec) {
      return originalCodec.validate((entryList) -> {
         return entryList.getStorage().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> {
            return "List must have contents";
         }) : DataResult.success(entryList);
      });
   }

   public static Codec nonEmptyMap(Codec originalCodec) {
      return originalCodec.validate((map) -> {
         return map.isEmpty() ? DataResult.error(() -> {
            return "Map must have contents";
         }) : DataResult.success(map);
      });
   }

   public static MapCodec createContextRetrievalCodec(final Function retriever) {
      class ContextRetrievalCodec extends MapCodec {
         public RecordBuilder encode(Object input, DynamicOps ops, RecordBuilder prefix) {
            return prefix;
         }

         public DataResult decode(DynamicOps ops, MapLike input) {
            return (DataResult)retriever.apply(ops);
         }

         public String toString() {
            return "ContextRetrievalCodec[" + String.valueOf(retriever) + "]";
         }

         public Stream keys(DynamicOps ops) {
            return Stream.empty();
         }
      }

      return new ContextRetrievalCodec();
   }

   public static Function createEqualTypeChecker(Function typeGetter) {
      return (collection) -> {
         Iterator iterator = collection.iterator();
         if (iterator.hasNext()) {
            Object object = typeGetter.apply(iterator.next());

            while(iterator.hasNext()) {
               Object object2 = iterator.next();
               Object object3 = typeGetter.apply(object2);
               if (object3 != object) {
                  return DataResult.error(() -> {
                     String var10000 = String.valueOf(object2);
                     return "Mixed type list: element " + var10000 + " had type " + String.valueOf(object3) + ", but list is of type " + String.valueOf(object);
                  });
               }
            }
         }

         return DataResult.success(collection, Lifecycle.stable());
      };
   }

   public static Codec exceptionCatching(final Codec codec) {
      return Codec.of(codec, new Decoder() {
         public DataResult decode(DynamicOps ops, Object input) {
            try {
               return codec.decode(ops, input);
            } catch (Exception var4) {
               return DataResult.error(() -> {
                  String var10000 = String.valueOf(input);
                  return "Caught exception decoding " + var10000 + ": " + var4.getMessage();
               });
            }
         }
      });
   }

   public static Codec formattedTime(DateTimeFormatter formatter) {
      PrimitiveCodec var10000 = Codec.STRING;
      Function var10001 = (string) -> {
         try {
            return DataResult.success(formatter.parse(string));
         } catch (Exception var3) {
            Objects.requireNonNull(var3);
            return DataResult.error(var3::getMessage);
         }
      };
      Objects.requireNonNull(formatter);
      return var10000.comapFlatMap(var10001, formatter::format);
   }

   public static MapCodec optionalLong(MapCodec codec) {
      return codec.xmap(OPTIONAL_OF_LONG_TO_OPTIONAL_LONG, OPTIONAL_LONG_TO_OPTIONAL_OF_LONG);
   }

   public static Codec map(Codec codec, int maxLength) {
      return codec.validate((map) -> {
         return map.size() > maxLength ? DataResult.error(() -> {
            int var10000 = map.size();
            return "Map is too long: " + var10000 + ", expected range [0-" + maxLength + "]";
         }) : DataResult.success(map);
      });
   }

   public static Codec object2BooleanMap(Codec keyCodec) {
      return Codec.unboundedMap(keyCodec, Codec.BOOL).xmap(Object2BooleanOpenHashMap::new, Object2ObjectOpenHashMap::new);
   }

   /** @deprecated */
   @Deprecated
   public static MapCodec parameters(final String typeKey, final String parametersKey, final Codec typeCodec, final Function typeGetter, final Function parametersCodecGetter) {
      return new MapCodec() {
         public Stream keys(DynamicOps ops) {
            return Stream.of(ops.createString(typeKey), ops.createString(parametersKey));
         }

         public DataResult decode(DynamicOps ops, MapLike input) {
            Object object = input.get(typeKey);
            return object == null ? DataResult.error(() -> {
               return "Missing \"" + typeKey + "\" in: " + String.valueOf(input);
            }) : typeCodec.decode(ops, object).flatMap((pair) -> {
               Object var10000 = input.get(parametersKey);
               Objects.requireNonNull(ops);
               Object object = Objects.requireNonNullElseGet(var10000, ops::emptyMap);
               return ((Codec)parametersCodecGetter.apply(pair.getFirst())).decode(ops, object).map(Pair::getFirst);
            });
         }

         public RecordBuilder encode(Object input, DynamicOps ops, RecordBuilder prefix) {
            Object object = typeGetter.apply(input);
            prefix.add(typeKey, typeCodec.encodeStart(ops, object));
            DataResult dataResult = this.encode((Codec)parametersCodecGetter.apply(object), input, ops);
            if (dataResult.result().isEmpty() || !Objects.equals(dataResult.result().get(), ops.emptyMap())) {
               prefix.add(parametersKey, dataResult);
            }

            return prefix;
         }

         private DataResult encode(Codec codec, Object value, DynamicOps ops) {
            return codec.encodeStart(ops, value);
         }
      };
   }

   public static Codec optional(final Codec codec) {
      return new Codec() {
         public DataResult decode(DynamicOps ops, Object input) {
            return isEmpty(ops, input) ? DataResult.success(Pair.of(Optional.empty(), input)) : codec.decode(ops, input).map((pair) -> {
               return pair.mapFirst(Optional::of);
            });
         }

         private static boolean isEmpty(DynamicOps ops, Object input) {
            Optional optional = ops.getMap(input).result();
            return optional.isPresent() && ((MapLike)optional.get()).entries().findAny().isEmpty();
         }

         public DataResult encode(Optional optional, DynamicOps dynamicOps, Object object) {
            return optional.isEmpty() ? DataResult.success(dynamicOps.emptyMap()) : codec.encode(optional.get(), dynamicOps, object);
         }

         // $FF: synthetic method
         public DataResult encode(final Object input, final DynamicOps ops, final Object prefix) {
            return this.encode((Optional)input, ops, prefix);
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static Codec enumByName(Function valueOf) {
      return Codec.STRING.comapFlatMap((id) -> {
         try {
            return DataResult.success((Enum)valueOf.apply(id));
         } catch (IllegalArgumentException var3) {
            return DataResult.error(() -> {
               return "No value with id: " + id;
            });
         }
      }, Enum::toString);
   }

   static {
      JSON_ELEMENT = fromOps(JsonOps.INSTANCE);
      BASIC_OBJECT = fromOps(JavaOps.INSTANCE);
      NBT_ELEMENT = fromOps(NbtOps.INSTANCE);
      VECTOR_2F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 2).map((listx) -> {
            return new Vector2f((Float)listx.get(0), (Float)listx.get(1));
         });
      }, (vec2f) -> {
         return List.of(vec2f.x(), vec2f.y());
      });
      VECTOR_3F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 3).map((listx) -> {
            return new Vector3f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2));
         });
      }, (vec3f) -> {
         return List.of(vec3f.x(), vec3f.y(), vec3f.z());
      });
      VECTOR_3I = Codec.INT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 3).map((listx) -> {
            return new Vector3i((Integer)listx.get(0), (Integer)listx.get(1), (Integer)listx.get(2));
         });
      }, (vec3i) -> {
         return List.of(vec3i.x(), vec3i.y(), vec3i.z());
      });
      VECTOR_4F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 4).map((listx) -> {
            return new Vector4f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2), (Float)listx.get(3));
         });
      }, (vec4f) -> {
         return List.of(vec4f.x(), vec4f.y(), vec4f.z(), vec4f.w());
      });
      QUATERNION_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 4).map((listx) -> {
            return (new Quaternionf((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2), (Float)listx.get(3))).normalize();
         });
      }, (quaternion) -> {
         return List.of(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
      });
      AXIS_ANGLE_4F = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.FLOAT.fieldOf("angle").forGetter((axisAngle) -> {
            return axisAngle.angle;
         }), VECTOR_3F.fieldOf("axis").forGetter((axisAngle) -> {
            return new Vector3f(axisAngle.x, axisAngle.y, axisAngle.z);
         })).apply(instance, AxisAngle4f::new);
      });
      ROTATION = Codec.withAlternative(QUATERNION_F, AXIS_ANGLE_4F.xmap(Quaternionf::new, AxisAngle4f::new));
      MATRIX_4F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 16).map((listx) -> {
            Matrix4f matrix4f = new Matrix4f();

            for(int i = 0; i < listx.size(); ++i) {
               matrix4f.setRowColumn(i >> 2, i & 3, (Float)listx.get(i));
            }

            return matrix4f.determineProperties();
         });
      }, (matrix) -> {
         FloatList floatList = new FloatArrayList(16);

         for(int i = 0; i < 16; ++i) {
            floatList.add(matrix.getRowColumn(i >> 2, i & 3));
         }

         return floatList;
      });
      RGB = Codec.withAlternative(Codec.INT, VECTOR_3F, (vec3f) -> {
         return ColorHelper.fromFloats(1.0F, vec3f.x(), vec3f.y(), vec3f.z());
      });
      ARGB = Codec.withAlternative(Codec.INT, VECTOR_4F, (vec4f) -> {
         return ColorHelper.fromFloats(vec4f.w(), vec4f.x(), vec4f.y(), vec4f.z());
      });
      UNSIGNED_BYTE = Codec.BYTE.flatComapMap(UnsignedBytes::toInt, (value) -> {
         return value > 255 ? DataResult.error(() -> {
            return "Unsigned byte was too large: " + value + " > 255";
         }) : DataResult.success(value.byteValue());
      });
      NON_NEGATIVE_INT = rangedInt(0, Integer.MAX_VALUE, (v) -> {
         return "Value must be non-negative: " + v;
      });
      POSITIVE_INT = rangedInt(1, Integer.MAX_VALUE, (v) -> {
         return "Value must be positive: " + v;
      });
      NON_NEGATIVE_FLOAT = rangedInclusiveFloat(0.0F, Float.MAX_VALUE, (v) -> {
         return "Value must be non-negative: " + v;
      });
      POSITIVE_FLOAT = rangedFloat(0.0F, Float.MAX_VALUE, (v) -> {
         return "Value must be positive: " + v;
      });
      REGULAR_EXPRESSION = Codec.STRING.comapFlatMap((pattern) -> {
         try {
            return DataResult.success(Pattern.compile(pattern));
         } catch (PatternSyntaxException var2) {
            return DataResult.error(() -> {
               return "Invalid regex pattern '" + pattern + "': " + var2.getMessage();
            });
         }
      }, Pattern::pattern);
      INSTANT = formattedTime(DateTimeFormatter.ISO_INSTANT).xmap(Instant::from, Function.identity());
      BASE_64 = Codec.STRING.comapFlatMap((encoded) -> {
         try {
            return DataResult.success(Base64.getDecoder().decode(encoded));
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> {
               return "Malformed base64 string";
            });
         }
      }, (data) -> {
         return Base64.getEncoder().encodeToString(data);
      });
      ESCAPED_STRING = Codec.STRING.comapFlatMap((string) -> {
         return DataResult.success(StringEscapeUtils.unescapeJava(string));
      }, StringEscapeUtils::escapeJava);
      TAG_ENTRY_ID = Codec.STRING.comapFlatMap((tagEntry) -> {
         return tagEntry.startsWith("#") ? Identifier.validate(tagEntry.substring(1)).map((id) -> {
            return new TagEntryId(id, true);
         }) : Identifier.validate(tagEntry).map((id) -> {
            return new TagEntryId(id, false);
         });
      }, TagEntryId::asString);
      OPTIONAL_OF_LONG_TO_OPTIONAL_LONG = (optional) -> {
         return (OptionalLong)optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
      };
      OPTIONAL_LONG_TO_OPTIONAL_OF_LONG = (optionalLong) -> {
         return optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty();
      };
      BIT_SET = Codec.LONG_STREAM.xmap((stream) -> {
         return BitSet.valueOf(stream.toArray());
      }, (set) -> {
         return Arrays.stream(set.toLongArray());
      });
      GAME_PROFILE_PROPERTY = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("name").forGetter(Property::name), Codec.STRING.fieldOf("value").forGetter(Property::value), Codec.STRING.lenientOptionalFieldOf("signature").forGetter((property) -> {
            return Optional.ofNullable(property.signature());
         })).apply(instance, (key, value, signature) -> {
            return new Property(key, value, (String)signature.orElse((Object)null));
         });
      });
      GAME_PROFILE_PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), GAME_PROFILE_PROPERTY.listOf()).xmap((either) -> {
         PropertyMap propertyMap = new PropertyMap();
         either.ifLeft((map) -> {
            map.forEach((key, values) -> {
               Iterator var3 = values.iterator();

               while(var3.hasNext()) {
                  String string = (String)var3.next();
                  propertyMap.put(key, new Property(key, string));
               }

            });
         }).ifRight((properties) -> {
            Iterator var2 = properties.iterator();

            while(var2.hasNext()) {
               Property property = (Property)var2.next();
               propertyMap.put(property.name(), property);
            }

         });
         return propertyMap;
      }, (properties) -> {
         return Either.right(properties.values().stream().toList());
      });
      PLAYER_NAME = Codec.string(0, 16).validate((name) -> {
         return StringHelper.isValidPlayerName(name) ? DataResult.success(name) : DataResult.error(() -> {
            return "Player name contained disallowed characters: '" + name + "'";
         });
      });
      GAME_PROFILE = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Uuids.CODEC.fieldOf("id").forGetter(GameProfile::getId), PLAYER_NAME.fieldOf("name").forGetter(GameProfile::getName)).apply(instance, GameProfile::new);
      });
      GAME_PROFILE_WITH_PROPERTIES = RecordCodecBuilder.create((instance) -> {
         return instance.group(GAME_PROFILE.forGetter(Function.identity()), GAME_PROFILE_PROPERTY_MAP.lenientOptionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)).apply(instance, (profile, properties) -> {
            properties.forEach((key, property) -> {
               profile.getProperties().put(key, property);
            });
            return profile;
         });
      });
      NON_EMPTY_STRING = Codec.STRING.validate((string) -> {
         return string.isEmpty() ? DataResult.error(() -> {
            return "Expected non-empty string";
         }) : DataResult.success(string);
      });
      CODEPOINT = Codec.STRING.comapFlatMap((string) -> {
         int[] is = string.codePoints().toArray();
         return is.length != 1 ? DataResult.error(() -> {
            return "Expected one codepoint, got: " + string;
         }) : DataResult.success(is[0]);
      }, Character::toString);
      IDENTIFIER_PATH = Codec.STRING.validate((path) -> {
         return !Identifier.isPathValid(path) ? DataResult.error(() -> {
            return "Invalid string to use as a resource path element: " + path;
         }) : DataResult.success(path);
      });
      URI = Codec.STRING.comapFlatMap((value) -> {
         try {
            return DataResult.success(Util.validateUri(value));
         } catch (URISyntaxException var2) {
            Objects.requireNonNull(var2);
            return DataResult.error(var2::getMessage);
         }
      }, URI::toString);
      CHAT_TEXT = Codec.STRING.validate((s) -> {
         for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!StringHelper.isValidChar(c)) {
               return DataResult.error(() -> {
                  return "Disallowed chat character: '" + c + "'";
               });
            }
         }

         return DataResult.success(s);
      });
   }

   public static record StrictUnboundedMapCodec(Codec keyCodec, Codec elementCodec) implements Codec, BaseMapCodec {
      public StrictUnboundedMapCodec(Codec keyCodec, Codec elementCodec) {
         this.keyCodec = keyCodec;
         this.elementCodec = elementCodec;
      }

      public DataResult decode(DynamicOps ops, MapLike input) {
         ImmutableMap.Builder builder = ImmutableMap.builder();
         Iterator var4 = input.entries().toList().iterator();

         while(var4.hasNext()) {
            Pair pair = (Pair)var4.next();
            DataResult dataResult = this.keyCodec().parse(ops, pair.getFirst());
            DataResult dataResult2 = this.elementCodec().parse(ops, pair.getSecond());
            DataResult dataResult3 = dataResult.apply2stable(Pair::of, dataResult2);
            Optional optional = dataResult3.error();
            if (optional.isPresent()) {
               String string = ((DataResult.Error)optional.get()).message();
               return DataResult.error(() -> {
                  if (dataResult.result().isPresent()) {
                     String var10000 = String.valueOf(dataResult.result().get());
                     return "Map entry '" + var10000 + "' : " + string;
                  } else {
                     return string;
                  }
               });
            }

            if (!dataResult3.result().isPresent()) {
               return DataResult.error(() -> {
                  return "Empty or invalid map contents are not allowed";
               });
            }

            Pair pair2 = (Pair)dataResult3.result().get();
            builder.put(pair2.getFirst(), pair2.getSecond());
         }

         Map map = builder.build();
         return DataResult.success(map);
      }

      public DataResult decode(DynamicOps ops, Object input) {
         return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap((map) -> {
            return this.decode(ops, map);
         }).map((map) -> {
            return Pair.of(map, input);
         });
      }

      public DataResult encode(Map map, DynamicOps dynamicOps, Object object) {
         return this.encode((Map)map, dynamicOps, (RecordBuilder)dynamicOps.mapBuilder()).build(object);
      }

      public String toString() {
         String var10000 = String.valueOf(this.keyCodec);
         return "StrictUnboundedMapCodec[" + var10000 + " -> " + String.valueOf(this.elementCodec) + "]";
      }

      public Codec keyCodec() {
         return this.keyCodec;
      }

      public Codec elementCodec() {
         return this.elementCodec;
      }

      // $FF: synthetic method
      public DataResult encode(final Object input, final DynamicOps ops, final Object prefix) {
         return this.encode((Map)input, ops, prefix);
      }
   }

   public static record TagEntryId(Identifier id, boolean tag) {
      public TagEntryId(Identifier identifier, boolean bl) {
         this.id = identifier;
         this.tag = bl;
      }

      public String toString() {
         return this.asString();
      }

      private String asString() {
         return this.tag ? "#" + String.valueOf(this.id) : this.id.toString();
      }

      public Identifier id() {
         return this.id;
      }

      public boolean tag() {
         return this.tag;
      }
   }

   public static class IdMapper {
      private final BiMap values = HashBiMap.create();

      public Codec getCodec(Codec idCodec) {
         BiMap biMap = this.values.inverse();
         BiMap var10001 = this.values;
         Objects.requireNonNull(var10001);
         Function var3 = var10001::get;
         Objects.requireNonNull(biMap);
         return Codecs.idChecked(idCodec, var3, biMap::get);
      }

      public IdMapper put(Object id, Object value) {
         Objects.requireNonNull(value, () -> {
            return "Value for " + String.valueOf(id) + " is null";
         });
         this.values.put(id, value);
         return this;
      }
   }
}
