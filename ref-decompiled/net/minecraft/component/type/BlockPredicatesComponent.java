package net.minecraft.component.type;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BlockPredicatesComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final Text CAN_BREAK_TEXT;
   public static final Text CAN_PLACE_TEXT;
   private static final Text CAN_USE_UNKNOWN_TEXT;
   private final List predicates;
   @Nullable
   private List tooltipText;
   @Nullable
   private CachedBlockPosition cachedPos;
   private boolean lastResult;
   private boolean nbtAware;

   public BlockPredicatesComponent(List predicates) {
      this.predicates = predicates;
   }

   private static boolean canUseCache(CachedBlockPosition pos, @Nullable CachedBlockPosition cachedPos, boolean nbtAware) {
      if (cachedPos != null && pos.getBlockState() == cachedPos.getBlockState()) {
         if (!nbtAware) {
            return true;
         } else if (pos.getBlockEntity() == null && cachedPos.getBlockEntity() == null) {
            return true;
         } else if (pos.getBlockEntity() != null && cachedPos.getBlockEntity() != null) {
            ErrorReporter.Logging logging = new ErrorReporter.Logging(LOGGER);

            boolean var7;
            try {
               DynamicRegistryManager dynamicRegistryManager = pos.getWorld().getRegistryManager();
               NbtCompound nbtCompound = getNbt(pos.getBlockEntity(), dynamicRegistryManager, logging);
               NbtCompound nbtCompound2 = getNbt(cachedPos.getBlockEntity(), dynamicRegistryManager, logging);
               var7 = Objects.equals(nbtCompound, nbtCompound2);
            } catch (Throwable var9) {
               try {
                  logging.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            logging.close();
            return var7;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static NbtCompound getNbt(BlockEntity blockEntity, DynamicRegistryManager registries, ErrorReporter errorReporter) {
      NbtWriteView nbtWriteView = NbtWriteView.create(errorReporter.makeChild(blockEntity.getReporterContext()), registries);
      blockEntity.writeDataWithId(nbtWriteView);
      return nbtWriteView.getNbt();
   }

   public boolean check(CachedBlockPosition cachedPos) {
      if (canUseCache(cachedPos, this.cachedPos, this.nbtAware)) {
         return this.lastResult;
      } else {
         this.cachedPos = cachedPos;
         this.nbtAware = false;
         Iterator var2 = this.predicates.iterator();

         BlockPredicate blockPredicate;
         do {
            if (!var2.hasNext()) {
               this.lastResult = false;
               return false;
            }

            blockPredicate = (BlockPredicate)var2.next();
         } while(!blockPredicate.test(cachedPos));

         this.nbtAware |= blockPredicate.hasNbt();
         this.lastResult = true;
         return true;
      }
   }

   private List getOrCreateTooltipText() {
      if (this.tooltipText == null) {
         this.tooltipText = createTooltipText(this.predicates);
      }

      return this.tooltipText;
   }

   public void addTooltips(Consumer adder) {
      this.getOrCreateTooltipText().forEach(adder);
   }

   private static List createTooltipText(List blockPredicates) {
      Iterator var1 = blockPredicates.iterator();

      BlockPredicate blockPredicate;
      do {
         if (!var1.hasNext()) {
            return blockPredicates.stream().flatMap((predicate) -> {
               return ((RegistryEntryList)predicate.blocks().orElseThrow()).stream();
            }).distinct().map((block) -> {
               return ((Block)block.value()).getName().formatted(Formatting.DARK_GRAY);
            }).toList();
         }

         blockPredicate = (BlockPredicate)var1.next();
      } while(!blockPredicate.blocks().isEmpty());

      return List.of(CAN_USE_UNKNOWN_TEXT);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof BlockPredicatesComponent) {
         BlockPredicatesComponent blockPredicatesComponent = (BlockPredicatesComponent)o;
         return this.predicates.equals(blockPredicatesComponent.predicates);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.predicates.hashCode();
   }

   public String toString() {
      return "AdventureModePredicate{predicates=" + String.valueOf(this.predicates) + "}";
   }

   static {
      CODEC = Codecs.listOrSingle(BlockPredicate.CODEC, Codecs.nonEmptyList(BlockPredicate.CODEC.listOf())).xmap(BlockPredicatesComponent::new, (checker) -> {
         return checker.predicates;
      });
      PACKET_CODEC = PacketCodec.tuple(BlockPredicate.PACKET_CODEC.collect(PacketCodecs.toList()), (blockPredicatesChecker) -> {
         return blockPredicatesChecker.predicates;
      }, BlockPredicatesComponent::new);
      CAN_BREAK_TEXT = Text.translatable("item.canBreak").formatted(Formatting.GRAY);
      CAN_PLACE_TEXT = Text.translatable("item.canPlace").formatted(Formatting.GRAY);
      CAN_USE_UNKNOWN_TEXT = Text.translatable("item.canUse.unknown").formatted(Formatting.GRAY);
   }
}
