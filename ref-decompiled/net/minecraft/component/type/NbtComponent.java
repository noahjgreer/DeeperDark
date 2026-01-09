package net.minecraft.component.type;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class NbtComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final NbtComponent DEFAULT = new NbtComponent(new NbtCompound());
   private static final String ID_KEY = "id";
   public static final Codec CODEC;
   public static final Codec CODEC_WITH_ID;
   /** @deprecated */
   @Deprecated
   public static final PacketCodec PACKET_CODEC;
   private final NbtCompound nbt;

   private NbtComponent(NbtCompound nbt) {
      this.nbt = nbt;
   }

   public static NbtComponent of(NbtCompound nbt) {
      return new NbtComponent(nbt.copy());
   }

   public boolean matches(NbtCompound nbt) {
      return NbtHelper.matches(nbt, this.nbt, true);
   }

   public static void set(ComponentType type, ItemStack stack, Consumer nbtSetter) {
      NbtComponent nbtComponent = ((NbtComponent)stack.getOrDefault(type, DEFAULT)).apply(nbtSetter);
      if (nbtComponent.nbt.isEmpty()) {
         stack.remove(type);
      } else {
         stack.set(type, nbtComponent);
      }

   }

   public static void set(ComponentType type, ItemStack stack, NbtCompound nbt) {
      if (!nbt.isEmpty()) {
         stack.set(type, of(nbt));
      } else {
         stack.remove(type);
      }

   }

   public NbtComponent apply(Consumer nbtConsumer) {
      NbtCompound nbtCompound = this.nbt.copy();
      nbtConsumer.accept(nbtCompound);
      return new NbtComponent(nbtCompound);
   }

   @Nullable
   public Identifier getId() {
      return (Identifier)this.nbt.get("id", Identifier.CODEC).orElse((Object)null);
   }

   @Nullable
   public Object getRegistryValueOfId(RegistryWrapper.WrapperLookup registries, RegistryKey registryRef) {
      Identifier identifier = this.getId();
      return identifier == null ? null : registries.getOptional(registryRef).flatMap((registry) -> {
         return registry.getOptional(RegistryKey.of(registryRef, identifier));
      }).map(RegistryEntry::value).orElse((Object)null);
   }

   public void applyToEntity(Entity entity) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);

      try {
         NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
         entity.writeData(nbtWriteView);
         NbtCompound nbtCompound = nbtWriteView.getNbt();
         UUID uUID = entity.getUuid();
         nbtCompound.copyFrom(this.nbt);
         entity.readData(NbtReadView.create(logging, entity.getRegistryManager(), nbtCompound));
         entity.setUuid(uUID);
      } catch (Throwable var7) {
         try {
            logging.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      logging.close();
   }

   public boolean applyToBlockEntity(BlockEntity blockEntity, RegistryWrapper.WrapperLookup registries) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER);

      boolean var7;
      label38: {
         try {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, registries);
            blockEntity.writeComponentlessData(nbtWriteView);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            NbtCompound nbtCompound2 = nbtCompound.copy();
            nbtCompound.copyFrom(this.nbt);
            if (!nbtCompound.equals(nbtCompound2)) {
               try {
                  blockEntity.readComponentlessData(NbtReadView.create(logging, registries, nbtCompound));
                  blockEntity.markDirty();
                  var7 = true;
                  break label38;
               } catch (Exception var11) {
                  LOGGER.warn("Failed to apply custom data to block entity at {}", blockEntity.getPos(), var11);

                  try {
                     blockEntity.readComponentlessData(NbtReadView.create(logging.makeChild(() -> {
                        return "(rollback)";
                     }), registries, nbtCompound2));
                  } catch (Exception var10) {
                     LOGGER.warn("Failed to rollback block entity at {} after failure", blockEntity.getPos(), var10);
                  }
               }
            }

            var7 = false;
         } catch (Throwable var12) {
            try {
               logging.close();
            } catch (Throwable var9) {
               var12.addSuppressed(var9);
            }

            throw var12;
         }

         logging.close();
         return var7;
      }

      logging.close();
      return var7;
   }

   public DataResult with(DynamicOps ops, MapEncoder encoder, Object value) {
      return encoder.encode(value, ops, ops.mapBuilder()).build(this.nbt).map((nbt) -> {
         return new NbtComponent((NbtCompound)nbt);
      });
   }

   public DataResult get(MapDecoder decoder) {
      return this.get(NbtOps.INSTANCE, decoder);
   }

   public DataResult get(DynamicOps ops, MapDecoder decoder) {
      MapLike mapLike = (MapLike)ops.getMap(this.nbt).getOrThrow();
      return decoder.decode(ops, mapLike);
   }

   public int getSize() {
      return this.nbt.getSize();
   }

   public boolean isEmpty() {
      return this.nbt.isEmpty();
   }

   public NbtCompound copyNbt() {
      return this.nbt.copy();
   }

   public boolean contains(String key) {
      return this.nbt.contains(key);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof NbtComponent) {
         NbtComponent nbtComponent = (NbtComponent)o;
         return this.nbt.equals(nbtComponent.nbt);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.nbt.hashCode();
   }

   public String toString() {
      return this.nbt.toString();
   }

   /** @deprecated */
   @Deprecated
   public NbtCompound getNbt() {
      return this.nbt;
   }

   static {
      CODEC = Codec.withAlternative(NbtCompound.CODEC, StringNbtReader.STRINGIFIED_CODEC).xmap(NbtComponent::new, (component) -> {
         return component.nbt;
      });
      CODEC_WITH_ID = CODEC.validate((component) -> {
         return component.getNbt().getString("id").isPresent() ? DataResult.success(component) : DataResult.error(() -> {
            return "Missing id for entity in: " + String.valueOf(component);
         });
      });
      PACKET_CODEC = PacketCodecs.NBT_COMPOUND.xmap(NbtComponent::new, (component) -> {
         return component.nbt;
      });
   }
}
