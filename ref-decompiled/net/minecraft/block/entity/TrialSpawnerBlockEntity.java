package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TrialSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawnerLogic.TrialSpawner {
   private final TrialSpawnerLogic logic = this.createDefaultLogic();

   public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.TRIAL_SPAWNER, pos, state);
   }

   private TrialSpawnerLogic createDefaultLogic() {
      EntityDetector entityDetector = EntityDetector.SURVIVAL_PLAYERS;
      EntityDetector.Selector selector = EntityDetector.Selector.IN_WORLD;
      return new TrialSpawnerLogic(TrialSpawnerLogic.FullConfig.DEFAULT, this, entityDetector, selector);
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.logic.readData(view);
      if (this.world != null) {
         this.updateListeners();
      }

   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      this.logic.writeData(view);
   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.logic.getData().getSpawnDataNbt((TrialSpawnerState)this.getCachedState().get(TrialSpawnerBlock.TRIAL_SPAWNER_STATE));
   }

   public void setEntityType(EntityType type, Random random) {
      if (this.world == null) {
         Util.logErrorOrPause("Expected non-null level");
      } else {
         this.logic.setEntityType(type, this.world);
         this.markDirty();
      }
   }

   public TrialSpawnerLogic getSpawner() {
      return this.logic;
   }

   public TrialSpawnerState getSpawnerState() {
      return !this.getCachedState().contains(Properties.TRIAL_SPAWNER_STATE) ? TrialSpawnerState.INACTIVE : (TrialSpawnerState)this.getCachedState().get(Properties.TRIAL_SPAWNER_STATE);
   }

   public void setSpawnerState(World world, TrialSpawnerState spawnerState) {
      this.markDirty();
      world.setBlockState(this.pos, (BlockState)this.getCachedState().with(Properties.TRIAL_SPAWNER_STATE, spawnerState));
   }

   public void updateListeners() {
      this.markDirty();
      if (this.world != null) {
         this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
      }

   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }
}
