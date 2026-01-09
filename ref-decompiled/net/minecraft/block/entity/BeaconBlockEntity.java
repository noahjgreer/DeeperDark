package net.minecraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeaconBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Nameable, BeamEmitter {
   private static final int field_31304 = 4;
   public static final List EFFECTS_BY_LEVEL;
   private static final Set EFFECTS;
   public static final int LEVEL_PROPERTY_INDEX = 0;
   public static final int PRIMARY_PROPERTY_INDEX = 1;
   public static final int SECONDARY_PROPERTY_INDEX = 2;
   public static final int PROPERTY_COUNT = 3;
   private static final int field_31305 = 10;
   private static final Text CONTAINER_NAME_TEXT;
   private static final String PRIMARY_EFFECT_NBT_KEY = "primary_effect";
   private static final String SECONDARY_EFFECT_NBT_KEY = "secondary_effect";
   List beamSegments = new ArrayList();
   private List field_19178 = new ArrayList();
   int level;
   private int minY;
   @Nullable
   RegistryEntry primary;
   @Nullable
   RegistryEntry secondary;
   @Nullable
   private Text customName;
   private ContainerLock lock;
   private final PropertyDelegate propertyDelegate;

   @Nullable
   static RegistryEntry getEffectOrNull(@Nullable RegistryEntry effect) {
      return EFFECTS.contains(effect) ? effect : null;
   }

   public BeaconBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.BEACON, pos, state);
      this.lock = ContainerLock.EMPTY;
      this.propertyDelegate = new PropertyDelegate() {
         public int get(int index) {
            int var10000;
            switch (index) {
               case 0:
                  var10000 = BeaconBlockEntity.this.level;
                  break;
               case 1:
                  var10000 = BeaconScreenHandler.getRawIdForStatusEffect(BeaconBlockEntity.this.primary);
                  break;
               case 2:
                  var10000 = BeaconScreenHandler.getRawIdForStatusEffect(BeaconBlockEntity.this.secondary);
                  break;
               default:
                  var10000 = 0;
            }

            return var10000;
         }

         public void set(int index, int value) {
            switch (index) {
               case 0:
                  BeaconBlockEntity.this.level = value;
                  break;
               case 1:
                  if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
                     BeaconBlockEntity.playSound(BeaconBlockEntity.this.world, BeaconBlockEntity.this.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT);
                  }

                  BeaconBlockEntity.this.primary = BeaconBlockEntity.getEffectOrNull(BeaconScreenHandler.getStatusEffectForRawId(value));
                  break;
               case 2:
                  BeaconBlockEntity.this.secondary = BeaconBlockEntity.getEffectOrNull(BeaconScreenHandler.getStatusEffectForRawId(value));
            }

         }

         public int size() {
            return 3;
         }
      };
   }

   public static void tick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      BlockPos blockPos;
      if (blockEntity.minY < j) {
         blockPos = pos;
         blockEntity.field_19178 = Lists.newArrayList();
         blockEntity.minY = pos.getY() - 1;
      } else {
         blockPos = new BlockPos(i, blockEntity.minY + 1, k);
      }

      BeamEmitter.BeamSegment beamSegment = blockEntity.field_19178.isEmpty() ? null : (BeamEmitter.BeamSegment)blockEntity.field_19178.get(blockEntity.field_19178.size() - 1);
      int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);

      int m;
      for(m = 0; m < 10 && blockPos.getY() <= l; ++m) {
         BlockState blockState = world.getBlockState(blockPos);
         Block block = blockState.getBlock();
         if (block instanceof Stainable stainable) {
            int n = stainable.getColor().getEntityColor();
            if (blockEntity.field_19178.size() <= 1) {
               beamSegment = new BeamEmitter.BeamSegment(n);
               blockEntity.field_19178.add(beamSegment);
            } else if (beamSegment != null) {
               if (n == beamSegment.getColor()) {
                  beamSegment.increaseHeight();
               } else {
                  beamSegment = new BeamEmitter.BeamSegment(ColorHelper.average(beamSegment.getColor(), n));
                  blockEntity.field_19178.add(beamSegment);
               }
            }
         } else {
            if (beamSegment == null || blockState.getOpacity() >= 15 && !blockState.isOf(Blocks.BEDROCK)) {
               blockEntity.field_19178.clear();
               blockEntity.minY = l;
               break;
            }

            beamSegment.increaseHeight();
         }

         blockPos = blockPos.up();
         ++blockEntity.minY;
      }

      m = blockEntity.level;
      if (world.getTime() % 80L == 0L) {
         if (!blockEntity.beamSegments.isEmpty()) {
            blockEntity.level = updateLevel(world, i, j, k);
         }

         if (blockEntity.level > 0 && !blockEntity.beamSegments.isEmpty()) {
            applyPlayerEffects(world, pos, blockEntity.level, blockEntity.primary, blockEntity.secondary);
            playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
         }
      }

      if (blockEntity.minY >= l) {
         blockEntity.minY = world.getBottomY() - 1;
         boolean bl = m > 0;
         blockEntity.beamSegments = blockEntity.field_19178;
         if (!world.isClient) {
            boolean bl2 = blockEntity.level > 0;
            if (!bl && bl2) {
               playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);
               Iterator var17 = world.getNonSpectatingEntities(ServerPlayerEntity.class, (new Box((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).expand(10.0, 5.0, 10.0)).iterator();

               while(var17.hasNext()) {
                  ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var17.next();
                  Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, blockEntity.level);
               }
            } else if (bl && !bl2) {
               playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
            }
         }
      }

   }

   private static int updateLevel(World world, int x, int y, int z) {
      int i = 0;

      for(int j = 1; j <= 4; i = j++) {
         int k = y - j;
         if (k < world.getBottomY()) {
            break;
         }

         boolean bl = true;

         for(int l = x - j; l <= x + j && bl; ++l) {
            for(int m = z - j; m <= z + j; ++m) {
               if (!world.getBlockState(new BlockPos(l, k, m)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
                  bl = false;
                  break;
               }
            }
         }

         if (!bl) {
            break;
         }
      }

      return i;
   }

   public void markRemoved() {
      playSound(this.world, this.pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
      super.markRemoved();
   }

   private static void applyPlayerEffects(World world, BlockPos pos, int beaconLevel, @Nullable RegistryEntry primaryEffect, @Nullable RegistryEntry secondaryEffect) {
      if (!world.isClient && primaryEffect != null) {
         double d = (double)(beaconLevel * 10 + 10);
         int i = 0;
         if (beaconLevel >= 4 && Objects.equals(primaryEffect, secondaryEffect)) {
            i = 1;
         }

         int j = (9 + beaconLevel * 2) * 20;
         Box box = (new Box(pos)).expand(d).stretch(0.0, (double)world.getHeight(), 0.0);
         List list = world.getNonSpectatingEntities(PlayerEntity.class, box);
         Iterator var11 = list.iterator();

         PlayerEntity playerEntity;
         while(var11.hasNext()) {
            playerEntity = (PlayerEntity)var11.next();
            playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, i, true, true));
         }

         if (beaconLevel >= 4 && !Objects.equals(primaryEffect, secondaryEffect) && secondaryEffect != null) {
            var11 = list.iterator();

            while(var11.hasNext()) {
               playerEntity = (PlayerEntity)var11.next();
               playerEntity.addStatusEffect(new StatusEffectInstance(secondaryEffect, j, 0, true, true));
            }
         }

      }
   }

   public static void playSound(World world, BlockPos pos, SoundEvent sound) {
      world.playSound((Entity)null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public List getBeamSegments() {
      return (List)(this.level == 0 ? ImmutableList.of() : this.beamSegments);
   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   private static void writeStatusEffect(WriteView view, String key, @Nullable RegistryEntry effect) {
      if (effect != null) {
         effect.getKey().ifPresent((entryKey) -> {
            view.putString(key, entryKey.getValue().toString());
         });
      }

   }

   @Nullable
   private static RegistryEntry readStatusEffect(ReadView view, String key) {
      Optional var10000 = view.read(key, Registries.STATUS_EFFECT.getEntryCodec());
      Set var10001 = EFFECTS;
      Objects.requireNonNull(var10001);
      return (RegistryEntry)var10000.filter(var10001::contains).orElse((Object)null);
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.primary = readStatusEffect(view, "primary_effect");
      this.secondary = readStatusEffect(view, "secondary_effect");
      this.customName = tryParseCustomName(view, "CustomName");
      this.lock = ContainerLock.read(view);
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      writeStatusEffect(view, "primary_effect", this.primary);
      writeStatusEffect(view, "secondary_effect", this.secondary);
      view.putInt("Levels", this.level);
      view.putNullable("CustomName", TextCodecs.CODEC, this.customName);
      this.lock.write(view);
   }

   public void setCustomName(@Nullable Text customName) {
      this.customName = customName;
   }

   @Nullable
   public Text getCustomName() {
      return this.customName;
   }

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      return LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName()) ? new BeaconScreenHandler(i, playerInventory, this.propertyDelegate, ScreenHandlerContext.create(this.world, this.getPos())) : null;
   }

   public Text getDisplayName() {
      return this.getName();
   }

   public Text getName() {
      return this.customName != null ? this.customName : CONTAINER_NAME_TEXT;
   }

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
      this.lock = (ContainerLock)components.getOrDefault(DataComponentTypes.LOCK, ContainerLock.EMPTY);
   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
      if (!this.lock.equals(ContainerLock.EMPTY)) {
         builder.add(DataComponentTypes.LOCK, this.lock);
      }

   }

   public void removeFromCopiedStackData(WriteView view) {
      view.remove("CustomName");
      view.remove("lock");
   }

   public void setWorld(World world) {
      super.setWorld(world);
      this.minY = world.getBottomY() - 1;
   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }

   static {
      EFFECTS_BY_LEVEL = List.of(List.of(StatusEffects.SPEED, StatusEffects.HASTE), List.of(StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST), List.of(StatusEffects.STRENGTH), List.of(StatusEffects.REGENERATION));
      EFFECTS = (Set)EFFECTS_BY_LEVEL.stream().flatMap(Collection::stream).collect(Collectors.toSet());
      CONTAINER_NAME_TEXT = Text.translatable("container.beacon");
   }
}
