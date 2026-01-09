package net.minecraft.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity extends BlockEntity {
   private static final String PROFILE_NBT_KEY = "profile";
   private static final String NOTE_BLOCK_SOUND_NBT_KEY = "note_block_sound";
   private static final String CUSTOM_NAME_NBT_KEY = "custom_name";
   @Nullable
   private static Executor currentExecutor;
   @Nullable
   private static LoadingCache nameToProfileCache;
   @Nullable
   private static LoadingCache uuidToProfileCache;
   public static final Executor EXECUTOR = (runnable) -> {
      Executor executor = currentExecutor;
      if (executor != null) {
         executor.execute(runnable);
      }

   };
   @Nullable
   private ProfileComponent owner;
   @Nullable
   private Identifier noteBlockSound;
   private int poweredTicks;
   private boolean powered;
   @Nullable
   private Text customName;

   public SkullBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.SKULL, pos, state);
   }

   public static void setServices(final ApiServices apiServices, Executor executor) {
      currentExecutor = executor;
      final BooleanSupplier booleanSupplier = () -> {
         return uuidToProfileCache == null;
      };
      nameToProfileCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader() {
         public CompletableFuture load(String string) {
            return SkullBlockEntity.fetchProfileByName(string, apiServices);
         }

         // $FF: synthetic method
         public Object load(final Object name) throws Exception {
            return this.load((String)name);
         }
      });
      uuidToProfileCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader() {
         public CompletableFuture load(UUID uUID) {
            return SkullBlockEntity.fetchProfileByUuid(uUID, apiServices, booleanSupplier);
         }

         // $FF: synthetic method
         public Object load(final Object uuid) throws Exception {
            return this.load((UUID)uuid);
         }
      });
   }

   static CompletableFuture fetchProfileByName(String name, ApiServices apiServices) {
      return apiServices.userCache().findByNameAsync(name).thenCompose((optional) -> {
         LoadingCache loadingCache = uuidToProfileCache;
         return loadingCache != null && !optional.isEmpty() ? ((CompletableFuture)loadingCache.getUnchecked(((GameProfile)optional.get()).getId())).thenApply((optional2) -> {
            return optional2.or(() -> {
               return optional;
            });
         }) : CompletableFuture.completedFuture(Optional.empty());
      });
   }

   static CompletableFuture fetchProfileByUuid(UUID uuid, ApiServices apiServices, BooleanSupplier booleanSupplier) {
      return CompletableFuture.supplyAsync(() -> {
         if (booleanSupplier.getAsBoolean()) {
            return Optional.empty();
         } else {
            ProfileResult profileResult = apiServices.sessionService().fetchProfile(uuid, true);
            return Optional.ofNullable(profileResult).map(ProfileResult::profile);
         }
      }, Util.getMainWorkerExecutor().named("fetchProfile"));
   }

   public static void clearServices() {
      currentExecutor = null;
      nameToProfileCache = null;
      uuidToProfileCache = null;
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      view.putNullable("profile", ProfileComponent.CODEC, this.owner);
      view.putNullable("note_block_sound", Identifier.CODEC, this.noteBlockSound);
      view.putNullable("custom_name", TextCodecs.CODEC, this.customName);
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.setOwner((ProfileComponent)view.read("profile", ProfileComponent.CODEC).orElse((Object)null));
      this.noteBlockSound = (Identifier)view.read("note_block_sound", Identifier.CODEC).orElse((Object)null);
      this.customName = tryParseCustomName(view, "custom_name");
   }

   public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
      if (state.contains(SkullBlock.POWERED) && (Boolean)state.get(SkullBlock.POWERED)) {
         blockEntity.powered = true;
         ++blockEntity.poweredTicks;
      } else {
         blockEntity.powered = false;
      }

   }

   public float getPoweredTicks(float tickProgress) {
      return this.powered ? (float)this.poweredTicks + tickProgress : (float)this.poweredTicks;
   }

   @Nullable
   public ProfileComponent getOwner() {
      return this.owner;
   }

   @Nullable
   public Identifier getNoteBlockSound() {
      return this.noteBlockSound;
   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   public void setOwner(@Nullable ProfileComponent profile) {
      synchronized(this) {
         this.owner = profile;
      }

      this.loadOwnerProperties();
   }

   private void loadOwnerProperties() {
      if (this.owner != null && !this.owner.isCompleted()) {
         this.owner.getFuture().thenAcceptAsync((owner) -> {
            this.owner = owner;
            this.markDirty();
         }, EXECUTOR);
      } else {
         this.markDirty();
      }
   }

   public static CompletableFuture fetchProfileByName(String name) {
      LoadingCache loadingCache = nameToProfileCache;
      return loadingCache != null && StringHelper.isValidPlayerName(name) ? (CompletableFuture)loadingCache.getUnchecked(name) : CompletableFuture.completedFuture(Optional.empty());
   }

   public static CompletableFuture fetchProfileByUuid(UUID uuid) {
      LoadingCache loadingCache = uuidToProfileCache;
      return loadingCache != null ? (CompletableFuture)loadingCache.getUnchecked(uuid) : CompletableFuture.completedFuture(Optional.empty());
   }

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      this.setOwner((ProfileComponent)components.get(DataComponentTypes.PROFILE));
      this.noteBlockSound = (Identifier)components.get(DataComponentTypes.NOTE_BLOCK_SOUND);
      this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      builder.add(DataComponentTypes.PROFILE, this.owner);
      builder.add(DataComponentTypes.NOTE_BLOCK_SOUND, this.noteBlockSound);
      builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
   }

   public void removeFromCopiedStackData(WriteView view) {
      super.removeFromCopiedStackData(view);
      view.remove("profile");
      view.remove("note_block_sound");
      view.remove("custom_name");
   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }
}
