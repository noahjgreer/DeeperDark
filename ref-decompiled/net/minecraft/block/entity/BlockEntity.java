package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class BlockEntity implements RenderDataBlockEntity, AttachmentTarget {
   private static final Codec TYPE_CODEC;
   private static final Logger LOGGER;
   private final BlockEntityType type;
   @Nullable
   protected World world;
   protected final BlockPos pos;
   protected boolean removed;
   private BlockState cachedState;
   private ComponentMap components;

   public BlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
      this.components = ComponentMap.EMPTY;
      this.type = type;
      this.pos = pos.toImmutable();
      this.validateSupports(state);
      this.cachedState = state;
   }

   private void validateSupports(BlockState state) {
      if (!this.supports(state)) {
         String var10002 = this.getNameForReport();
         throw new IllegalStateException("Invalid block entity " + var10002 + " state at " + String.valueOf(this.pos) + ", got " + String.valueOf(state));
      }
   }

   public boolean supports(BlockState state) {
      return this.type.supports(state);
   }

   public static BlockPos posFromNbt(ChunkPos chunkPos, NbtCompound nbt) {
      int i = nbt.getInt("x", 0);
      int j = nbt.getInt("y", 0);
      int k = nbt.getInt("z", 0);
      int l = ChunkSectionPos.getSectionCoord(i);
      int m = ChunkSectionPos.getSectionCoord(k);
      if (l != chunkPos.x || m != chunkPos.z) {
         LOGGER.warn("Block entity {} found in a wrong chunk, expected position from chunk {}", nbt, chunkPos);
         i = chunkPos.getOffsetX(ChunkSectionPos.getLocalCoord(i));
         k = chunkPos.getOffsetZ(ChunkSectionPos.getLocalCoord(k));
      }

      return new BlockPos(i, j, k);
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public boolean hasWorld() {
      return this.world != null;
   }

   protected void readData(ReadView view) {
   }

   public final void read(ReadView view) {
      this.readData(view);
      this.components = (ComponentMap)view.read("components", ComponentMap.CODEC).orElse(ComponentMap.EMPTY);
   }

   public final void readComponentlessData(ReadView view) {
      this.readData(view);
   }

   protected void writeData(WriteView view) {
   }

   public final NbtCompound createNbtWithIdentifyingData(RegistryWrapper.WrapperLookup registries) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);

      NbtCompound var4;
      try {
         NbtWriteView nbtWriteView = NbtWriteView.create(logging, registries);
         this.writeFullData(nbtWriteView);
         var4 = nbtWriteView.getNbt();
      } catch (Throwable var6) {
         try {
            logging.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      logging.close();
      return var4;
   }

   public void writeFullData(WriteView view) {
      this.writeDataWithoutId(view);
      this.writeIdentifyingData(view);
   }

   public void writeDataWithId(WriteView view) {
      this.writeDataWithoutId(view);
      this.writeId(view);
   }

   public final NbtCompound createNbt(RegistryWrapper.WrapperLookup registries) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);

      NbtCompound var4;
      try {
         NbtWriteView nbtWriteView = NbtWriteView.create(logging, registries);
         this.writeDataWithoutId(nbtWriteView);
         var4 = nbtWriteView.getNbt();
      } catch (Throwable var6) {
         try {
            logging.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      logging.close();
      return var4;
   }

   public void writeDataWithoutId(WriteView data) {
      this.writeData(data);
      data.put("components", ComponentMap.CODEC, this.components);
   }

   public final NbtCompound createComponentlessNbt(RegistryWrapper.WrapperLookup registries) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);

      NbtCompound var4;
      try {
         NbtWriteView nbtWriteView = NbtWriteView.create(logging, registries);
         this.writeComponentlessData(nbtWriteView);
         var4 = nbtWriteView.getNbt();
      } catch (Throwable var6) {
         try {
            logging.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      logging.close();
      return var4;
   }

   public void writeComponentlessData(WriteView view) {
      this.writeData(view);
   }

   private void writeId(WriteView view) {
      writeId(view, this.getType());
   }

   public static void writeId(WriteView view, BlockEntityType type) {
      view.put("id", TYPE_CODEC, type);
   }

   private void writeIdentifyingData(WriteView view) {
      this.writeId(view);
      view.putInt("x", this.pos.getX());
      view.putInt("y", this.pos.getY());
      view.putInt("z", this.pos.getZ());
   }

   @Nullable
   public static BlockEntity createFromNbt(BlockPos pos, BlockState state, NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
      BlockEntityType blockEntityType = (BlockEntityType)nbt.get("id", TYPE_CODEC).orElse((Object)null);
      if (blockEntityType == null) {
         LOGGER.error("Skipping block entity with invalid type: {}", nbt.get("id"));
         return null;
      } else {
         BlockEntity blockEntity;
         try {
            blockEntity = blockEntityType.instantiate(pos, state);
         } catch (Throwable var12) {
            LOGGER.error("Failed to create block entity {} for block {} at position {} ", new Object[]{blockEntityType, pos, state, var12});
            return null;
         }

         try {
            ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER);

            BlockEntity var7;
            try {
               blockEntity.read(NbtReadView.create(logging, registries, nbt));
               var7 = blockEntity;
            } catch (Throwable var10) {
               try {
                  logging.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            logging.close();
            return var7;
         } catch (Throwable var11) {
            LOGGER.error("Failed to load data for block entity {} for block {} at position {}", new Object[]{blockEntityType, pos, state, var11});
            return null;
         }
      }
   }

   public void markDirty() {
      if (this.world != null) {
         markDirty(this.world, this.pos, this.cachedState);
      }

   }

   protected static void markDirty(World world, BlockPos pos, BlockState state) {
      world.markDirty(pos);
      if (!state.isAir()) {
         world.updateComparators(pos, state.getBlock());
      }

   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getCachedState() {
      return this.cachedState;
   }

   @Nullable
   public Packet toUpdatePacket() {
      return null;
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return new NbtCompound();
   }

   public boolean isRemoved() {
      return this.removed;
   }

   public void markRemoved() {
      this.removed = true;
   }

   public void cancelRemoval() {
      this.removed = false;
   }

   public void onBlockReplaced(BlockPos pos, BlockState oldState) {
      if (this instanceof Inventory inventory) {
         if (this.world != null) {
            ItemScatterer.spawn(this.world, pos, inventory);
         }
      }

   }

   public boolean onSyncedBlockEvent(int type, int data) {
      return false;
   }

   public void populateCrashReport(CrashReportSection crashReportSection) {
      crashReportSection.add("Name", this::getNameForReport);
      BlockState var10002 = this.getCachedState();
      Objects.requireNonNull(var10002);
      crashReportSection.add("Cached block", var10002::toString);
      if (this.world == null) {
         crashReportSection.add("Block location", () -> {
            return String.valueOf(this.pos) + " (world missing)";
         });
      } else {
         var10002 = this.world.getBlockState(this.pos);
         Objects.requireNonNull(var10002);
         crashReportSection.add("Actual block", var10002::toString);
         CrashReportSection.addBlockLocation(crashReportSection, this.world, this.pos);
      }

   }

   public String getNameForReport() {
      String var10000 = String.valueOf(Registries.BLOCK_ENTITY_TYPE.getId(this.getType()));
      return var10000 + " // " + this.getClass().getCanonicalName();
   }

   public BlockEntityType getType() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated
   public void setCachedState(BlockState state) {
      this.validateSupports(state);
      this.cachedState = state;
   }

   protected void readComponents(ComponentsAccess components) {
   }

   public final void readComponents(ItemStack stack) {
      this.readComponents(stack.getDefaultComponents(), stack.getComponentChanges());
   }

   public final void readComponents(ComponentMap defaultComponents, ComponentChanges components) {
      final Set set = new HashSet();
      set.add(DataComponentTypes.BLOCK_ENTITY_DATA);
      set.add(DataComponentTypes.BLOCK_STATE);
      final ComponentMap componentMap = MergedComponentMap.create(defaultComponents, components);
      this.readComponents(new ComponentsAccess(this) {
         @Nullable
         public Object get(ComponentType type) {
            set.add(type);
            return componentMap.get(type);
         }

         public Object getOrDefault(ComponentType type, Object fallback) {
            set.add(type);
            return componentMap.getOrDefault(type, fallback);
         }
      });
      Objects.requireNonNull(set);
      ComponentChanges componentChanges = components.withRemovedIf(set::contains);
      this.components = componentChanges.toAddedRemovedPair().added();
   }

   protected void addComponents(ComponentMap.Builder builder) {
   }

   /** @deprecated */
   @Deprecated
   public void removeFromCopiedStackData(WriteView view) {
   }

   public final ComponentMap createComponentMap() {
      ComponentMap.Builder builder = ComponentMap.builder();
      builder.addAll(this.components);
      this.addComponents(builder);
      return builder.build();
   }

   public ComponentMap getComponents() {
      return this.components;
   }

   public void setComponents(ComponentMap components) {
      this.components = components;
   }

   @Nullable
   public static Text tryParseCustomName(ReadView view, String key) {
      return (Text)view.read(key, TextCodecs.CODEC).orElse((Object)null);
   }

   public ErrorReporter.Context getReporterContext() {
      return new ReporterContext(this);
   }

   static {
      TYPE_CODEC = Registries.BLOCK_ENTITY_TYPE.getCodec();
      LOGGER = LogUtils.getLogger();
   }

   private static record ReporterContext(BlockEntity blockEntity) implements ErrorReporter.Context {
      ReporterContext(BlockEntity blockEntity) {
         this.blockEntity = blockEntity;
      }

      public String getName() {
         String var10000 = this.blockEntity.getNameForReport();
         return var10000 + "@" + String.valueOf(this.blockEntity.getPos());
      }

      public BlockEntity blockEntity() {
         return this.blockEntity;
      }
   }
}
