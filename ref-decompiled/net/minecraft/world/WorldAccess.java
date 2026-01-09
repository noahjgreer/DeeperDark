package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public interface WorldAccess extends RegistryWorldView, LunarWorldView, ScheduledTickView {
   default long getLunarTime() {
      return this.getLevelProperties().getTimeOfDay();
   }

   long getTickOrder();

   default OrderedTick createOrderedTick(BlockPos pos, Object type, int delay, TickPriority priority) {
      return new OrderedTick(type, pos, this.getLevelProperties().getTime() + (long)delay, priority, this.getTickOrder());
   }

   default OrderedTick createOrderedTick(BlockPos pos, Object type, int delay) {
      return new OrderedTick(type, pos, this.getLevelProperties().getTime() + (long)delay, this.getTickOrder());
   }

   WorldProperties getLevelProperties();

   LocalDifficulty getLocalDifficulty(BlockPos pos);

   @Nullable
   MinecraftServer getServer();

   default Difficulty getDifficulty() {
      return this.getLevelProperties().getDifficulty();
   }

   ChunkManager getChunkManager();

   default boolean isChunkLoaded(int chunkX, int chunkZ) {
      return this.getChunkManager().isChunkLoaded(chunkX, chunkZ);
   }

   Random getRandom();

   default void updateNeighbors(BlockPos pos, Block block) {
   }

   default void replaceWithStateForNeighborUpdate(Direction direction, BlockPos pos, BlockPos neighborPos, BlockState neighborState, int flags, int maxUpdateDepth) {
      NeighborUpdater.replaceWithStateForNeighborUpdate(this, direction, pos, neighborPos, neighborState, flags, maxUpdateDepth - 1);
   }

   default void playSound(@Nullable Entity source, BlockPos pos, SoundEvent sound, SoundCategory category) {
      this.playSound(source, pos, sound, category, 1.0F, 1.0F);
   }

   void playSound(@Nullable Entity source, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch);

   void addParticleClient(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

   void syncWorldEvent(@Nullable Entity source, int eventId, BlockPos pos, int data);

   default void syncWorldEvent(int eventId, BlockPos pos, int data) {
      this.syncWorldEvent((Entity)null, eventId, pos, data);
   }

   void emitGameEvent(RegistryEntry event, Vec3d emitterPos, GameEvent.Emitter emitter);

   default void emitGameEvent(@Nullable Entity entity, RegistryEntry event, Vec3d pos) {
      this.emitGameEvent(event, pos, new GameEvent.Emitter(entity, (BlockState)null));
   }

   default void emitGameEvent(@Nullable Entity entity, RegistryEntry event, BlockPos pos) {
      this.emitGameEvent(event, pos, new GameEvent.Emitter(entity, (BlockState)null));
   }

   default void emitGameEvent(RegistryEntry event, BlockPos pos, GameEvent.Emitter emitter) {
      this.emitGameEvent(event, Vec3d.ofCenter(pos), emitter);
   }

   default void emitGameEvent(RegistryKey event, BlockPos pos, GameEvent.Emitter emitter) {
      this.emitGameEvent((RegistryEntry)this.getRegistryManager().getOrThrow(RegistryKeys.GAME_EVENT).getOrThrow(event), (BlockPos)pos, (GameEvent.Emitter)emitter);
   }
}
