package net.minecraft.network.codec;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PacketCodecs {
   int field_49674 = 65536;
   PacketCodec BOOLEAN = new PacketCodec() {
      public Boolean decode(ByteBuf byteBuf) {
         return byteBuf.readBoolean();
      }

      public void encode(ByteBuf byteBuf, Boolean boolean_) {
         byteBuf.writeBoolean(boolean_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Boolean)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec BYTE = new PacketCodec() {
      public Byte decode(ByteBuf byteBuf) {
         return byteBuf.readByte();
      }

      public void encode(ByteBuf byteBuf, Byte byte_) {
         byteBuf.writeByte(byte_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Byte)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec DEGREES = BYTE.xmap(MathHelper::unpackDegrees, MathHelper::packDegrees);
   PacketCodec SHORT = new PacketCodec() {
      public Short decode(ByteBuf byteBuf) {
         return byteBuf.readShort();
      }

      public void encode(ByteBuf byteBuf, Short short_) {
         byteBuf.writeShort(short_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Short)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec UNSIGNED_SHORT = new PacketCodec() {
      public Integer decode(ByteBuf byteBuf) {
         return byteBuf.readUnsignedShort();
      }

      public void encode(ByteBuf byteBuf, Integer integer) {
         byteBuf.writeShort(integer);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Integer)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec INTEGER = new PacketCodec() {
      public Integer decode(ByteBuf byteBuf) {
         return byteBuf.readInt();
      }

      public void encode(ByteBuf byteBuf, Integer integer) {
         byteBuf.writeInt(integer);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Integer)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec VAR_INT = new PacketCodec() {
      public Integer decode(ByteBuf byteBuf) {
         return VarInts.read(byteBuf);
      }

      public void encode(ByteBuf byteBuf, Integer integer) {
         VarInts.write(byteBuf, integer);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Integer)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec OPTIONAL_INT = VAR_INT.xmap((value) -> {
      return value == 0 ? OptionalInt.empty() : OptionalInt.of(value - 1);
   }, (value) -> {
      return value.isPresent() ? value.getAsInt() + 1 : 0;
   });
   PacketCodec LONG = new PacketCodec() {
      public Long decode(ByteBuf byteBuf) {
         return byteBuf.readLong();
      }

      public void encode(ByteBuf byteBuf, Long long_) {
         byteBuf.writeLong(long_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Long)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec VAR_LONG = new PacketCodec() {
      public Long decode(ByteBuf byteBuf) {
         return VarLongs.read(byteBuf);
      }

      public void encode(ByteBuf byteBuf, Long long_) {
         VarLongs.write(byteBuf, long_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Long)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec FLOAT = new PacketCodec() {
      public Float decode(ByteBuf byteBuf) {
         return byteBuf.readFloat();
      }

      public void encode(ByteBuf byteBuf, Float float_) {
         byteBuf.writeFloat(float_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Float)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec DOUBLE = new PacketCodec() {
      public Double decode(ByteBuf byteBuf) {
         return byteBuf.readDouble();
      }

      public void encode(ByteBuf byteBuf, Double double_) {
         byteBuf.writeDouble(double_);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Double)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec BYTE_ARRAY = new PacketCodec() {
      public byte[] decode(ByteBuf buf) {
         return PacketByteBuf.readByteArray(buf);
      }

      public void encode(ByteBuf buf, byte[] value) {
         PacketByteBuf.writeByteArray(buf, value);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (byte[])object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec LONG_ARRAY = new PacketCodec() {
      public long[] decode(ByteBuf buf) {
         return PacketByteBuf.readLongArray(buf);
      }

      public void encode(ByteBuf buf, long[] values) {
         PacketByteBuf.writeLongArray(buf, values);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (long[])object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec STRING = string(32767);
   PacketCodec NBT_ELEMENT = nbt(() -> {
      return NbtSizeTracker.of(2097152L);
   });
   PacketCodec UNLIMITED_NBT_ELEMENT = nbt(NbtSizeTracker::ofUnlimitedBytes);
   PacketCodec NBT_COMPOUND = nbtCompound(() -> {
      return NbtSizeTracker.of(2097152L);
   });
   PacketCodec UNLIMITED_NBT_COMPOUND = nbtCompound(NbtSizeTracker::ofUnlimitedBytes);
   PacketCodec OPTIONAL_NBT = new PacketCodec() {
      public Optional decode(ByteBuf byteBuf) {
         return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf));
      }

      public void encode(ByteBuf byteBuf, Optional optional) {
         PacketByteBuf.writeNbt(byteBuf, (NbtElement)optional.orElse((Object)null));
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Optional)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec VECTOR_3F = new PacketCodec() {
      public Vector3f decode(ByteBuf byteBuf) {
         return PacketByteBuf.readVector3f(byteBuf);
      }

      public void encode(ByteBuf byteBuf, Vector3f vector3f) {
         PacketByteBuf.writeVector3f(byteBuf, vector3f);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Vector3f)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec QUATERNION_F = new PacketCodec() {
      public Quaternionf decode(ByteBuf byteBuf) {
         return PacketByteBuf.readQuaternionf(byteBuf);
      }

      public void encode(ByteBuf byteBuf, Quaternionf quaternionf) {
         PacketByteBuf.writeQuaternionf(byteBuf, quaternionf);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Quaternionf)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec SYNC_ID = new PacketCodec() {
      public Integer decode(ByteBuf byteBuf) {
         return PacketByteBuf.readSyncId(byteBuf);
      }

      public void encode(ByteBuf byteBuf, Integer integer) {
         PacketByteBuf.writeSyncId(byteBuf, integer);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Integer)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec PROPERTY_MAP = new PacketCodec() {
      private static final int NAME_MAX_LENGTH = 64;
      private static final int VALUE_MAX_LENGTH = 32767;
      private static final int SIGNATURE_MAX_LENGTH = 1024;
      private static final int MAP_MAX_SIZE = 16;

      public PropertyMap decode(ByteBuf byteBuf) {
         int i = PacketCodecs.readCollectionSize(byteBuf, 16);
         PropertyMap propertyMap = new PropertyMap();

         for(int j = 0; j < i; ++j) {
            String string = StringEncoding.decode(byteBuf, 64);
            String string2 = StringEncoding.decode(byteBuf, 32767);
            String string3 = (String)PacketByteBuf.readNullable(byteBuf, (bufx) -> {
               return StringEncoding.decode(bufx, 1024);
            });
            Property property = new Property(string, string2, string3);
            propertyMap.put(property.name(), property);
         }

         return propertyMap;
      }

      public void encode(ByteBuf byteBuf, PropertyMap propertyMap) {
         PacketCodecs.writeCollectionSize(byteBuf, propertyMap.size(), 16);
         Iterator var3 = propertyMap.values().iterator();

         while(var3.hasNext()) {
            Property property = (Property)var3.next();
            StringEncoding.encode(byteBuf, property.name(), 64);
            StringEncoding.encode(byteBuf, property.value(), 32767);
            PacketByteBuf.writeNullable(byteBuf, property.signature(), (bufx, signature) -> {
               StringEncoding.encode(bufx, signature, 1024);
            });
         }

      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (PropertyMap)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec GAME_PROFILE = new PacketCodec() {
      public GameProfile decode(ByteBuf byteBuf) {
         UUID uUID = (UUID)Uuids.PACKET_CODEC.decode(byteBuf);
         String string = StringEncoding.decode(byteBuf, 16);
         GameProfile gameProfile = new GameProfile(uUID, string);
         gameProfile.getProperties().putAll((Multimap)PacketCodecs.PROPERTY_MAP.decode(byteBuf));
         return gameProfile;
      }

      public void encode(ByteBuf byteBuf, GameProfile gameProfile) {
         Uuids.PACKET_CODEC.encode(byteBuf, gameProfile.getId());
         StringEncoding.encode(byteBuf, gameProfile.getName(), 16);
         PacketCodecs.PROPERTY_MAP.encode(byteBuf, gameProfile.getProperties());
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (GameProfile)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };
   PacketCodec RGB = new PacketCodec() {
      public Integer decode(ByteBuf byteBuf) {
         return ColorHelper.getArgb(byteBuf.readByte() & 255, byteBuf.readByte() & 255, byteBuf.readByte() & 255);
      }

      public void encode(ByteBuf byteBuf, Integer integer) {
         byteBuf.writeByte(ColorHelper.getRed(integer));
         byteBuf.writeByte(ColorHelper.getGreen(integer));
         byteBuf.writeByte(ColorHelper.getBlue(integer));
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((ByteBuf)object, (Integer)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((ByteBuf)object);
      }
   };

   static PacketCodec byteArray(final int maxLength) {
      return new PacketCodec() {
         public byte[] decode(ByteBuf buf) {
            return PacketByteBuf.readByteArray(buf, maxLength);
         }

         public void encode(ByteBuf buf, byte[] value) {
            if (value.length > maxLength) {
               throw new EncoderException("ByteArray with size " + value.length + " is bigger than allowed " + maxLength);
            } else {
               PacketByteBuf.writeByteArray(buf, value);
            }
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (byte[])object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec string(final int maxLength) {
      return new PacketCodec() {
         public String decode(ByteBuf byteBuf) {
            return StringEncoding.decode(byteBuf, maxLength);
         }

         public void encode(ByteBuf byteBuf, String string) {
            StringEncoding.encode(byteBuf, string, maxLength);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (String)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec nbtElement(final Supplier sizeTrackerSupplier) {
      return new PacketCodec() {
         public Optional decode(ByteBuf byteBuf) {
            return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)sizeTrackerSupplier.get()));
         }

         public void encode(ByteBuf byteBuf, Optional optional) {
            PacketByteBuf.writeNbt(byteBuf, (NbtElement)optional.orElse((Object)null));
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (Optional)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec nbt(final Supplier sizeTracker) {
      return new PacketCodec() {
         public NbtElement decode(ByteBuf byteBuf) {
            NbtElement nbtElement = PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)sizeTracker.get());
            if (nbtElement == null) {
               throw new DecoderException("Expected non-null compound tag");
            } else {
               return nbtElement;
            }
         }

         public void encode(ByteBuf byteBuf, NbtElement nbtElement) {
            if (nbtElement == NbtEnd.INSTANCE) {
               throw new EncoderException("Expected non-null compound tag");
            } else {
               PacketByteBuf.writeNbt(byteBuf, nbtElement);
            }
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (NbtElement)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec nbtCompound(Supplier sizeTracker) {
      return nbt(sizeTracker).xmap((nbt) -> {
         if (nbt instanceof NbtCompound nbtCompound) {
            return nbtCompound;
         } else {
            throw new DecoderException("Not a compound tag: " + String.valueOf(nbt));
         }
      }, (nbt) -> {
         return nbt;
      });
   }

   static PacketCodec unlimitedCodec(Codec codec) {
      return codec(codec, NbtSizeTracker::ofUnlimitedBytes);
   }

   static PacketCodec codec(Codec codec) {
      return codec(codec, () -> {
         return NbtSizeTracker.of(2097152L);
      });
   }

   static PacketCodec.ResultFunction fromCodec(DynamicOps ops, Codec codec) {
      return (codecx) -> {
         return new PacketCodec() {
            public Object decode(ByteBuf byteBuf) {
               Object object = codec.decode(byteBuf);
               return codec2.parse(dynamicOps, object).getOrThrow((error) -> {
                  return new DecoderException("Failed to decode: " + error + " " + String.valueOf(object));
               });
            }

            public void encode(ByteBuf byteBuf, Object object) {
               Object object2 = codec2.encodeStart(dynamicOps, object).getOrThrow((error) -> {
                  return new EncoderException("Failed to encode: " + error + " " + String.valueOf(object));
               });
               codec.encode(byteBuf, object2);
            }

            // $FF: synthetic method
            public void encode(final Object object, final Object object2) {
               this.encode((ByteBuf)object, object2);
            }

            // $FF: synthetic method
            public Object decode(final Object object) {
               return this.decode((ByteBuf)object);
            }
         };
      };
   }

   static PacketCodec codec(Codec codec, Supplier sizeTracker) {
      return nbt(sizeTracker).collect(fromCodec(NbtOps.INSTANCE, codec));
   }

   static PacketCodec unlimitedRegistryCodec(Codec codec) {
      return registryCodec(codec, NbtSizeTracker::ofUnlimitedBytes);
   }

   static PacketCodec registryCodec(Codec codec) {
      return registryCodec(codec, () -> {
         return NbtSizeTracker.of(2097152L);
      });
   }

   static PacketCodec registryCodec(final Codec codec, Supplier sizeTracker) {
      final PacketCodec packetCodec = nbt(sizeTracker);
      return new PacketCodec() {
         public Object decode(RegistryByteBuf registryByteBuf) {
            NbtElement nbtElement = (NbtElement)packetCodec.decode(registryByteBuf);
            RegistryOps registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
            return codec.parse(registryOps, nbtElement).getOrThrow((error) -> {
               return new DecoderException("Failed to decode: " + error + " " + String.valueOf(nbtElement));
            });
         }

         public void encode(RegistryByteBuf registryByteBuf, Object object) {
            RegistryOps registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
            NbtElement nbtElement = (NbtElement)codec.encodeStart(registryOps, object).getOrThrow((error) -> {
               return new EncoderException("Failed to encode: " + error + " " + String.valueOf(object));
            });
            packetCodec.encode(registryByteBuf, nbtElement);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((RegistryByteBuf)object, object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((RegistryByteBuf)object);
         }
      };
   }

   static PacketCodec optional(final PacketCodec codec) {
      return new PacketCodec() {
         public Optional decode(ByteBuf byteBuf) {
            return byteBuf.readBoolean() ? Optional.of(codec.decode(byteBuf)) : Optional.empty();
         }

         public void encode(ByteBuf byteBuf, Optional optional) {
            if (optional.isPresent()) {
               byteBuf.writeBoolean(true);
               codec.encode(byteBuf, optional.get());
            } else {
               byteBuf.writeBoolean(false);
            }

         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (Optional)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static int readCollectionSize(ByteBuf buf, int maxSize) {
      int i = VarInts.read(buf);
      if (i > maxSize) {
         throw new DecoderException("" + i + " elements exceeded max size of: " + maxSize);
      } else {
         return i;
      }
   }

   static void writeCollectionSize(ByteBuf buf, int size, int maxSize) {
      if (size > maxSize) {
         throw new EncoderException("" + size + " elements exceeded max size of: " + maxSize);
      } else {
         VarInts.write(buf, size);
      }
   }

   static PacketCodec collection(IntFunction factory, PacketCodec elementCodec) {
      return collection(factory, elementCodec, Integer.MAX_VALUE);
   }

   static PacketCodec collection(final IntFunction factory, final PacketCodec elementCodec, final int maxSize) {
      return new PacketCodec() {
         public Collection decode(ByteBuf byteBuf) {
            int i = PacketCodecs.readCollectionSize(byteBuf, maxSize);
            Collection collection = (Collection)factory.apply(Math.min(i, 65536));

            for(int j = 0; j < i; ++j) {
               collection.add(elementCodec.decode(byteBuf));
            }

            return collection;
         }

         public void encode(ByteBuf byteBuf, Collection collection) {
            PacketCodecs.writeCollectionSize(byteBuf, collection.size(), maxSize);
            Iterator var3 = collection.iterator();

            while(var3.hasNext()) {
               Object object = var3.next();
               elementCodec.encode(byteBuf, object);
            }

         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (Collection)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec.ResultFunction toCollection(IntFunction collectionFactory) {
      return (codec) -> {
         return collection(collectionFactory, codec);
      };
   }

   static PacketCodec.ResultFunction toList() {
      return (codec) -> {
         return collection(ArrayList::new, codec);
      };
   }

   static PacketCodec.ResultFunction toList(int maxLength) {
      return (codec) -> {
         return collection(ArrayList::new, codec, maxLength);
      };
   }

   static PacketCodec map(IntFunction factory, PacketCodec keyCodec, PacketCodec valueCodec) {
      return map(factory, keyCodec, valueCodec, Integer.MAX_VALUE);
   }

   static PacketCodec map(final IntFunction factory, final PacketCodec keyCodec, final PacketCodec valueCodec, final int maxSize) {
      return new PacketCodec() {
         public void encode(ByteBuf byteBuf, Map map) {
            PacketCodecs.writeCollectionSize(byteBuf, map.size(), maxSize);
            map.forEach((object, object2) -> {
               keyCodec.encode(byteBuf, object);
               valueCodec.encode(byteBuf, object2);
            });
         }

         public Map decode(ByteBuf byteBuf) {
            int i = PacketCodecs.readCollectionSize(byteBuf, maxSize);
            Map map = (Map)factory.apply(Math.min(i, 65536));

            for(int j = 0; j < i; ++j) {
               Object object = keyCodec.decode(byteBuf);
               Object object2 = valueCodec.decode(byteBuf);
               map.put(object, object2);
            }

            return map;
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (Map)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec either(final PacketCodec left, final PacketCodec right) {
      return new PacketCodec() {
         public Either decode(ByteBuf byteBuf) {
            return byteBuf.readBoolean() ? Either.left(left.decode(byteBuf)) : Either.right(right.decode(byteBuf));
         }

         public void encode(ByteBuf byteBuf, Either either) {
            either.ifLeft((value) -> {
               byteBuf.writeBoolean(true);
               left.encode(byteBuf, value);
            }).ifRight((value) -> {
               byteBuf.writeBoolean(false);
               right.encode(byteBuf, value);
            });
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (Either)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec.ResultFunction lengthPrepended(int maxSize, BiFunction bufWrapper) {
      return (codec) -> {
         return new PacketCodec() {
            public Object decode(ByteBuf byteBuf) {
               int ix = VarInts.read(byteBuf);
               if (ix > i) {
                  throw new DecoderException("Buffer size " + ix + " is larger than allowed limit of " + i);
               } else {
                  int j = byteBuf.readerIndex();
                  ByteBuf byteBuf2 = (ByteBuf)biFunction.apply(byteBuf, byteBuf.slice(j, ix));
                  byteBuf.readerIndex(j + ix);
                  return codec.decode(byteBuf2);
               }
            }

            public void encode(ByteBuf byteBuf, Object object) {
               ByteBuf byteBuf2 = (ByteBuf)biFunction.apply(byteBuf, byteBuf.alloc().buffer());

               try {
                  codec.encode(byteBuf2, object);
                  int ix = byteBuf2.readableBytes();
                  if (ix > i) {
                     throw new EncoderException("Buffer size " + ix + " is  larger than allowed limit of " + i);
                  }

                  VarInts.write(byteBuf, ix);
                  byteBuf.writeBytes(byteBuf2);
               } finally {
                  byteBuf2.release();
               }

            }

            // $FF: synthetic method
            public void encode(final Object object, final Object object2) {
               this.encode((ByteBuf)object, object2);
            }

            // $FF: synthetic method
            public Object decode(final Object object) {
               return this.decode((ByteBuf)object);
            }
         };
      };
   }

   static PacketCodec.ResultFunction lengthPrepended(int maxSize) {
      return lengthPrepended(maxSize, (byteBuf, bufToWrap) -> {
         return bufToWrap;
      });
   }

   static PacketCodec.ResultFunction lengthPrependedRegistry(int maxSize) {
      return lengthPrepended(maxSize, (registryByteBuf, byteBuf) -> {
         return new RegistryByteBuf(byteBuf, registryByteBuf.getRegistryManager());
      });
   }

   static PacketCodec indexed(final IntFunction indexToValue, final ToIntFunction valueToIndex) {
      return new PacketCodec() {
         public Object decode(ByteBuf byteBuf) {
            int i = VarInts.read(byteBuf);
            return indexToValue.apply(i);
         }

         public void encode(ByteBuf byteBuf, Object object) {
            int i = valueToIndex.applyAsInt(object);
            VarInts.write(byteBuf, i);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static PacketCodec entryOf(IndexedIterable iterable) {
      Objects.requireNonNull(iterable);
      IntFunction var10000 = iterable::getOrThrow;
      Objects.requireNonNull(iterable);
      return indexed(var10000, iterable::getRawIdOrThrow);
   }

   private static PacketCodec registry(final RegistryKey registry, final Function registryTransformer) {
      return new PacketCodec() {
         private IndexedIterable getRegistryOrThrow(RegistryByteBuf buf) {
            return (IndexedIterable)registryTransformer.apply(buf.getRegistryManager().getOrThrow(registry));
         }

         public Object decode(RegistryByteBuf registryByteBuf) {
            int i = VarInts.read(registryByteBuf);
            return this.getRegistryOrThrow(registryByteBuf).getOrThrow(i);
         }

         public void encode(RegistryByteBuf registryByteBuf, Object object) {
            int i = this.getRegistryOrThrow(registryByteBuf).getRawIdOrThrow(object);
            VarInts.write(registryByteBuf, i);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((RegistryByteBuf)object, object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((RegistryByteBuf)object);
         }
      };
   }

   static PacketCodec registryValue(RegistryKey registry) {
      return registry(registry, (registryx) -> {
         return registryx;
      });
   }

   static PacketCodec registryEntry(RegistryKey registry) {
      return registry(registry, Registry::getIndexedEntries);
   }

   static PacketCodec registryEntry(final RegistryKey registry, final PacketCodec directCodec) {
      return new PacketCodec() {
         private static final int field_61045 = 0;

         private IndexedIterable getIndexedEntries(RegistryByteBuf buf) {
            return buf.getRegistryManager().getOrThrow(registry).getIndexedEntries();
         }

         public RegistryEntry decode(RegistryByteBuf registryByteBuf) {
            int i = VarInts.read(registryByteBuf);
            return i == 0 ? RegistryEntry.of(directCodec.decode(registryByteBuf)) : (RegistryEntry)this.getIndexedEntries(registryByteBuf).getOrThrow(i - 1);
         }

         public void encode(RegistryByteBuf registryByteBuf, RegistryEntry registryEntry) {
            switch (registryEntry.getType()) {
               case REFERENCE:
                  int i = this.getIndexedEntries(registryByteBuf).getRawIdOrThrow(registryEntry);
                  VarInts.write(registryByteBuf, i + 1);
                  break;
               case DIRECT:
                  VarInts.write(registryByteBuf, 0);
                  directCodec.encode(registryByteBuf, registryEntry.value());
            }

         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((RegistryByteBuf)object, (RegistryEntry)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((RegistryByteBuf)object);
         }
      };
   }

   static PacketCodec registryEntryList(final RegistryKey registryRef) {
      return new PacketCodec() {
         private static final int field_61046 = -1;
         private final PacketCodec entryCodec = PacketCodecs.registryEntry(registryRef);

         public RegistryEntryList decode(RegistryByteBuf registryByteBuf) {
            int i = VarInts.read(registryByteBuf) - 1;
            if (i == -1) {
               Registry registry = registryByteBuf.getRegistryManager().getOrThrow(registryRef);
               return (RegistryEntryList)registry.getOptional(TagKey.of(registryRef, (Identifier)Identifier.PACKET_CODEC.decode(registryByteBuf))).orElseThrow();
            } else {
               List list = new ArrayList(Math.min(i, 65536));

               for(int j = 0; j < i; ++j) {
                  list.add((RegistryEntry)this.entryCodec.decode(registryByteBuf));
               }

               return RegistryEntryList.of((List)list);
            }
         }

         public void encode(RegistryByteBuf registryByteBuf, RegistryEntryList registryEntryList) {
            Optional optional = registryEntryList.getTagKey();
            if (optional.isPresent()) {
               VarInts.write(registryByteBuf, 0);
               Identifier.PACKET_CODEC.encode(registryByteBuf, ((TagKey)optional.get()).id());
            } else {
               VarInts.write(registryByteBuf, registryEntryList.size() + 1);
               Iterator var4 = registryEntryList.iterator();

               while(var4.hasNext()) {
                  RegistryEntry registryEntry = (RegistryEntry)var4.next();
                  this.entryCodec.encode(registryByteBuf, registryEntry);
               }
            }

         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((RegistryByteBuf)object, (RegistryEntryList)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((RegistryByteBuf)object);
         }
      };
   }

   static PacketCodec lenientJson(final int maxLength) {
      return new PacketCodec() {
         private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();

         public JsonElement decode(ByteBuf byteBuf) {
            String string = StringEncoding.decode(byteBuf, maxLength);

            try {
               return LenientJsonParser.parse(string);
            } catch (JsonSyntaxException var4) {
               throw new DecoderException("Failed to parse JSON", var4);
            }
         }

         public void encode(ByteBuf byteBuf, JsonElement jsonElement) {
            String string = GSON.toJson(jsonElement);
            StringEncoding.encode(byteBuf, string, maxLength);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (JsonElement)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }
}
