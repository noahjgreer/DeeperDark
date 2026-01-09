package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Holder, Vibrations {
   private static final int DEFAULT_LAST_VIBRATION_FREQUENCY = 0;
   private Vibrations.ListenerData listenerData;
   private final Vibrations.VibrationListener listener;
   private final Vibrations.Callback callback;
   private int lastVibrationFrequency;

   protected SculkSensorBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
      this.lastVibrationFrequency = 0;
      this.callback = this.createCallback();
      this.listenerData = new Vibrations.ListenerData();
      this.listener = new Vibrations.VibrationListener(this);
   }

   public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
      this(BlockEntityType.SCULK_SENSOR, pos, state);
   }

   public Vibrations.Callback createCallback() {
      return new VibrationCallback(this.getPos());
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.lastVibrationFrequency = view.getInt("last_vibration_frequency", 0);
      this.listenerData = (Vibrations.ListenerData)view.read("listener", Vibrations.ListenerData.CODEC).orElseGet(Vibrations.ListenerData::new);
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      view.putInt("last_vibration_frequency", this.lastVibrationFrequency);
      view.put("listener", Vibrations.ListenerData.CODEC, this.listenerData);
   }

   public Vibrations.ListenerData getVibrationListenerData() {
      return this.listenerData;
   }

   public Vibrations.Callback getVibrationCallback() {
      return this.callback;
   }

   public int getLastVibrationFrequency() {
      return this.lastVibrationFrequency;
   }

   public void setLastVibrationFrequency(int lastVibrationFrequency) {
      this.lastVibrationFrequency = lastVibrationFrequency;
   }

   public Vibrations.VibrationListener getEventListener() {
      return this.listener;
   }

   // $FF: synthetic method
   public GameEventListener getEventListener() {
      return this.getEventListener();
   }

   protected class VibrationCallback implements Vibrations.Callback {
      public static final int RANGE = 8;
      protected final BlockPos pos;
      private final PositionSource positionSource;

      public VibrationCallback(final BlockPos pos) {
         this.pos = pos;
         this.positionSource = new BlockPositionSource(pos);
      }

      public int getRange() {
         return 8;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public boolean triggersAvoidCriterion() {
         return true;
      }

      public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry event, @Nullable GameEvent.Emitter emitter) {
         if (!pos.equals(this.pos) || !event.matches((RegistryEntry)GameEvent.BLOCK_DESTROY) && !event.matches((RegistryEntry)GameEvent.BLOCK_PLACE)) {
            return Vibrations.getFrequency(event) == 0 ? false : SculkSensorBlock.isInactive(SculkSensorBlockEntity.this.getCachedState());
         } else {
            return false;
         }
      }

      public void accept(ServerWorld world, BlockPos pos, RegistryEntry event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
         BlockState blockState = SculkSensorBlockEntity.this.getCachedState();
         if (SculkSensorBlock.isInactive(blockState)) {
            int i = Vibrations.getFrequency(event);
            SculkSensorBlockEntity.this.setLastVibrationFrequency(i);
            int j = Vibrations.getSignalStrength(distance, this.getRange());
            Block var11 = blockState.getBlock();
            if (var11 instanceof SculkSensorBlock) {
               SculkSensorBlock sculkSensorBlock = (SculkSensorBlock)var11;
               sculkSensorBlock.setActive(sourceEntity, world, this.pos, blockState, j, i);
            }
         }

      }

      public void onListen() {
         SculkSensorBlockEntity.this.markDirty();
      }

      public boolean requiresTickingChunksAround() {
         return true;
      }
   }
}
