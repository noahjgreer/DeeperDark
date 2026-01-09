package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NetherPortalBlock extends Block implements Portal {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec CODEC = createCodec(NetherPortalBlock::new);
   public static final EnumProperty AXIS;
   private static final Map SHAPES_BY_AXIS;

   public MapCodec getCodec() {
      return CODEC;
   }

   public NetherPortalBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AXIS, Direction.Axis.X));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_AXIS.get(state.get(AXIS));
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.getDimension().natural() && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < world.getDifficulty().getId() && world.shouldTickBlockAt(pos)) {
         while(world.getBlockState(pos).isOf(this)) {
            pos = pos.down();
         }

         if (world.getBlockState(pos).allowsSpawning(world, pos, EntityType.ZOMBIFIED_PIGLIN)) {
            Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, pos.up(), SpawnReason.STRUCTURE);
            if (entity != null) {
               entity.resetPortalCooldown();
               Entity entity2 = entity.getVehicle();
               if (entity2 != null) {
                  entity2.resetPortalCooldown();
               }
            }
         }
      }

   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      Direction.Axis axis = direction.getAxis();
      Direction.Axis axis2 = (Direction.Axis)state.get(AXIS);
      boolean bl = axis2 != axis && axis.isHorizontal();
      return !bl && !neighborState.isOf(this) && !NetherPortal.getOnAxis(world, pos, axis2).wasAlreadyValid() ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (entity.canUsePortals(false)) {
         entity.tryUsePortal(this, pos);
      }

   }

   public int getPortalDelay(ServerWorld world, Entity entity) {
      if (entity instanceof PlayerEntity playerEntity) {
         return Math.max(0, world.getGameRules().getInt(playerEntity.getAbilities().invulnerable ? GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY));
      } else {
         return 0;
      }
   }

   @Nullable
   public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
      RegistryKey registryKey = world.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
      ServerWorld serverWorld = world.getServer().getWorld(registryKey);
      if (serverWorld == null) {
         return null;
      } else {
         boolean bl = serverWorld.getRegistryKey() == World.NETHER;
         WorldBorder worldBorder = serverWorld.getWorldBorder();
         double d = DimensionType.getCoordinateScaleFactor(world.getDimension(), serverWorld.getDimension());
         BlockPos blockPos = worldBorder.clampFloored(entity.getX() * d, entity.getY(), entity.getZ() * d);
         return this.getOrCreateExitPortalTarget(serverWorld, entity, pos, blockPos, bl, worldBorder);
      }
   }

   @Nullable
   private TeleportTarget getOrCreateExitPortalTarget(ServerWorld world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder) {
      Optional optional = world.getPortalForcer().getPortalPos(scaledPos, inNether, worldBorder);
      BlockLocating.Rectangle rectangle;
      TeleportTarget.PostDimensionTransition postDimensionTransition;
      if (optional.isPresent()) {
         BlockPos blockPos = (BlockPos)optional.get();
         BlockState blockState = world.getBlockState(blockPos);
         rectangle = BlockLocating.getLargestRectangle(blockPos, (Direction.Axis)blockState.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, (posx) -> {
            return world.getBlockState(posx) == blockState;
         });
         postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then((entityx) -> {
            entityx.addPortalChunkTicketAt(blockPos);
         });
      } else {
         Direction.Axis axis = (Direction.Axis)entity.getWorld().getBlockState(pos).getOrEmpty(AXIS).orElse(Direction.Axis.X);
         Optional optional2 = world.getPortalForcer().createPortal(scaledPos, axis);
         if (optional2.isEmpty()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
            return null;
         }

         rectangle = (BlockLocating.Rectangle)optional2.get();
         postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
      }

      return getExitPortalTarget(entity, pos, rectangle, world, postDimensionTransition);
   }

   private static TeleportTarget getExitPortalTarget(Entity entity, BlockPos pos, BlockLocating.Rectangle exitPortalRectangle, ServerWorld world, TeleportTarget.PostDimensionTransition postDimensionTransition) {
      BlockState blockState = entity.getWorld().getBlockState(pos);
      Direction.Axis axis;
      Vec3d vec3d;
      if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
         axis = (Direction.Axis)blockState.get(Properties.HORIZONTAL_AXIS);
         BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(pos, axis, 21, Direction.Axis.Y, 21, (posx) -> {
            return entity.getWorld().getBlockState(posx) == blockState;
         });
         vec3d = entity.positionInPortal(axis, rectangle);
      } else {
         axis = Direction.Axis.X;
         vec3d = new Vec3d(0.5, 0.0, 0.0);
      }

      return getExitPortalTarget(world, exitPortalRectangle, axis, vec3d, entity, postDimensionTransition);
   }

   private static TeleportTarget getExitPortalTarget(ServerWorld world, BlockLocating.Rectangle exitPortalRectangle, Direction.Axis axis, Vec3d positionInPortal, Entity entity, TeleportTarget.PostDimensionTransition postDimensionTransition) {
      BlockPos blockPos = exitPortalRectangle.lowerLeft;
      BlockState blockState = world.getBlockState(blockPos);
      Direction.Axis axis2 = (Direction.Axis)blockState.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
      double d = (double)exitPortalRectangle.width;
      double e = (double)exitPortalRectangle.height;
      EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
      int i = axis == axis2 ? 0 : 90;
      double f = (double)entityDimensions.width() / 2.0 + (d - (double)entityDimensions.width()) * positionInPortal.getX();
      double g = (e - (double)entityDimensions.height()) * positionInPortal.getY();
      double h = 0.5 + positionInPortal.getZ();
      boolean bl = axis2 == Direction.Axis.X;
      Vec3d vec3d = new Vec3d((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
      Vec3d vec3d2 = NetherPortal.findOpenPosition(vec3d, world, entity, entityDimensions);
      return new TeleportTarget(world, vec3d2, Vec3d.ZERO, (float)i, 0.0F, PositionFlag.combine(PositionFlag.DELTA, PositionFlag.ROT), postDimensionTransition);
   }

   public Portal.Effect getPortalEffect() {
      return Portal.Effect.CONFUSION;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (random.nextInt(100) == 0) {
         world.playSoundClient((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d = (double)pos.getX() + random.nextDouble();
         double e = (double)pos.getY() + random.nextDouble();
         double f = (double)pos.getZ() + random.nextDouble();
         double g = ((double)random.nextFloat() - 0.5) * 0.5;
         double h = ((double)random.nextFloat() - 0.5) * 0.5;
         double j = ((double)random.nextFloat() - 0.5) * 0.5;
         int k = random.nextInt(2) * 2 - 1;
         if (!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
            d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
            g = (double)(random.nextFloat() * 2.0F * (float)k);
         } else {
            f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
            j = (double)(random.nextFloat() * 2.0F * (float)k);
         }

         world.addParticleClient(ParticleTypes.PORTAL, d, e, f, g, h, j);
      }

   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return ItemStack.EMPTY;
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)state.get(AXIS)) {
               case X:
                  return (BlockState)state.with(AXIS, Direction.Axis.Z);
               case Z:
                  return (BlockState)state.with(AXIS, Direction.Axis.X);
               default:
                  return state;
            }
         default:
            return state;
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AXIS);
   }

   static {
      AXIS = Properties.HORIZONTAL_AXIS;
      SHAPES_BY_AXIS = VoxelShapes.createHorizontalAxisShapeMap(Block.createColumnShape(4.0, 16.0, 0.0, 16.0));
   }
}
