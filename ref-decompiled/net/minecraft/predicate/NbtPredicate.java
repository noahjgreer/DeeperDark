package net.minecraft.predicate;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public record NbtPredicate(NbtCompound nbt) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final String SELECTED_ITEM_KEY = "SelectedItem";

   public NbtPredicate(NbtCompound nbt) {
      this.nbt = nbt;
   }

   public boolean test(ComponentsAccess components) {
      NbtComponent nbtComponent = (NbtComponent)components.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
      return nbtComponent.matches(this.nbt);
   }

   public boolean test(Entity entity) {
      return this.test((NbtElement)entityToNbt(entity));
   }

   public boolean test(@Nullable NbtElement element) {
      return element != null && NbtHelper.matches(this.nbt, element, true);
   }

   public static NbtCompound entityToNbt(Entity entity) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);

      NbtCompound var7;
      try {
         NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
         entity.writeData(nbtWriteView);
         if (entity instanceof PlayerEntity playerEntity) {
            ItemStack itemStack = playerEntity.getInventory().getSelectedStack();
            if (!itemStack.isEmpty()) {
               nbtWriteView.put("SelectedItem", ItemStack.CODEC, itemStack);
            }
         }

         var7 = nbtWriteView.getNbt();
      } catch (Throwable var6) {
         try {
            logging.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      logging.close();
      return var7;
   }

   public NbtCompound nbt() {
      return this.nbt;
   }

   static {
      CODEC = StringNbtReader.NBT_COMPOUND_CODEC.xmap(NbtPredicate::new, NbtPredicate::nbt);
      PACKET_CODEC = PacketCodecs.NBT_COMPOUND.xmap(NbtPredicate::new, NbtPredicate::nbt);
   }
}
