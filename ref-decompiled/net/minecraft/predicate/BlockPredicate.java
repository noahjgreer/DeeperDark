package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public record BlockPredicate(Optional blocks, Optional state, Optional nbt, ComponentsPredicate components) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.BLOCK).optionalFieldOf("blocks").forGetter(BlockPredicate::blocks), StatePredicate.CODEC.optionalFieldOf("state").forGetter(BlockPredicate::state), NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(BlockPredicate::nbt), ComponentsPredicate.CODEC.forGetter(BlockPredicate::components)).apply(instance, BlockPredicate::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public BlockPredicate(Optional optional, Optional optional2, Optional optional3, ComponentsPredicate componentsPredicate) {
      this.blocks = optional;
      this.state = optional2;
      this.nbt = optional3;
      this.components = componentsPredicate;
   }

   public boolean test(ServerWorld world, BlockPos pos) {
      if (!world.isPosLoaded(pos)) {
         return false;
      } else if (!this.testState(world.getBlockState(pos))) {
         return false;
      } else {
         if (this.nbt.isPresent() || !this.components.isEmpty()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (this.nbt.isPresent() && !testNbt(world, blockEntity, (NbtPredicate)this.nbt.get())) {
               return false;
            }

            if (!this.components.isEmpty() && !testComponents(blockEntity, this.components)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean test(CachedBlockPosition pos) {
      if (!this.testState(pos.getBlockState())) {
         return false;
      } else {
         return !this.nbt.isPresent() || testNbt(pos.getWorld(), pos.getBlockEntity(), (NbtPredicate)this.nbt.get());
      }
   }

   private boolean testState(BlockState state) {
      if (this.blocks.isPresent() && !state.isIn((RegistryEntryList)this.blocks.get())) {
         return false;
      } else {
         return !this.state.isPresent() || ((StatePredicate)this.state.get()).test(state);
      }
   }

   private static boolean testNbt(WorldView world, @Nullable BlockEntity blockEntity, NbtPredicate nbtPredicate) {
      return blockEntity != null && nbtPredicate.test((NbtElement)blockEntity.createNbtWithIdentifyingData(world.getRegistryManager()));
   }

   private static boolean testComponents(@Nullable BlockEntity blockEntity, ComponentsPredicate components) {
      return blockEntity != null && components.test((ComponentsAccess)blockEntity.createComponentMap());
   }

   public boolean hasNbt() {
      return this.nbt.isPresent();
   }

   public Optional blocks() {
      return this.blocks;
   }

   public Optional state() {
      return this.state;
   }

   public Optional nbt() {
      return this.nbt;
   }

   public ComponentsPredicate components() {
      return this.components;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(PacketCodecs.registryEntryList(RegistryKeys.BLOCK)), BlockPredicate::blocks, PacketCodecs.optional(StatePredicate.PACKET_CODEC), BlockPredicate::state, PacketCodecs.optional(NbtPredicate.PACKET_CODEC), BlockPredicate::nbt, ComponentsPredicate.PACKET_CODEC, BlockPredicate::components, BlockPredicate::new);
   }

   public static class Builder {
      private Optional blocks = Optional.empty();
      private Optional state = Optional.empty();
      private Optional nbt = Optional.empty();
      private ComponentsPredicate components;

      private Builder() {
         this.components = ComponentsPredicate.EMPTY;
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder blocks(RegistryEntryLookup blockRegistry, Block... blocks) {
         return this.blocks(blockRegistry, (Collection)Arrays.asList(blocks));
      }

      public Builder blocks(RegistryEntryLookup blockRegistry, Collection blocks) {
         this.blocks = Optional.of(RegistryEntryList.of(Block::getRegistryEntry, blocks));
         return this;
      }

      public Builder tag(RegistryEntryLookup blockRegistry, TagKey tag) {
         this.blocks = Optional.of(blockRegistry.getOrThrow(tag));
         return this;
      }

      public Builder nbt(NbtCompound nbt) {
         this.nbt = Optional.of(new NbtPredicate(nbt));
         return this;
      }

      public Builder state(StatePredicate.Builder state) {
         this.state = state.build();
         return this;
      }

      public Builder components(ComponentsPredicate components) {
         this.components = components;
         return this;
      }

      public BlockPredicate build() {
         return new BlockPredicate(this.blocks, this.state, this.nbt, this.components);
      }
   }
}
