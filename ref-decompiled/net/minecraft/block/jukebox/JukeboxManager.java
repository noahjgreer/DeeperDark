package net.minecraft.block.jukebox;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class JukeboxManager {
   public static final int TICKS_PER_SECOND = 20;
   private long ticksSinceSongStarted;
   @Nullable
   private RegistryEntry song;
   private final BlockPos pos;
   private final ChangeNotifier changeNotifier;

   public JukeboxManager(ChangeNotifier changeNotifier, BlockPos pos) {
      this.changeNotifier = changeNotifier;
      this.pos = pos;
   }

   public boolean isPlaying() {
      return this.song != null;
   }

   @Nullable
   public JukeboxSong getSong() {
      return this.song == null ? null : (JukeboxSong)this.song.value();
   }

   public long getTicksSinceSongStarted() {
      return this.ticksSinceSongStarted;
   }

   public void setValues(RegistryEntry song, long ticksPlaying) {
      if (!((JukeboxSong)song.value()).shouldStopPlaying(ticksPlaying)) {
         this.song = song;
         this.ticksSinceSongStarted = ticksPlaying;
      }
   }

   public void startPlaying(WorldAccess world, RegistryEntry song) {
      this.song = song;
      this.ticksSinceSongStarted = 0L;
      int i = world.getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG).getRawId((JukeboxSong)this.song.value());
      world.syncWorldEvent((Entity)null, 1010, this.pos, i);
      this.changeNotifier.notifyChange();
   }

   public void stopPlaying(WorldAccess world, @Nullable BlockState state) {
      if (this.song != null) {
         this.song = null;
         this.ticksSinceSongStarted = 0L;
         world.emitGameEvent((RegistryEntry)GameEvent.JUKEBOX_STOP_PLAY, (BlockPos)this.pos, (GameEvent.Emitter)GameEvent.Emitter.of(state));
         world.syncWorldEvent(1011, this.pos, 0);
         this.changeNotifier.notifyChange();
      }
   }

   public void tick(WorldAccess world, @Nullable BlockState state) {
      if (this.song != null) {
         if (((JukeboxSong)this.song.value()).shouldStopPlaying(this.ticksSinceSongStarted)) {
            this.stopPlaying(world, state);
         } else {
            if (this.hasSecondPassed()) {
               world.emitGameEvent((RegistryEntry)GameEvent.JUKEBOX_PLAY, (BlockPos)this.pos, (GameEvent.Emitter)GameEvent.Emitter.of(state));
               spawnNoteParticles(world, this.pos);
            }

            ++this.ticksSinceSongStarted;
         }
      }
   }

   private boolean hasSecondPassed() {
      return this.ticksSinceSongStarted % 20L == 0L;
   }

   private static void spawnNoteParticles(WorldAccess world, BlockPos pos) {
      if (world instanceof ServerWorld serverWorld) {
         Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2000000476837158, 0.0);
         float f = (float)world.getRandom().nextInt(4) / 24.0F;
         serverWorld.spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, (double)f, 0.0, 0.0, 1.0);
      }

   }

   @FunctionalInterface
   public interface ChangeNotifier {
      void notifyChange();
   }
}
