package net.minecraft.entity.decoration;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class LeashKnotEntity extends BlockAttachedEntity {
   public static final double field_30455 = 0.375;

   public LeashKnotEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public LeashKnotEntity(World world, BlockPos pos) {
      super(EntityType.LEASH_KNOT, world, pos);
      this.setPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   protected void initDataTracker(DataTracker.Builder builder) {
   }

   protected void updateAttachmentPosition() {
      this.setPos((double)this.attachedBlockPos.getX() + 0.5, (double)this.attachedBlockPos.getY() + 0.375, (double)this.attachedBlockPos.getZ() + 0.5);
      double d = (double)this.getType().getWidth() / 2.0;
      double e = (double)this.getType().getHeight();
      this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + e, this.getZ() + d));
   }

   public boolean shouldRender(double distance) {
      return distance < 1024.0;
   }

   public void onBreak(ServerWorld world, @Nullable Entity breaker) {
      this.playSound(SoundEvents.ITEM_LEAD_UNTIED, 1.0F, 1.0F);
   }

   protected void writeCustomData(WriteView view) {
   }

   protected void readCustomData(ReadView view) {
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      if (this.getWorld().isClient) {
         return ActionResult.SUCCESS;
      } else {
         if (player.getStackInHand(hand).isOf(Items.SHEARS)) {
            ActionResult actionResult = super.interact(player, hand);
            if (actionResult instanceof ActionResult.Success) {
               ActionResult.Success success = (ActionResult.Success)actionResult;
               if (success.shouldIncrementStat()) {
                  return actionResult;
               }
            }
         }

         boolean bl = false;
         List list = Leashable.collectLeashablesHeldBy(player);
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Leashable leashable = (Leashable)var5.next();
            if (leashable.canBeLeashedTo(this)) {
               leashable.attachLeash(this, true);
               bl = true;
            }
         }

         boolean bl2 = false;
         if (!bl && !player.shouldCancelInteraction()) {
            List list2 = Leashable.collectLeashablesHeldBy(this);
            Iterator var7 = list2.iterator();

            while(var7.hasNext()) {
               Leashable leashable2 = (Leashable)var7.next();
               if (leashable2.canBeLeashedTo(player)) {
                  leashable2.attachLeash(player, true);
                  bl2 = true;
               }
            }
         }

         if (!bl && !bl2) {
            return super.interact(player, hand);
         } else {
            this.emitGameEvent(GameEvent.BLOCK_ATTACH, player);
            this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_TIED);
            return ActionResult.SUCCESS;
         }
      }
   }

   public void onHeldLeashUpdate(Leashable heldLeashable) {
      if (Leashable.collectLeashablesHeldBy(this).isEmpty()) {
         this.discard();
      }

   }

   public boolean canStayAttached() {
      return this.getWorld().getBlockState(this.attachedBlockPos).isIn(BlockTags.FENCES);
   }

   public static LeashKnotEntity getOrCreate(World world, BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      List list = world.getNonSpectatingEntities(LeashKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0));
      Iterator var6 = list.iterator();

      LeashKnotEntity leashKnotEntity;
      do {
         if (!var6.hasNext()) {
            LeashKnotEntity leashKnotEntity2 = new LeashKnotEntity(world, pos);
            world.spawnEntity(leashKnotEntity2);
            return leashKnotEntity2;
         }

         leashKnotEntity = (LeashKnotEntity)var6.next();
      } while(!leashKnotEntity.getAttachedBlockPos().equals(pos));

      return leashKnotEntity;
   }

   public void onPlace() {
      this.playSound(SoundEvents.ITEM_LEAD_TIED, 1.0F, 1.0F);
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      return new EntitySpawnS2CPacket(this, 0, this.getAttachedBlockPos());
   }

   public Vec3d getLeashPos(float tickProgress) {
      return this.getLerpedPos(tickProgress).add(0.0, 0.2, 0.0);
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.LEAD);
   }
}
