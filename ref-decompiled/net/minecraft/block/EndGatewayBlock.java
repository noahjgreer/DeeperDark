package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class EndGatewayBlock extends BlockWithEntity implements Portal {
   public static final MapCodec CODEC = createCodec(EndGatewayBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public EndGatewayBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new EndGatewayBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return validateTicker(type, BlockEntityType.END_GATEWAY, world.isClient ? EndGatewayBlockEntity::clientTick : EndGatewayBlockEntity::serverTick);
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof EndGatewayBlockEntity) {
         int i = ((EndGatewayBlockEntity)blockEntity).getDrawnSidesCount();

         for(int j = 0; j < i; ++j) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = (random.nextDouble() - 0.5) * 0.5;
            double h = (random.nextDouble() - 0.5) * 0.5;
            double k = (random.nextDouble() - 0.5) * 0.5;
            int l = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
               f = (double)pos.getZ() + 0.5 + 0.25 * (double)l;
               k = (double)(random.nextFloat() * 2.0F * (float)l);
            } else {
               d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
               g = (double)(random.nextFloat() * 2.0F * (float)l);
            }

            world.addParticleClient(ParticleTypes.PORTAL, d, e, f, g, h, k);
         }

      }
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return ItemStack.EMPTY;
   }

   protected boolean canBucketPlace(BlockState state, Fluid fluid) {
      return false;
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (entity.canUsePortals(false)) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (!world.isClient && blockEntity instanceof EndGatewayBlockEntity) {
            EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
            if (!endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
               entity.tryUsePortal(this, pos);
               EndGatewayBlockEntity.startTeleportCooldown(world, pos, state, endGatewayBlockEntity);
            }
         }
      }

   }

   @Nullable
   public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof EndGatewayBlockEntity endGatewayBlockEntity) {
         Vec3d vec3d = endGatewayBlockEntity.getOrCreateExitPortalPos(world, pos);
         if (vec3d == null) {
            return null;
         } else {
            return entity instanceof EnderPearlEntity ? new TeleportTarget(world, vec3d, Vec3d.ZERO, 0.0F, 0.0F, Set.of(), TeleportTarget.ADD_PORTAL_CHUNK_TICKET) : new TeleportTarget(world, vec3d, Vec3d.ZERO, 0.0F, 0.0F, PositionFlag.combine(PositionFlag.DELTA, PositionFlag.ROT), TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
         }
      } else {
         return null;
      }
   }

   protected BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }
}
