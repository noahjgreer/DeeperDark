package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class HotbarStorageEntry {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int HOTBAR_SIZE = PlayerInventory.getHotbarSize();
   public static final Codec CODEC;
   private static final DynamicOps NBT_OPS;
   private static final Dynamic EMPTY_STACK;
   private List stacks;

   private HotbarStorageEntry(List stacks) {
      this.stacks = stacks;
   }

   public HotbarStorageEntry() {
      this(Collections.nCopies(HOTBAR_SIZE, EMPTY_STACK));
   }

   public List deserialize(RegistryWrapper.WrapperLookup registries) {
      return this.stacks.stream().map((stack) -> {
         return (ItemStack)ItemStack.OPTIONAL_CODEC.parse(RegistryOps.withRegistry(stack, registries)).resultOrPartial((error) -> {
            LOGGER.warn("Could not parse hotbar item: {}", error);
         }).orElse(ItemStack.EMPTY);
      }).toList();
   }

   public void serialize(PlayerInventory playerInventory, DynamicRegistryManager registryManager) {
      RegistryOps registryOps = registryManager.getOps(NBT_OPS);
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(HOTBAR_SIZE);

      for(int i = 0; i < HOTBAR_SIZE; ++i) {
         ItemStack itemStack = playerInventory.getStack(i);
         Optional optional = ItemStack.OPTIONAL_CODEC.encodeStart(registryOps, itemStack).resultOrPartial((error) -> {
            LOGGER.warn("Could not encode hotbar item: {}", error);
         }).map((nbt) -> {
            return new Dynamic(NBT_OPS, nbt);
         });
         builder.add((Dynamic)optional.orElse(EMPTY_STACK));
      }

      this.stacks = builder.build();
   }

   public boolean isEmpty() {
      Iterator var1 = this.stacks.iterator();

      Dynamic dynamic;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         dynamic = (Dynamic)var1.next();
      } while(isEmpty(dynamic));

      return false;
   }

   private static boolean isEmpty(Dynamic stack) {
      return EMPTY_STACK.equals(stack);
   }

   static {
      CODEC = Codec.PASSTHROUGH.listOf().validate((stacks) -> {
         return Util.decodeFixedLengthList(stacks, HOTBAR_SIZE);
      }).xmap(HotbarStorageEntry::new, (entry) -> {
         return entry.stacks;
      });
      NBT_OPS = NbtOps.INSTANCE;
      EMPTY_STACK = new Dynamic(NBT_OPS, (NbtElement)ItemStack.OPTIONAL_CODEC.encodeStart(NBT_OPS, ItemStack.EMPTY).getOrThrow());
   }
}
