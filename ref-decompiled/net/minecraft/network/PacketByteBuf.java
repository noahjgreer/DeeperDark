package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PacketByteBuf extends ByteBuf {
   public static final int MAX_READ_NBT_SIZE = 2097152;
   private final ByteBuf parent;
   public static final short DEFAULT_MAX_STRING_LENGTH = Short.MAX_VALUE;
   public static final int MAX_TEXT_LENGTH = 262144;
   private static final int field_39381 = 256;
   private static final int field_39382 = 256;
   private static final int field_39383 = 512;
   private static final Gson GSON = new Gson();

   public PacketByteBuf(ByteBuf parent) {
      this.parent = parent;
   }

   /** @deprecated */
   @Deprecated
   public Object decode(DynamicOps ops, Codec codec) {
      return this.decode(ops, codec, NbtSizeTracker.ofUnlimitedBytes());
   }

   /** @deprecated */
   @Deprecated
   public Object decode(DynamicOps ops, Codec codec, NbtSizeTracker sizeTracker) {
      NbtElement nbtElement = this.readNbt(sizeTracker);
      return codec.parse(ops, nbtElement).getOrThrow((error) -> {
         return new DecoderException("Failed to decode: " + error + " " + String.valueOf(nbtElement));
      });
   }

   /** @deprecated */
   @Deprecated
   public PacketByteBuf encode(DynamicOps ops, Codec codec, Object value) {
      NbtElement nbtElement = (NbtElement)codec.encodeStart(ops, value).getOrThrow((error) -> {
         return new EncoderException("Failed to encode: " + error + " " + String.valueOf(value));
      });
      this.writeNbt(nbtElement);
      return this;
   }

   public Object decodeAsJson(Codec codec) {
      JsonElement jsonElement = LenientJsonParser.parse(this.readString());
      DataResult dataResult = codec.parse(JsonOps.INSTANCE, jsonElement);
      return dataResult.getOrThrow((error) -> {
         return new DecoderException("Failed to decode JSON: " + error);
      });
   }

   public void encodeAsJson(Codec codec, Object value) {
      DataResult dataResult = codec.encodeStart(JsonOps.INSTANCE, value);
      this.writeString(GSON.toJson((JsonElement)dataResult.getOrThrow((error) -> {
         return new EncoderException("Failed to encode: " + error + " " + String.valueOf(value));
      })));
   }

   public static IntFunction getMaxValidator(IntFunction applier, int max) {
      return (value) -> {
         if (value > max) {
            throw new DecoderException("Value " + value + " is larger than limit " + max);
         } else {
            return applier.apply(value);
         }
      };
   }

   public Collection readCollection(IntFunction collectionFactory, PacketDecoder reader) {
      int i = this.readVarInt();
      Collection collection = (Collection)collectionFactory.apply(i);

      for(int j = 0; j < i; ++j) {
         collection.add(reader.decode(this));
      }

      return collection;
   }

   public void writeCollection(Collection collection, PacketEncoder writer) {
      this.writeVarInt(collection.size());
      Iterator var3 = collection.iterator();

      while(var3.hasNext()) {
         Object object = var3.next();
         writer.encode(this, object);
      }

   }

   public List readList(PacketDecoder reader) {
      return (List)this.readCollection(Lists::newArrayListWithCapacity, reader);
   }

   public IntList readIntList() {
      int i = this.readVarInt();
      IntList intList = new IntArrayList();

      for(int j = 0; j < i; ++j) {
         intList.add(this.readVarInt());
      }

      return intList;
   }

   public void writeIntList(IntList list) {
      this.writeVarInt(list.size());
      list.forEach(this::writeVarInt);
   }

   public Map readMap(IntFunction mapFactory, PacketDecoder keyReader, PacketDecoder valueReader) {
      int i = this.readVarInt();
      Map map = (Map)mapFactory.apply(i);

      for(int j = 0; j < i; ++j) {
         Object object = keyReader.decode(this);
         Object object2 = valueReader.decode(this);
         map.put(object, object2);
      }

      return map;
   }

   public Map readMap(PacketDecoder keyReader, PacketDecoder valueReader) {
      return this.readMap(Maps::newHashMapWithExpectedSize, keyReader, valueReader);
   }

   public void writeMap(Map map, PacketEncoder keyWriter, PacketEncoder valueWriter) {
      this.writeVarInt(map.size());
      map.forEach((key, value) -> {
         keyWriter.encode(this, key);
         valueWriter.encode(this, value);
      });
   }

   public void forEachInCollection(Consumer consumer) {
      int i = this.readVarInt();

      for(int j = 0; j < i; ++j) {
         consumer.accept(this);
      }

   }

   public void writeEnumSet(EnumSet enumSet, Class type) {
      Enum[] enums = (Enum[])type.getEnumConstants();
      BitSet bitSet = new BitSet(enums.length);

      for(int i = 0; i < enums.length; ++i) {
         bitSet.set(i, enumSet.contains(enums[i]));
      }

      this.writeBitSet(bitSet, enums.length);
   }

   public EnumSet readEnumSet(Class type) {
      Enum[] enums = (Enum[])type.getEnumConstants();
      BitSet bitSet = this.readBitSet(enums.length);
      EnumSet enumSet = EnumSet.noneOf(type);

      for(int i = 0; i < enums.length; ++i) {
         if (bitSet.get(i)) {
            enumSet.add(enums[i]);
         }
      }

      return enumSet;
   }

   public void writeOptional(Optional value, PacketEncoder writer) {
      if (value.isPresent()) {
         this.writeBoolean(true);
         writer.encode(this, value.get());
      } else {
         this.writeBoolean(false);
      }

   }

   public Optional readOptional(PacketDecoder reader) {
      return this.readBoolean() ? Optional.of(reader.decode(this)) : Optional.empty();
   }

   public void writeEither(Either either, PacketEncoder leftEncoder, PacketEncoder rightEncoder) {
      either.ifLeft((object) -> {
         this.writeBoolean(true);
         leftEncoder.encode(this, object);
      }).ifRight((object) -> {
         this.writeBoolean(false);
         rightEncoder.encode(this, object);
      });
   }

   public Either readEither(PacketDecoder leftDecoder, PacketDecoder rightDecoder) {
      return this.readBoolean() ? Either.left(leftDecoder.decode(this)) : Either.right(rightDecoder.decode(this));
   }

   @Nullable
   public Object readNullable(PacketDecoder reader) {
      return readNullable(this, reader);
   }

   @Nullable
   public static Object readNullable(ByteBuf buf, PacketDecoder reader) {
      return buf.readBoolean() ? reader.decode(buf) : null;
   }

   public void writeNullable(@Nullable Object value, PacketEncoder writer) {
      writeNullable(this, value, writer);
   }

   public static void writeNullable(ByteBuf buf, @Nullable Object value, PacketEncoder writer) {
      if (value != null) {
         buf.writeBoolean(true);
         writer.encode(buf, value);
      } else {
         buf.writeBoolean(false);
      }

   }

   public byte[] readByteArray() {
      return readByteArray(this);
   }

   public static byte[] readByteArray(ByteBuf buf) {
      return readByteArray(buf, buf.readableBytes());
   }

   public PacketByteBuf writeByteArray(byte[] array) {
      writeByteArray(this, array);
      return this;
   }

   public static void writeByteArray(ByteBuf buf, byte[] array) {
      VarInts.write(buf, array.length);
      buf.writeBytes(array);
   }

   public byte[] readByteArray(int maxSize) {
      return readByteArray(this, maxSize);
   }

   public static byte[] readByteArray(ByteBuf buf, int maxSize) {
      int i = VarInts.read(buf);
      if (i > maxSize) {
         throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxSize);
      } else {
         byte[] bs = new byte[i];
         buf.readBytes(bs);
         return bs;
      }
   }

   public PacketByteBuf writeIntArray(int[] array) {
      this.writeVarInt(array.length);
      int[] var2 = array;
      int var3 = array.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int i = var2[var4];
         this.writeVarInt(i);
      }

      return this;
   }

   public int[] readIntArray() {
      return this.readIntArray(this.readableBytes());
   }

   public int[] readIntArray(int maxSize) {
      int i = this.readVarInt();
      if (i > maxSize) {
         throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
      } else {
         int[] is = new int[i];

         for(int j = 0; j < is.length; ++j) {
            is[j] = this.readVarInt();
         }

         return is;
      }
   }

   public PacketByteBuf writeLongArray(long[] values) {
      writeLongArray(this, values);
      return this;
   }

   public static void writeLongArray(ByteBuf buf, long[] values) {
      VarInts.write(buf, values.length);
      writeFixedLengthLongArray(buf, values);
   }

   public PacketByteBuf writeFixedLengthLongArray(long[] values) {
      writeFixedLengthLongArray(this, values);
      return this;
   }

   public static void writeFixedLengthLongArray(ByteBuf buf, long[] values) {
      long[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long l = var2[var4];
         buf.writeLong(l);
      }

   }

   public long[] readLongArray() {
      return readLongArray(this);
   }

   public long[] readFixedLengthLongArray(long[] values) {
      return readFixedLengthLongArray(this, values);
   }

   public static long[] readLongArray(ByteBuf buf) {
      int i = VarInts.read(buf);
      int j = buf.readableBytes() / 8;
      if (i > j) {
         throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + j);
      } else {
         return readFixedLengthLongArray(buf, new long[i]);
      }
   }

   public static long[] readFixedLengthLongArray(ByteBuf buf, long[] values) {
      for(int i = 0; i < values.length; ++i) {
         values[i] = buf.readLong();
      }

      return values;
   }

   public BlockPos readBlockPos() {
      return readBlockPos(this);
   }

   public static BlockPos readBlockPos(ByteBuf buf) {
      return BlockPos.fromLong(buf.readLong());
   }

   public PacketByteBuf writeBlockPos(BlockPos pos) {
      writeBlockPos(this, pos);
      return this;
   }

   public static void writeBlockPos(ByteBuf buf, BlockPos pos) {
      buf.writeLong(pos.asLong());
   }

   public ChunkPos readChunkPos() {
      return new ChunkPos(this.readLong());
   }

   public PacketByteBuf writeChunkPos(ChunkPos pos) {
      this.writeLong(pos.toLong());
      return this;
   }

   public static ChunkPos readChunkPos(ByteBuf buf) {
      return new ChunkPos(buf.readLong());
   }

   public static void writeChunkPos(ByteBuf buf, ChunkPos pos) {
      buf.writeLong(pos.toLong());
   }

   public ChunkSectionPos readChunkSectionPos() {
      return ChunkSectionPos.from(this.readLong());
   }

   public PacketByteBuf writeChunkSectionPos(ChunkSectionPos pos) {
      this.writeLong(pos.asLong());
      return this;
   }

   public GlobalPos readGlobalPos() {
      RegistryKey registryKey = this.readRegistryKey(RegistryKeys.WORLD);
      BlockPos blockPos = this.readBlockPos();
      return GlobalPos.create(registryKey, blockPos);
   }

   public void writeGlobalPos(GlobalPos pos) {
      this.writeRegistryKey(pos.dimension());
      this.writeBlockPos(pos.pos());
   }

   public Vector3f readVector3f() {
      return readVector3f(this);
   }

   public static Vector3f readVector3f(ByteBuf buf) {
      return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
   }

   public void writeVector3f(Vector3f vector3f) {
      writeVector3f(this, vector3f);
   }

   public static void writeVector3f(ByteBuf buf, Vector3f vector) {
      buf.writeFloat(vector.x());
      buf.writeFloat(vector.y());
      buf.writeFloat(vector.z());
   }

   public Quaternionf readQuaternionf() {
      return readQuaternionf(this);
   }

   public static Quaternionf readQuaternionf(ByteBuf buf) {
      return new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
   }

   public void writeQuaternionf(Quaternionf quaternionf) {
      writeQuaternionf(this, quaternionf);
   }

   public static void writeQuaternionf(ByteBuf buf, Quaternionf quaternion) {
      buf.writeFloat(quaternion.x);
      buf.writeFloat(quaternion.y);
      buf.writeFloat(quaternion.z);
      buf.writeFloat(quaternion.w);
   }

   public static Vec3d readVec3d(ByteBuf buf) {
      return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
   }

   public Vec3d readVec3d() {
      return readVec3d(this);
   }

   public static void writeVec3d(ByteBuf buf, Vec3d vec) {
      buf.writeDouble(vec.getX());
      buf.writeDouble(vec.getY());
      buf.writeDouble(vec.getZ());
   }

   public void writeVec3d(Vec3d vec) {
      writeVec3d(this, vec);
   }

   public Enum readEnumConstant(Class enumClass) {
      return ((Enum[])enumClass.getEnumConstants())[this.readVarInt()];
   }

   public PacketByteBuf writeEnumConstant(Enum instance) {
      return this.writeVarInt(instance.ordinal());
   }

   public Object decode(IntFunction idToValue) {
      int i = this.readVarInt();
      return idToValue.apply(i);
   }

   public PacketByteBuf encode(ToIntFunction valueToId, Object value) {
      int i = valueToId.applyAsInt(value);
      return this.writeVarInt(i);
   }

   public int readVarInt() {
      return VarInts.read(this.parent);
   }

   public long readVarLong() {
      return VarLongs.read(this.parent);
   }

   public PacketByteBuf writeUuid(UUID uuid) {
      writeUuid(this, uuid);
      return this;
   }

   public static void writeUuid(ByteBuf buf, UUID uuid) {
      buf.writeLong(uuid.getMostSignificantBits());
      buf.writeLong(uuid.getLeastSignificantBits());
   }

   public UUID readUuid() {
      return readUuid(this);
   }

   public static UUID readUuid(ByteBuf buf) {
      return new UUID(buf.readLong(), buf.readLong());
   }

   public PacketByteBuf writeVarInt(int value) {
      VarInts.write(this.parent, value);
      return this;
   }

   public PacketByteBuf writeVarLong(long value) {
      VarLongs.write(this.parent, value);
      return this;
   }

   public PacketByteBuf writeNbt(@Nullable NbtElement nbt) {
      writeNbt(this, nbt);
      return this;
   }

   public static void writeNbt(ByteBuf buf, @Nullable NbtElement nbt) {
      if (nbt == null) {
         nbt = NbtEnd.INSTANCE;
      }

      try {
         NbtIo.writeForPacket((NbtElement)nbt, new ByteBufOutputStream(buf));
      } catch (IOException var3) {
         throw new EncoderException(var3);
      }
   }

   @Nullable
   public NbtCompound readNbt() {
      return readNbt((ByteBuf)this);
   }

   @Nullable
   public static NbtCompound readNbt(ByteBuf buf) {
      NbtElement nbtElement = readNbt(buf, NbtSizeTracker.of(2097152L));
      if (nbtElement != null && !(nbtElement instanceof NbtCompound)) {
         throw new DecoderException("Not a compound tag: " + String.valueOf(nbtElement));
      } else {
         return (NbtCompound)nbtElement;
      }
   }

   @Nullable
   public static NbtElement readNbt(ByteBuf buf, NbtSizeTracker sizeTracker) {
      try {
         NbtElement nbtElement = NbtIo.read(new ByteBufInputStream(buf), sizeTracker);
         return nbtElement.getType() == 0 ? null : nbtElement;
      } catch (IOException var3) {
         throw new EncoderException(var3);
      }
   }

   @Nullable
   public NbtElement readNbt(NbtSizeTracker sizeTracker) {
      return readNbt(this, sizeTracker);
   }

   public String readString() {
      return this.readString(32767);
   }

   public String readString(int maxLength) {
      return StringEncoding.decode(this.parent, maxLength);
   }

   public PacketByteBuf writeString(String string) {
      return this.writeString(string, 32767);
   }

   public PacketByteBuf writeString(String string, int maxLength) {
      StringEncoding.encode(this.parent, string, maxLength);
      return this;
   }

   public Identifier readIdentifier() {
      return Identifier.of(this.readString(32767));
   }

   public PacketByteBuf writeIdentifier(Identifier id) {
      this.writeString(id.toString());
      return this;
   }

   public RegistryKey readRegistryKey(RegistryKey registryRef) {
      Identifier identifier = this.readIdentifier();
      return RegistryKey.of(registryRef, identifier);
   }

   public void writeRegistryKey(RegistryKey key) {
      this.writeIdentifier(key.getValue());
   }

   public RegistryKey readRegistryRefKey() {
      Identifier identifier = this.readIdentifier();
      return RegistryKey.ofRegistry(identifier);
   }

   public Date readDate() {
      return new Date(this.readLong());
   }

   public PacketByteBuf writeDate(Date date) {
      this.writeLong(date.getTime());
      return this;
   }

   public Instant readInstant() {
      return Instant.ofEpochMilli(this.readLong());
   }

   public void writeInstant(Instant instant) {
      this.writeLong(instant.toEpochMilli());
   }

   public PublicKey readPublicKey() {
      try {
         return NetworkEncryptionUtils.decodeEncodedRsaPublicKey(this.readByteArray(512));
      } catch (NetworkEncryptionException var2) {
         throw new DecoderException("Malformed public key bytes", var2);
      }
   }

   public PacketByteBuf writePublicKey(PublicKey publicKey) {
      this.writeByteArray(publicKey.getEncoded());
      return this;
   }

   public BlockHitResult readBlockHitResult() {
      BlockPos blockPos = this.readBlockPos();
      Direction direction = (Direction)this.readEnumConstant(Direction.class);
      float f = this.readFloat();
      float g = this.readFloat();
      float h = this.readFloat();
      boolean bl = this.readBoolean();
      boolean bl2 = this.readBoolean();
      return new BlockHitResult(new Vec3d((double)blockPos.getX() + (double)f, (double)blockPos.getY() + (double)g, (double)blockPos.getZ() + (double)h), direction, blockPos, bl, bl2);
   }

   public void writeBlockHitResult(BlockHitResult hitResult) {
      BlockPos blockPos = hitResult.getBlockPos();
      this.writeBlockPos(blockPos);
      this.writeEnumConstant(hitResult.getSide());
      Vec3d vec3d = hitResult.getPos();
      this.writeFloat((float)(vec3d.x - (double)blockPos.getX()));
      this.writeFloat((float)(vec3d.y - (double)blockPos.getY()));
      this.writeFloat((float)(vec3d.z - (double)blockPos.getZ()));
      this.writeBoolean(hitResult.isInsideBlock());
      this.writeBoolean(hitResult.isAgainstWorldBorder());
   }

   public BitSet readBitSet() {
      return BitSet.valueOf(this.readLongArray());
   }

   public void writeBitSet(BitSet bitSet) {
      this.writeLongArray(bitSet.toLongArray());
   }

   public BitSet readBitSet(int size) {
      byte[] bs = new byte[MathHelper.ceilDiv(size, 8)];
      this.readBytes(bs);
      return BitSet.valueOf(bs);
   }

   public void writeBitSet(BitSet bitSet, int size) {
      if (bitSet.length() > size) {
         int var10002 = bitSet.length();
         throw new EncoderException("BitSet is larger than expected size (" + var10002 + ">" + size + ")");
      } else {
         byte[] bs = bitSet.toByteArray();
         this.writeBytes(Arrays.copyOf(bs, MathHelper.ceilDiv(size, 8)));
      }
   }

   public static int readSyncId(ByteBuf buf) {
      return VarInts.read(buf);
   }

   public int readSyncId() {
      return readSyncId(this.parent);
   }

   public static void writeSyncId(ByteBuf buf, int syncId) {
      VarInts.write(buf, syncId);
   }

   public void writeSyncId(int syncId) {
      writeSyncId(this.parent, syncId);
   }

   public boolean isContiguous() {
      return this.parent.isContiguous();
   }

   public int maxFastWritableBytes() {
      return this.parent.maxFastWritableBytes();
   }

   public int capacity() {
      return this.parent.capacity();
   }

   public PacketByteBuf capacity(int i) {
      this.parent.capacity(i);
      return this;
   }

   public int maxCapacity() {
      return this.parent.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.parent.alloc();
   }

   public ByteOrder order() {
      return this.parent.order();
   }

   public ByteBuf order(ByteOrder byteOrder) {
      return this.parent.order(byteOrder);
   }

   public ByteBuf unwrap() {
      return this.parent;
   }

   public boolean isDirect() {
      return this.parent.isDirect();
   }

   public boolean isReadOnly() {
      return this.parent.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.parent.asReadOnly();
   }

   public int readerIndex() {
      return this.parent.readerIndex();
   }

   public PacketByteBuf readerIndex(int i) {
      this.parent.readerIndex(i);
      return this;
   }

   public int writerIndex() {
      return this.parent.writerIndex();
   }

   public PacketByteBuf writerIndex(int i) {
      this.parent.writerIndex(i);
      return this;
   }

   public PacketByteBuf setIndex(int i, int j) {
      this.parent.setIndex(i, j);
      return this;
   }

   public int readableBytes() {
      return this.parent.readableBytes();
   }

   public int writableBytes() {
      return this.parent.writableBytes();
   }

   public int maxWritableBytes() {
      return this.parent.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.parent.isReadable();
   }

   public boolean isReadable(int size) {
      return this.parent.isReadable(size);
   }

   public boolean isWritable() {
      return this.parent.isWritable();
   }

   public boolean isWritable(int size) {
      return this.parent.isWritable(size);
   }

   public PacketByteBuf clear() {
      this.parent.clear();
      return this;
   }

   public PacketByteBuf markReaderIndex() {
      this.parent.markReaderIndex();
      return this;
   }

   public PacketByteBuf resetReaderIndex() {
      this.parent.resetReaderIndex();
      return this;
   }

   public PacketByteBuf markWriterIndex() {
      this.parent.markWriterIndex();
      return this;
   }

   public PacketByteBuf resetWriterIndex() {
      this.parent.resetWriterIndex();
      return this;
   }

   public PacketByteBuf discardReadBytes() {
      this.parent.discardReadBytes();
      return this;
   }

   public PacketByteBuf discardSomeReadBytes() {
      this.parent.discardSomeReadBytes();
      return this;
   }

   public PacketByteBuf ensureWritable(int i) {
      this.parent.ensureWritable(i);
      return this;
   }

   public int ensureWritable(int minBytes, boolean force) {
      return this.parent.ensureWritable(minBytes, force);
   }

   public boolean getBoolean(int index) {
      return this.parent.getBoolean(index);
   }

   public byte getByte(int index) {
      return this.parent.getByte(index);
   }

   public short getUnsignedByte(int index) {
      return this.parent.getUnsignedByte(index);
   }

   public short getShort(int index) {
      return this.parent.getShort(index);
   }

   public short getShortLE(int index) {
      return this.parent.getShortLE(index);
   }

   public int getUnsignedShort(int index) {
      return this.parent.getUnsignedShort(index);
   }

   public int getUnsignedShortLE(int index) {
      return this.parent.getUnsignedShortLE(index);
   }

   public int getMedium(int index) {
      return this.parent.getMedium(index);
   }

   public int getMediumLE(int index) {
      return this.parent.getMediumLE(index);
   }

   public int getUnsignedMedium(int index) {
      return this.parent.getUnsignedMedium(index);
   }

   public int getUnsignedMediumLE(int index) {
      return this.parent.getUnsignedMediumLE(index);
   }

   public int getInt(int index) {
      return this.parent.getInt(index);
   }

   public int getIntLE(int index) {
      return this.parent.getIntLE(index);
   }

   public long getUnsignedInt(int index) {
      return this.parent.getUnsignedInt(index);
   }

   public long getUnsignedIntLE(int index) {
      return this.parent.getUnsignedIntLE(index);
   }

   public long getLong(int index) {
      return this.parent.getLong(index);
   }

   public long getLongLE(int index) {
      return this.parent.getLongLE(index);
   }

   public char getChar(int index) {
      return this.parent.getChar(index);
   }

   public float getFloat(int index) {
      return this.parent.getFloat(index);
   }

   public double getDouble(int index) {
      return this.parent.getDouble(index);
   }

   public PacketByteBuf getBytes(int i, ByteBuf byteBuf) {
      this.parent.getBytes(i, byteBuf);
      return this;
   }

   public PacketByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
      this.parent.getBytes(i, byteBuf, j);
      return this;
   }

   public PacketByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
      this.parent.getBytes(i, byteBuf, j, k);
      return this;
   }

   public PacketByteBuf getBytes(int i, byte[] bs) {
      this.parent.getBytes(i, bs);
      return this;
   }

   public PacketByteBuf getBytes(int i, byte[] bs, int j, int k) {
      this.parent.getBytes(i, bs, j, k);
      return this;
   }

   public PacketByteBuf getBytes(int i, ByteBuffer byteBuffer) {
      this.parent.getBytes(i, byteBuffer);
      return this;
   }

   public PacketByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
      this.parent.getBytes(i, outputStream, j);
      return this;
   }

   public int getBytes(int index, GatheringByteChannel channel, int length) throws IOException {
      return this.parent.getBytes(index, channel, length);
   }

   public int getBytes(int index, FileChannel channel, long pos, int length) throws IOException {
      return this.parent.getBytes(index, channel, pos, length);
   }

   public CharSequence getCharSequence(int index, int length, Charset charset) {
      return this.parent.getCharSequence(index, length, charset);
   }

   public PacketByteBuf setBoolean(int i, boolean bl) {
      this.parent.setBoolean(i, bl);
      return this;
   }

   public PacketByteBuf setByte(int i, int j) {
      this.parent.setByte(i, j);
      return this;
   }

   public PacketByteBuf setShort(int i, int j) {
      this.parent.setShort(i, j);
      return this;
   }

   public PacketByteBuf setShortLE(int i, int j) {
      this.parent.setShortLE(i, j);
      return this;
   }

   public PacketByteBuf setMedium(int i, int j) {
      this.parent.setMedium(i, j);
      return this;
   }

   public PacketByteBuf setMediumLE(int i, int j) {
      this.parent.setMediumLE(i, j);
      return this;
   }

   public PacketByteBuf setInt(int i, int j) {
      this.parent.setInt(i, j);
      return this;
   }

   public PacketByteBuf setIntLE(int i, int j) {
      this.parent.setIntLE(i, j);
      return this;
   }

   public PacketByteBuf setLong(int i, long l) {
      this.parent.setLong(i, l);
      return this;
   }

   public PacketByteBuf setLongLE(int i, long l) {
      this.parent.setLongLE(i, l);
      return this;
   }

   public PacketByteBuf setChar(int i, int j) {
      this.parent.setChar(i, j);
      return this;
   }

   public PacketByteBuf setFloat(int i, float f) {
      this.parent.setFloat(i, f);
      return this;
   }

   public PacketByteBuf setDouble(int i, double d) {
      this.parent.setDouble(i, d);
      return this;
   }

   public PacketByteBuf setBytes(int i, ByteBuf byteBuf) {
      this.parent.setBytes(i, byteBuf);
      return this;
   }

   public PacketByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
      this.parent.setBytes(i, byteBuf, j);
      return this;
   }

   public PacketByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
      this.parent.setBytes(i, byteBuf, j, k);
      return this;
   }

   public PacketByteBuf setBytes(int i, byte[] bs) {
      this.parent.setBytes(i, bs);
      return this;
   }

   public PacketByteBuf setBytes(int i, byte[] bs, int j, int k) {
      this.parent.setBytes(i, bs, j, k);
      return this;
   }

   public PacketByteBuf setBytes(int i, ByteBuffer byteBuffer) {
      this.parent.setBytes(i, byteBuffer);
      return this;
   }

   public int setBytes(int index, InputStream stream, int length) throws IOException {
      return this.parent.setBytes(index, stream, length);
   }

   public int setBytes(int index, ScatteringByteChannel channel, int length) throws IOException {
      return this.parent.setBytes(index, channel, length);
   }

   public int setBytes(int index, FileChannel channel, long pos, int length) throws IOException {
      return this.parent.setBytes(index, channel, pos, length);
   }

   public PacketByteBuf setZero(int i, int j) {
      this.parent.setZero(i, j);
      return this;
   }

   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
      return this.parent.setCharSequence(index, sequence, charset);
   }

   public boolean readBoolean() {
      return this.parent.readBoolean();
   }

   public byte readByte() {
      return this.parent.readByte();
   }

   public short readUnsignedByte() {
      return this.parent.readUnsignedByte();
   }

   public short readShort() {
      return this.parent.readShort();
   }

   public short readShortLE() {
      return this.parent.readShortLE();
   }

   public int readUnsignedShort() {
      return this.parent.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.parent.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.parent.readMedium();
   }

   public int readMediumLE() {
      return this.parent.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.parent.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.parent.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.parent.readInt();
   }

   public int readIntLE() {
      return this.parent.readIntLE();
   }

   public long readUnsignedInt() {
      return this.parent.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.parent.readUnsignedIntLE();
   }

   public long readLong() {
      return this.parent.readLong();
   }

   public long readLongLE() {
      return this.parent.readLongLE();
   }

   public char readChar() {
      return this.parent.readChar();
   }

   public float readFloat() {
      return this.parent.readFloat();
   }

   public double readDouble() {
      return this.parent.readDouble();
   }

   public ByteBuf readBytes(int length) {
      return this.parent.readBytes(length);
   }

   public ByteBuf readSlice(int length) {
      return this.parent.readSlice(length);
   }

   public ByteBuf readRetainedSlice(int length) {
      return this.parent.readRetainedSlice(length);
   }

   public PacketByteBuf readBytes(ByteBuf byteBuf) {
      this.parent.readBytes(byteBuf);
      return this;
   }

   public PacketByteBuf readBytes(ByteBuf byteBuf, int i) {
      this.parent.readBytes(byteBuf, i);
      return this;
   }

   public PacketByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
      this.parent.readBytes(byteBuf, i, j);
      return this;
   }

   public PacketByteBuf readBytes(byte[] bs) {
      this.parent.readBytes(bs);
      return this;
   }

   public PacketByteBuf readBytes(byte[] bs, int i, int j) {
      this.parent.readBytes(bs, i, j);
      return this;
   }

   public PacketByteBuf readBytes(ByteBuffer byteBuffer) {
      this.parent.readBytes(byteBuffer);
      return this;
   }

   public PacketByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
      this.parent.readBytes(outputStream, i);
      return this;
   }

   public int readBytes(GatheringByteChannel channel, int length) throws IOException {
      return this.parent.readBytes(channel, length);
   }

   public CharSequence readCharSequence(int length, Charset charset) {
      return this.parent.readCharSequence(length, charset);
   }

   public int readBytes(FileChannel channel, long pos, int length) throws IOException {
      return this.parent.readBytes(channel, pos, length);
   }

   public PacketByteBuf skipBytes(int i) {
      this.parent.skipBytes(i);
      return this;
   }

   public PacketByteBuf writeBoolean(boolean bl) {
      this.parent.writeBoolean(bl);
      return this;
   }

   public PacketByteBuf writeByte(int i) {
      this.parent.writeByte(i);
      return this;
   }

   public PacketByteBuf writeShort(int i) {
      this.parent.writeShort(i);
      return this;
   }

   public PacketByteBuf writeShortLE(int i) {
      this.parent.writeShortLE(i);
      return this;
   }

   public PacketByteBuf writeMedium(int i) {
      this.parent.writeMedium(i);
      return this;
   }

   public PacketByteBuf writeMediumLE(int i) {
      this.parent.writeMediumLE(i);
      return this;
   }

   public PacketByteBuf writeInt(int i) {
      this.parent.writeInt(i);
      return this;
   }

   public PacketByteBuf writeIntLE(int i) {
      this.parent.writeIntLE(i);
      return this;
   }

   public PacketByteBuf writeLong(long l) {
      this.parent.writeLong(l);
      return this;
   }

   public PacketByteBuf writeLongLE(long l) {
      this.parent.writeLongLE(l);
      return this;
   }

   public PacketByteBuf writeChar(int i) {
      this.parent.writeChar(i);
      return this;
   }

   public PacketByteBuf writeFloat(float f) {
      this.parent.writeFloat(f);
      return this;
   }

   public PacketByteBuf writeDouble(double d) {
      this.parent.writeDouble(d);
      return this;
   }

   public PacketByteBuf writeBytes(ByteBuf byteBuf) {
      this.parent.writeBytes(byteBuf);
      return this;
   }

   public PacketByteBuf writeBytes(ByteBuf byteBuf, int i) {
      this.parent.writeBytes(byteBuf, i);
      return this;
   }

   public PacketByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
      this.parent.writeBytes(byteBuf, i, j);
      return this;
   }

   public PacketByteBuf writeBytes(byte[] bs) {
      this.parent.writeBytes(bs);
      return this;
   }

   public PacketByteBuf writeBytes(byte[] bs, int i, int j) {
      this.parent.writeBytes(bs, i, j);
      return this;
   }

   public PacketByteBuf writeBytes(ByteBuffer byteBuffer) {
      this.parent.writeBytes(byteBuffer);
      return this;
   }

   public int writeBytes(InputStream stream, int length) throws IOException {
      return this.parent.writeBytes(stream, length);
   }

   public int writeBytes(ScatteringByteChannel channel, int length) throws IOException {
      return this.parent.writeBytes(channel, length);
   }

   public int writeBytes(FileChannel channel, long pos, int length) throws IOException {
      return this.parent.writeBytes(channel, pos, length);
   }

   public PacketByteBuf writeZero(int i) {
      this.parent.writeZero(i);
      return this;
   }

   public int writeCharSequence(CharSequence sequence, Charset charset) {
      return this.parent.writeCharSequence(sequence, charset);
   }

   public int indexOf(int from, int to, byte value) {
      return this.parent.indexOf(from, to, value);
   }

   public int bytesBefore(byte value) {
      return this.parent.bytesBefore(value);
   }

   public int bytesBefore(int length, byte value) {
      return this.parent.bytesBefore(length, value);
   }

   public int bytesBefore(int index, int length, byte value) {
      return this.parent.bytesBefore(index, length, value);
   }

   public int forEachByte(ByteProcessor byteProcessor) {
      return this.parent.forEachByte(byteProcessor);
   }

   public int forEachByte(int index, int length, ByteProcessor byteProcessor) {
      return this.parent.forEachByte(index, length, byteProcessor);
   }

   public int forEachByteDesc(ByteProcessor byteProcessor) {
      return this.parent.forEachByteDesc(byteProcessor);
   }

   public int forEachByteDesc(int index, int length, ByteProcessor byteProcessor) {
      return this.parent.forEachByteDesc(index, length, byteProcessor);
   }

   public ByteBuf copy() {
      return this.parent.copy();
   }

   public ByteBuf copy(int index, int length) {
      return this.parent.copy(index, length);
   }

   public ByteBuf slice() {
      return this.parent.slice();
   }

   public ByteBuf retainedSlice() {
      return this.parent.retainedSlice();
   }

   public ByteBuf slice(int index, int length) {
      return this.parent.slice(index, length);
   }

   public ByteBuf retainedSlice(int index, int length) {
      return this.parent.retainedSlice(index, length);
   }

   public ByteBuf duplicate() {
      return this.parent.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.parent.retainedDuplicate();
   }

   public int nioBufferCount() {
      return this.parent.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.parent.nioBuffer();
   }

   public ByteBuffer nioBuffer(int index, int length) {
      return this.parent.nioBuffer(index, length);
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      return this.parent.internalNioBuffer(index, length);
   }

   public ByteBuffer[] nioBuffers() {
      return this.parent.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return this.parent.nioBuffers(index, length);
   }

   public boolean hasArray() {
      return this.parent.hasArray();
   }

   public byte[] array() {
      return this.parent.array();
   }

   public int arrayOffset() {
      return this.parent.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.parent.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.parent.memoryAddress();
   }

   public String toString(Charset charset) {
      return this.parent.toString(charset);
   }

   public String toString(int index, int length, Charset charset) {
      return this.parent.toString(index, length, charset);
   }

   public int hashCode() {
      return this.parent.hashCode();
   }

   public boolean equals(Object o) {
      return this.parent.equals(o);
   }

   public int compareTo(ByteBuf byteBuf) {
      return this.parent.compareTo(byteBuf);
   }

   public String toString() {
      return this.parent.toString();
   }

   public PacketByteBuf retain(int i) {
      this.parent.retain(i);
      return this;
   }

   public PacketByteBuf retain() {
      this.parent.retain();
      return this;
   }

   public PacketByteBuf touch() {
      this.parent.touch();
      return this;
   }

   public PacketByteBuf touch(Object object) {
      this.parent.touch(object);
      return this;
   }

   public int refCnt() {
      return this.parent.refCnt();
   }

   public boolean release() {
      return this.parent.release();
   }

   public boolean release(int decrement) {
      return this.parent.release(decrement);
   }

   // $FF: synthetic method
   public ByteBuf touch(final Object object) {
      return this.touch(object);
   }

   // $FF: synthetic method
   public ByteBuf touch() {
      return this.touch();
   }

   // $FF: synthetic method
   public ByteBuf retain() {
      return this.retain();
   }

   // $FF: synthetic method
   public ByteBuf retain(final int increment) {
      return this.retain(increment);
   }

   // $FF: synthetic method
   public ByteBuf writeZero(final int length) {
      return this.writeZero(length);
   }

   // $FF: synthetic method
   public ByteBuf writeBytes(final ByteBuffer buf) {
      return this.writeBytes(buf);
   }

   // $FF: synthetic method
   public ByteBuf writeBytes(final byte[] bytes, final int sourceIndex, final int length) {
      return this.writeBytes(bytes, sourceIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf writeBytes(final byte[] bytes) {
      return this.writeBytes(bytes);
   }

   // $FF: synthetic method
   public ByteBuf writeBytes(final ByteBuf buf, final int sourceIndex, final int length) {
      return this.writeBytes(buf, sourceIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf writeBytes(final ByteBuf buf, final int length) {
      return this.writeBytes(buf, length);
   }

   // $FF: synthetic method
   public ByteBuf writeBytes(final ByteBuf buf) {
      return this.writeBytes(buf);
   }

   // $FF: synthetic method
   public ByteBuf writeDouble(final double value) {
      return this.writeDouble(value);
   }

   // $FF: synthetic method
   public ByteBuf writeFloat(final float value) {
      return this.writeFloat(value);
   }

   // $FF: synthetic method
   public ByteBuf writeChar(final int value) {
      return this.writeChar(value);
   }

   // $FF: synthetic method
   public ByteBuf writeLongLE(final long value) {
      return this.writeLongLE(value);
   }

   // $FF: synthetic method
   public ByteBuf writeLong(final long value) {
      return this.writeLong(value);
   }

   // $FF: synthetic method
   public ByteBuf writeIntLE(final int value) {
      return this.writeIntLE(value);
   }

   // $FF: synthetic method
   public ByteBuf writeInt(final int value) {
      return this.writeInt(value);
   }

   // $FF: synthetic method
   public ByteBuf writeMediumLE(final int value) {
      return this.writeMediumLE(value);
   }

   // $FF: synthetic method
   public ByteBuf writeMedium(final int value) {
      return this.writeMedium(value);
   }

   // $FF: synthetic method
   public ByteBuf writeShortLE(final int value) {
      return this.writeShortLE(value);
   }

   // $FF: synthetic method
   public ByteBuf writeShort(final int value) {
      return this.writeShort(value);
   }

   // $FF: synthetic method
   public ByteBuf writeByte(final int value) {
      return this.writeByte(value);
   }

   // $FF: synthetic method
   public ByteBuf writeBoolean(final boolean value) {
      return this.writeBoolean(value);
   }

   // $FF: synthetic method
   public ByteBuf skipBytes(final int length) {
      return this.skipBytes(length);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final OutputStream stream, final int length) throws IOException {
      return this.readBytes(stream, length);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final ByteBuffer buf) {
      return this.readBytes(buf);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final byte[] bytes, final int outputIndex, final int length) {
      return this.readBytes(bytes, outputIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final byte[] bytes) {
      return this.readBytes(bytes);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final ByteBuf buf, final int outputIndex, final int length) {
      return this.readBytes(buf, outputIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final ByteBuf buf, final int length) {
      return this.readBytes(buf, length);
   }

   // $FF: synthetic method
   public ByteBuf readBytes(final ByteBuf buf) {
      return this.readBytes(buf);
   }

   // $FF: synthetic method
   public ByteBuf setZero(final int index, final int length) {
      return this.setZero(index, length);
   }

   // $FF: synthetic method
   public ByteBuf setBytes(final int index, final ByteBuffer buf) {
      return this.setBytes(index, buf);
   }

   // $FF: synthetic method
   public ByteBuf setBytes(final int index, final byte[] bytes, final int sourceIndex, final int length) {
      return this.setBytes(index, bytes, sourceIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf setBytes(final int index, final byte[] bytes) {
      return this.setBytes(index, bytes);
   }

   // $FF: synthetic method
   public ByteBuf setBytes(final int index, final ByteBuf buf, final int sourceIndex, final int length) {
      return this.setBytes(index, buf, sourceIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf setBytes(final int index, final ByteBuf buf, final int length) {
      return this.setBytes(index, buf, length);
   }

   // $FF: synthetic method
   public ByteBuf setBytes(final int index, final ByteBuf buf) {
      return this.setBytes(index, buf);
   }

   // $FF: synthetic method
   public ByteBuf setDouble(final int index, final double value) {
      return this.setDouble(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setFloat(final int index, final float value) {
      return this.setFloat(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setChar(final int index, final int value) {
      return this.setChar(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setLongLE(final int index, final long value) {
      return this.setLongLE(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setLong(final int index, final long value) {
      return this.setLong(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setIntLE(final int index, final int value) {
      return this.setIntLE(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setInt(final int index, final int value) {
      return this.setInt(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setMediumLE(final int index, final int value) {
      return this.setMediumLE(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setMedium(final int index, final int value) {
      return this.setMedium(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setShortLE(final int index, final int value) {
      return this.setShortLE(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setShort(final int index, final int value) {
      return this.setShort(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setByte(final int index, final int value) {
      return this.setByte(index, value);
   }

   // $FF: synthetic method
   public ByteBuf setBoolean(final int index, final boolean value) {
      return this.setBoolean(index, value);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final OutputStream stream, final int length) throws IOException {
      return this.getBytes(index, stream, length);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final ByteBuffer buf) {
      return this.getBytes(index, buf);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final byte[] bytes, final int outputIndex, final int length) {
      return this.getBytes(index, bytes, outputIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final byte[] bytes) {
      return this.getBytes(index, bytes);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final ByteBuf buf, final int outputIndex, final int length) {
      return this.getBytes(index, buf, outputIndex, length);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final ByteBuf buf, final int length) {
      return this.getBytes(index, buf, length);
   }

   // $FF: synthetic method
   public ByteBuf getBytes(final int index, final ByteBuf buf) {
      return this.getBytes(index, buf);
   }

   // $FF: synthetic method
   public ByteBuf ensureWritable(final int minBytes) {
      return this.ensureWritable(minBytes);
   }

   // $FF: synthetic method
   public ByteBuf discardSomeReadBytes() {
      return this.discardSomeReadBytes();
   }

   // $FF: synthetic method
   public ByteBuf discardReadBytes() {
      return this.discardReadBytes();
   }

   // $FF: synthetic method
   public ByteBuf resetWriterIndex() {
      return this.resetWriterIndex();
   }

   // $FF: synthetic method
   public ByteBuf markWriterIndex() {
      return this.markWriterIndex();
   }

   // $FF: synthetic method
   public ByteBuf resetReaderIndex() {
      return this.resetReaderIndex();
   }

   // $FF: synthetic method
   public ByteBuf markReaderIndex() {
      return this.markReaderIndex();
   }

   // $FF: synthetic method
   public ByteBuf clear() {
      return this.clear();
   }

   // $FF: synthetic method
   public ByteBuf setIndex(final int readerIndex, final int writerIndex) {
      return this.setIndex(readerIndex, writerIndex);
   }

   // $FF: synthetic method
   public ByteBuf writerIndex(final int index) {
      return this.writerIndex(index);
   }

   // $FF: synthetic method
   public ByteBuf readerIndex(final int index) {
      return this.readerIndex(index);
   }

   // $FF: synthetic method
   public ByteBuf capacity(final int capacity) {
      return this.capacity(capacity);
   }

   // $FF: synthetic method
   public ReferenceCounted touch(final Object object) {
      return this.touch(object);
   }

   // $FF: synthetic method
   public ReferenceCounted touch() {
      return this.touch();
   }

   // $FF: synthetic method
   public ReferenceCounted retain(final int increment) {
      return this.retain(increment);
   }

   // $FF: synthetic method
   public ReferenceCounted retain() {
      return this.retain();
   }
}
