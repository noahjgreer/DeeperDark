package net.minecraft.entity.data;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.WolfSoundVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.Nullable;

public class TrackedDataHandlerRegistry {
   private static final Int2ObjectBiMap DATA_HANDLERS = Int2ObjectBiMap.create(16);
   public static final TrackedDataHandler BYTE;
   public static final TrackedDataHandler INTEGER;
   public static final TrackedDataHandler LONG;
   public static final TrackedDataHandler FLOAT;
   public static final TrackedDataHandler STRING;
   public static final TrackedDataHandler TEXT_COMPONENT;
   public static final TrackedDataHandler OPTIONAL_TEXT_COMPONENT;
   public static final TrackedDataHandler ITEM_STACK;
   public static final TrackedDataHandler BLOCK_STATE;
   private static final PacketCodec OPTIONAL_BLOCK_STATE_CODEC;
   public static final TrackedDataHandler OPTIONAL_BLOCK_STATE;
   public static final TrackedDataHandler BOOLEAN;
   public static final TrackedDataHandler PARTICLE;
   public static final TrackedDataHandler PARTICLE_LIST;
   public static final TrackedDataHandler ROTATION;
   public static final TrackedDataHandler BLOCK_POS;
   public static final TrackedDataHandler OPTIONAL_BLOCK_POS;
   public static final TrackedDataHandler FACING;
   public static final TrackedDataHandler LAZY_ENTITY_REFERENCE;
   public static final TrackedDataHandler OPTIONAL_GLOBAL_POS;
   public static final TrackedDataHandler NBT_COMPOUND;
   public static final TrackedDataHandler VILLAGER_DATA;
   private static final PacketCodec OPTIONAL_INT_CODEC;
   public static final TrackedDataHandler OPTIONAL_INT;
   public static final TrackedDataHandler ENTITY_POSE;
   public static final TrackedDataHandler CAT_VARIANT;
   public static final TrackedDataHandler CHICKEN_VARIANT;
   public static final TrackedDataHandler COW_VARIANT;
   public static final TrackedDataHandler WOLF_VARIANT;
   public static final TrackedDataHandler WOLF_SOUND_VARIANT;
   public static final TrackedDataHandler FROG_VARIANT;
   public static final TrackedDataHandler PIG_VARIANT;
   public static final TrackedDataHandler PAINTING_VARIANT;
   public static final TrackedDataHandler ARMADILLO_STATE;
   public static final TrackedDataHandler SNIFFER_STATE;
   public static final TrackedDataHandler VECTOR_3F;
   public static final TrackedDataHandler QUATERNION_F;

   public static void register(TrackedDataHandler handler) {
      DATA_HANDLERS.add(handler);
   }

   @Nullable
   public static TrackedDataHandler get(int id) {
      return (TrackedDataHandler)DATA_HANDLERS.get(id);
   }

   public static int getId(TrackedDataHandler handler) {
      return DATA_HANDLERS.getRawId(handler);
   }

   private TrackedDataHandlerRegistry() {
   }

   static {
      BYTE = TrackedDataHandler.create(PacketCodecs.BYTE);
      INTEGER = TrackedDataHandler.create(PacketCodecs.VAR_INT);
      LONG = TrackedDataHandler.create(PacketCodecs.VAR_LONG);
      FLOAT = TrackedDataHandler.create(PacketCodecs.FLOAT);
      STRING = TrackedDataHandler.create(PacketCodecs.STRING);
      TEXT_COMPONENT = TrackedDataHandler.create(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
      OPTIONAL_TEXT_COMPONENT = TrackedDataHandler.create(TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC);
      ITEM_STACK = new TrackedDataHandler() {
         public PacketCodec codec() {
            return ItemStack.OPTIONAL_PACKET_CODEC;
         }

         public ItemStack copy(ItemStack itemStack) {
            return itemStack.copy();
         }

         // $FF: synthetic method
         public Object copy(final Object object) {
            return this.copy((ItemStack)object);
         }
      };
      BLOCK_STATE = TrackedDataHandler.create(PacketCodecs.entryOf(Block.STATE_IDS));
      OPTIONAL_BLOCK_STATE_CODEC = new PacketCodec() {
         public void encode(ByteBuf byteBuf, Optional optional) {
            if (optional.isPresent()) {
               VarInts.write(byteBuf, Block.getRawIdFromState((BlockState)optional.get()));
            } else {
               VarInts.write(byteBuf, 0);
            }

         }

         public Optional decode(ByteBuf byteBuf) {
            int i = VarInts.read(byteBuf);
            return i == 0 ? Optional.empty() : Optional.of(Block.getStateFromRawId(i));
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
      OPTIONAL_BLOCK_STATE = TrackedDataHandler.create(OPTIONAL_BLOCK_STATE_CODEC);
      BOOLEAN = TrackedDataHandler.create(PacketCodecs.BOOLEAN);
      PARTICLE = TrackedDataHandler.create(ParticleTypes.PACKET_CODEC);
      PARTICLE_LIST = TrackedDataHandler.create(ParticleTypes.PACKET_CODEC.collect(PacketCodecs.toList()));
      ROTATION = TrackedDataHandler.create(EulerAngle.PACKET_CODEC);
      BLOCK_POS = TrackedDataHandler.create(BlockPos.PACKET_CODEC);
      OPTIONAL_BLOCK_POS = TrackedDataHandler.create(BlockPos.PACKET_CODEC.collect(PacketCodecs::optional));
      FACING = TrackedDataHandler.create(Direction.PACKET_CODEC);
      LAZY_ENTITY_REFERENCE = TrackedDataHandler.create(LazyEntityReference.createPacketCodec().collect(PacketCodecs::optional));
      OPTIONAL_GLOBAL_POS = TrackedDataHandler.create(GlobalPos.PACKET_CODEC.collect(PacketCodecs::optional));
      NBT_COMPOUND = new TrackedDataHandler() {
         public PacketCodec codec() {
            return PacketCodecs.UNLIMITED_NBT_COMPOUND;
         }

         public NbtCompound copy(NbtCompound nbtCompound) {
            return nbtCompound.copy();
         }

         // $FF: synthetic method
         public Object copy(final Object object) {
            return this.copy((NbtCompound)object);
         }
      };
      VILLAGER_DATA = TrackedDataHandler.create(VillagerData.PACKET_CODEC);
      OPTIONAL_INT_CODEC = new PacketCodec() {
         public OptionalInt decode(ByteBuf byteBuf) {
            int i = VarInts.read(byteBuf);
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
         }

         public void encode(ByteBuf byteBuf, OptionalInt optionalInt) {
            VarInts.write(byteBuf, optionalInt.orElse(-1) + 1);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (OptionalInt)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
      OPTIONAL_INT = TrackedDataHandler.create(OPTIONAL_INT_CODEC);
      ENTITY_POSE = TrackedDataHandler.create(EntityPose.PACKET_CODEC);
      CAT_VARIANT = TrackedDataHandler.create(CatVariant.PACKET_CODEC);
      CHICKEN_VARIANT = TrackedDataHandler.create(ChickenVariant.ENTRY_PACKET_CODEC);
      COW_VARIANT = TrackedDataHandler.create(CowVariant.ENTRY_PACKET_CODEC);
      WOLF_VARIANT = TrackedDataHandler.create(WolfVariant.ENTRY_PACKET_CODEC);
      WOLF_SOUND_VARIANT = TrackedDataHandler.create(WolfSoundVariant.PACKET_CODEC);
      FROG_VARIANT = TrackedDataHandler.create(FrogVariant.PACKET_CODEC);
      PIG_VARIANT = TrackedDataHandler.create(PigVariant.ENTRY_PACKET_CODEC);
      PAINTING_VARIANT = TrackedDataHandler.create(PaintingVariant.ENTRY_PACKET_CODEC);
      ARMADILLO_STATE = TrackedDataHandler.create(ArmadilloEntity.State.PACKET_CODEC);
      SNIFFER_STATE = TrackedDataHandler.create(SnifferEntity.State.PACKET_CODEC);
      VECTOR_3F = TrackedDataHandler.create(PacketCodecs.VECTOR_3F);
      QUATERNION_F = TrackedDataHandler.create(PacketCodecs.QUATERNION_F);
      register(BYTE);
      register(INTEGER);
      register(LONG);
      register(FLOAT);
      register(STRING);
      register(TEXT_COMPONENT);
      register(OPTIONAL_TEXT_COMPONENT);
      register(ITEM_STACK);
      register(BOOLEAN);
      register(ROTATION);
      register(BLOCK_POS);
      register(OPTIONAL_BLOCK_POS);
      register(FACING);
      register(LAZY_ENTITY_REFERENCE);
      register(BLOCK_STATE);
      register(OPTIONAL_BLOCK_STATE);
      register(NBT_COMPOUND);
      register(PARTICLE);
      register(PARTICLE_LIST);
      register(VILLAGER_DATA);
      register(OPTIONAL_INT);
      register(ENTITY_POSE);
      register(CAT_VARIANT);
      register(COW_VARIANT);
      register(WOLF_VARIANT);
      register(WOLF_SOUND_VARIANT);
      register(FROG_VARIANT);
      register(PIG_VARIANT);
      register(CHICKEN_VARIANT);
      register(OPTIONAL_GLOBAL_POS);
      register(PAINTING_VARIANT);
      register(SNIFFER_STATE);
      register(ARMADILLO_STATE);
      register(VECTOR_3F);
      register(QUATERNION_F);
   }
}
