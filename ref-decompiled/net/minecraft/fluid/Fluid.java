package net.minecraft.fluid;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class Fluid {
   public static final IdList STATE_IDS = new IdList();
   protected final StateManager stateManager;
   private FluidState defaultState;
   private final RegistryEntry.Reference registryEntry;

   protected Fluid() {
      this.registryEntry = Registries.FLUID.createEntry(this);
      StateManager.Builder builder = new StateManager.Builder(this);
      this.appendProperties(builder);
      this.stateManager = builder.build(Fluid::getDefaultState, FluidState::new);
      this.setDefaultState((FluidState)this.stateManager.getDefaultState());
   }

   protected void appendProperties(StateManager.Builder builder) {
   }

   public StateManager getStateManager() {
      return this.stateManager;
   }

   protected final void setDefaultState(FluidState state) {
      this.defaultState = state;
   }

   public final FluidState getDefaultState() {
      return this.defaultState;
   }

   public abstract Item getBucketItem();

   protected void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
   }

   protected void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState fluidState) {
   }

   protected void onRandomTick(ServerWorld world, BlockPos pos, FluidState state, Random random) {
   }

   protected void onEntityCollision(World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
   }

   @Nullable
   protected ParticleEffect getParticle() {
      return null;
   }

   protected abstract boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction);

   protected abstract Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state);

   public abstract int getTickRate(WorldView world);

   protected boolean hasRandomTicks() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getBlastResistance();

   public abstract float getHeight(FluidState state, BlockView world, BlockPos pos);

   public abstract float getHeight(FluidState state);

   protected abstract BlockState toBlockState(FluidState state);

   public abstract boolean isStill(FluidState state);

   public abstract int getLevel(FluidState state);

   public boolean matchesType(Fluid fluid) {
      return fluid == this;
   }

   /** @deprecated */
   @Deprecated
   public boolean isIn(TagKey tag) {
      return this.registryEntry.isIn(tag);
   }

   public abstract VoxelShape getShape(FluidState state, BlockView world, BlockPos pos);

   @Nullable
   public Box getCollisionBox(FluidState state, BlockView world, BlockPos pos) {
      if (this.isEmpty()) {
         return null;
      } else {
         float f = state.getHeight(world, pos);
         return new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)pos.getX() + 1.0, (double)((float)pos.getY() + f), (double)pos.getZ() + 1.0);
      }
   }

   public Optional getBucketFillSound() {
      return Optional.empty();
   }

   /** @deprecated */
   @Deprecated
   public RegistryEntry.Reference getRegistryEntry() {
      return this.registryEntry;
   }
}
