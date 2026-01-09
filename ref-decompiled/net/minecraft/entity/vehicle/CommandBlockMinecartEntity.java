package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
   static final TrackedData COMMAND;
   static final TrackedData LAST_OUTPUT;
   private final CommandBlockExecutor commandExecutor = new CommandExecutor();
   private static final int EXECUTE_TICK_COOLDOWN = 4;
   private int lastExecuted;

   public CommandBlockMinecartEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected Item asItem() {
      return Items.MINECART;
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.COMMAND_BLOCK_MINECART);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(COMMAND, "");
      builder.add(LAST_OUTPUT, ScreenTexts.EMPTY);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.commandExecutor.readData(view);
      this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
      this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      this.commandExecutor.writeData(view);
   }

   public BlockState getDefaultContainedBlock() {
      return Blocks.COMMAND_BLOCK.getDefaultState();
   }

   public CommandBlockExecutor getCommandExecutor() {
      return this.commandExecutor;
   }

   public void onActivatorRail(int x, int y, int z, boolean powered) {
      if (powered && this.age - this.lastExecuted >= 4) {
         this.getCommandExecutor().execute(this.getWorld());
         this.lastExecuted = this.age;
      }

   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      return this.commandExecutor.interact(player);
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (LAST_OUTPUT.equals(data)) {
         try {
            this.commandExecutor.setLastOutput((Text)this.getDataTracker().get(LAST_OUTPUT));
         } catch (Throwable var3) {
         }
      } else if (COMMAND.equals(data)) {
         this.commandExecutor.setCommand((String)this.getDataTracker().get(COMMAND));
      }

   }

   static {
      COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
      LAST_OUTPUT = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
   }

   public class CommandExecutor extends CommandBlockExecutor {
      public ServerWorld getWorld() {
         return (ServerWorld)CommandBlockMinecartEntity.this.getWorld();
      }

      public void markDirty() {
         CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
         CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
      }

      public Vec3d getPos() {
         return CommandBlockMinecartEntity.this.getPos();
      }

      public CommandBlockMinecartEntity getMinecart() {
         return CommandBlockMinecartEntity.this;
      }

      public ServerCommandSource getSource() {
         return new ServerCommandSource(this, CommandBlockMinecartEntity.this.getPos(), CommandBlockMinecartEntity.this.getRotationClient(), this.getWorld(), 2, this.getName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), this.getWorld().getServer(), CommandBlockMinecartEntity.this);
      }

      public boolean isEditable() {
         return !CommandBlockMinecartEntity.this.isRemoved();
      }
   }
}
