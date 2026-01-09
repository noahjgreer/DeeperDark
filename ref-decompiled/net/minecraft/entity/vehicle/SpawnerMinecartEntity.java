package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
   private final MobSpawnerLogic logic = new MobSpawnerLogic() {
      public void sendStatus(World world, BlockPos pos, int status) {
         world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)status);
      }
   };
   private final Runnable ticker;

   public SpawnerMinecartEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.ticker = this.getTicker(world);
   }

   protected Item asItem() {
      return Items.MINECART;
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.MINECART);
   }

   private Runnable getTicker(World world) {
      return world instanceof ServerWorld ? () -> {
         this.logic.serverTick((ServerWorld)world, this.getBlockPos());
      } : () -> {
         this.logic.clientTick(world, this.getBlockPos());
      };
   }

   public BlockState getDefaultContainedBlock() {
      return Blocks.SPAWNER.getDefaultState();
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.logic.readData(this.getWorld(), this.getBlockPos(), view);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      this.logic.writeData(view);
   }

   public void handleStatus(byte status) {
      this.logic.handleStatus(this.getWorld(), status);
   }

   public void tick() {
      super.tick();
      this.ticker.run();
   }

   public MobSpawnerLogic getLogic() {
      return this.logic;
   }
}
