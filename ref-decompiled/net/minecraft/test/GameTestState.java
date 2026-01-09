package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public class GameTestState {
   private final RegistryEntry.Reference instanceEntry;
   @Nullable
   private BlockPos testBlockPos;
   private final ServerWorld world;
   private final Collection listeners = Lists.newArrayList();
   private final int tickLimit;
   private final Collection timedTaskRunners = Lists.newCopyOnWriteArrayList();
   private final Object2LongMap ticksByRunnables = new Object2LongOpenHashMap();
   private boolean initialized;
   private boolean tickedOnce;
   private int tick;
   private boolean started;
   private final TestAttemptConfig testAttemptConfig;
   private final Stopwatch stopwatch = Stopwatch.createUnstarted();
   private boolean completed;
   private final BlockRotation rotation;
   @Nullable
   private TestException exception;
   @Nullable
   private TestInstanceBlockEntity blockEntity;

   public GameTestState(RegistryEntry.Reference instanceEntry, BlockRotation rotation, ServerWorld world, TestAttemptConfig testAttemptConfig) {
      this.instanceEntry = instanceEntry;
      this.world = world;
      this.testAttemptConfig = testAttemptConfig;
      this.tickLimit = ((TestInstance)instanceEntry.value()).getMaxTicks();
      this.rotation = rotation;
   }

   public void setTestBlockPos(@Nullable BlockPos testBlockPos) {
      this.testBlockPos = testBlockPos;
   }

   public GameTestState startCountdown(int additionalExpectedStopTime) {
      this.tick = -(((TestInstance)this.instanceEntry.value()).getSetupTicks() + additionalExpectedStopTime + 1);
      return this;
   }

   public void initializeImmediately() {
      if (!this.initialized) {
         TestInstanceBlockEntity testInstanceBlockEntity = this.getTestInstanceBlockEntity();
         if (!testInstanceBlockEntity.placeStructure()) {
            this.fail((Text)Text.translatable("test.error.structure.failure", testInstanceBlockEntity.getTestName().getString()));
         }

         this.initialized = true;
         testInstanceBlockEntity.placeBarriers();
         BlockBox blockBox = testInstanceBlockEntity.getBlockBox();
         this.world.getBlockTickScheduler().clearNextTicks(blockBox);
         this.world.clearUpdatesInArea(blockBox);
         this.listeners.forEach((listener) -> {
            listener.onStarted(this);
         });
      }
   }

   public void tick(TestRunContext context) {
      if (!this.isCompleted()) {
         if (!this.initialized) {
            this.fail((Text)Text.translatable("test.error.ticking_without_structure"));
         }

         if (this.blockEntity == null) {
            this.fail((Text)Text.translatable("test.error.missing_block_entity"));
         }

         if (this.exception != null) {
            this.complete();
         }

         if (!this.tickedOnce) {
            Stream var10000 = this.blockEntity.getBlockBox().streamChunkPos();
            ServerWorld var10001 = this.world;
            Objects.requireNonNull(var10001);
            if (!var10000.allMatch(var10001::shouldTickTestAt)) {
               return;
            }
         }

         this.tickedOnce = true;
         this.tickTests();
         if (this.isCompleted()) {
            if (this.exception != null) {
               this.listeners.forEach((listener) -> {
                  listener.onFailed(this, context);
               });
            } else {
               this.listeners.forEach((listener) -> {
                  listener.onPassed(this, context);
               });
            }
         }

      }
   }

   private void tickTests() {
      ++this.tick;
      if (this.tick >= 0) {
         if (!this.started) {
            this.start();
         }

         ObjectIterator objectIterator = this.ticksByRunnables.object2LongEntrySet().iterator();

         while(objectIterator.hasNext()) {
            Object2LongMap.Entry entry = (Object2LongMap.Entry)objectIterator.next();
            if (entry.getLongValue() <= (long)this.tick) {
               try {
                  ((Runnable)entry.getKey()).run();
               } catch (TestException var4) {
                  this.fail(var4);
               } catch (Exception var5) {
                  this.fail((TestException)(new UnknownTestException(var5)));
               }

               objectIterator.remove();
            }
         }

         if (this.tick > this.tickLimit) {
            if (this.timedTaskRunners.isEmpty()) {
               this.fail((TestException)(new TickLimitExceededException(Text.translatable("test.error.timeout.no_result", ((TestInstance)this.instanceEntry.value()).getMaxTicks()))));
            } else {
               this.timedTaskRunners.forEach((runner) -> {
                  runner.runReported(this.tick);
               });
               if (this.exception == null) {
                  this.fail((TestException)(new TickLimitExceededException(Text.translatable("test.error.timeout.no_sequences_finished", ((TestInstance)this.instanceEntry.value()).getMaxTicks()))));
               }
            }
         } else {
            this.timedTaskRunners.forEach((runner) -> {
               runner.runSilently(this.tick);
            });
         }

      }
   }

   private void start() {
      if (!this.started) {
         this.started = true;
         this.getTestInstanceBlockEntity().setRunning();

         try {
            ((TestInstance)this.instanceEntry.value()).start(new TestContext(this));
         } catch (TestException var2) {
            this.fail(var2);
         } catch (Exception var3) {
            this.fail((TestException)(new UnknownTestException(var3)));
         }

      }
   }

   public void runAtTick(long tick, Runnable runnable) {
      this.ticksByRunnables.put(runnable, tick);
   }

   public Identifier getId() {
      return this.instanceEntry.registryKey().getValue();
   }

   @Nullable
   public BlockPos getPos() {
      return this.testBlockPos;
   }

   public BlockPos getOrigin() {
      return this.blockEntity.getStartPos();
   }

   public Box getBoundingBox() {
      TestInstanceBlockEntity testInstanceBlockEntity = this.getTestInstanceBlockEntity();
      return testInstanceBlockEntity.getBox();
   }

   public TestInstanceBlockEntity getTestInstanceBlockEntity() {
      if (this.blockEntity == null) {
         if (this.testBlockPos == null) {
            throw new IllegalStateException("This GameTestInfo has no position");
         }

         BlockEntity var2 = this.world.getBlockEntity(this.testBlockPos);
         if (var2 instanceof TestInstanceBlockEntity) {
            TestInstanceBlockEntity testInstanceBlockEntity = (TestInstanceBlockEntity)var2;
            this.blockEntity = testInstanceBlockEntity;
         }

         if (this.blockEntity == null) {
            throw new IllegalStateException("Could not find a test instance block entity at the given coordinate " + String.valueOf(this.testBlockPos));
         }
      }

      return this.blockEntity;
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   public boolean isPassed() {
      return this.completed && this.exception == null;
   }

   public boolean isFailed() {
      return this.exception != null;
   }

   public boolean isStarted() {
      return this.started;
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public long getElapsedMilliseconds() {
      return this.stopwatch.elapsed(TimeUnit.MILLISECONDS);
   }

   private void complete() {
      if (!this.completed) {
         this.completed = true;
         if (this.stopwatch.isRunning()) {
            this.stopwatch.stop();
         }
      }

   }

   public void completeIfSuccessful() {
      if (this.exception == null) {
         this.complete();
         Box box = this.getBoundingBox();
         List list = this.getWorld().getEntitiesByClass(Entity.class, box.expand(1.0), (entity) -> {
            return !(entity instanceof PlayerEntity);
         });
         list.forEach((entity) -> {
            entity.remove(Entity.RemovalReason.DISCARDED);
         });
      }

   }

   public void fail(Text message) {
      this.fail((TestException)(new GameTestException(message, this.tick)));
   }

   public void fail(TestException exception) {
      this.exception = exception;
   }

   @Nullable
   public TestException getThrowable() {
      return this.exception;
   }

   public String toString() {
      return this.getId().toString();
   }

   public void addListener(TestListener listener) {
      this.listeners.add(listener);
   }

   @Nullable
   public GameTestState init() {
      TestInstanceBlockEntity testInstanceBlockEntity = this.placeTestInstance((BlockPos)Objects.requireNonNull(this.testBlockPos), this.rotation, this.world);
      if (testInstanceBlockEntity != null) {
         this.blockEntity = testInstanceBlockEntity;
         this.initializeImmediately();
         return this;
      } else {
         return null;
      }
   }

   @Nullable
   private TestInstanceBlockEntity placeTestInstance(BlockPos pos, BlockRotation rotation, ServerWorld world) {
      world.setBlockState(pos, Blocks.TEST_INSTANCE_BLOCK.getDefaultState());
      BlockEntity var5 = world.getBlockEntity(pos);
      if (var5 instanceof TestInstanceBlockEntity testInstanceBlockEntity) {
         RegistryKey registryKey = this.getInstanceEntry().registryKey();
         Vec3i vec3i = (Vec3i)TestInstanceBlockEntity.getStructureSize(world, registryKey).orElse(new Vec3i(1, 1, 1));
         testInstanceBlockEntity.setData(new TestInstanceBlockEntity.Data(Optional.of(registryKey), vec3i, rotation, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty()));
         return testInstanceBlockEntity;
      } else {
         return null;
      }
   }

   int getTick() {
      return this.tick;
   }

   TimedTaskRunner createTimedTaskRunner() {
      TimedTaskRunner timedTaskRunner = new TimedTaskRunner(this);
      this.timedTaskRunners.add(timedTaskRunner);
      return timedTaskRunner;
   }

   public boolean isRequired() {
      return ((TestInstance)this.instanceEntry.value()).isRequired();
   }

   public boolean isOptional() {
      return !((TestInstance)this.instanceEntry.value()).isRequired();
   }

   public Identifier getStructure() {
      return ((TestInstance)this.instanceEntry.value()).getStructure();
   }

   public BlockRotation getRotation() {
      return ((TestInstance)this.instanceEntry.value()).getData().rotation().rotate(this.rotation);
   }

   public TestInstance getInstance() {
      return (TestInstance)this.instanceEntry.value();
   }

   public RegistryEntry.Reference getInstanceEntry() {
      return this.instanceEntry;
   }

   public int getTickLimit() {
      return this.tickLimit;
   }

   public boolean isFlaky() {
      return ((TestInstance)this.instanceEntry.value()).getMaxAttempts() > 1;
   }

   public int getMaxAttempts() {
      return ((TestInstance)this.instanceEntry.value()).getMaxAttempts();
   }

   public int getRequiredSuccesses() {
      return ((TestInstance)this.instanceEntry.value()).getRequiredSuccesses();
   }

   public TestAttemptConfig getTestAttemptConfig() {
      return this.testAttemptConfig;
   }

   public Stream streamListeners() {
      return this.listeners.stream();
   }

   public GameTestState copy() {
      GameTestState gameTestState = new GameTestState(this.instanceEntry, this.rotation, this.world, this.getTestAttemptConfig());
      if (this.testBlockPos != null) {
         gameTestState.setTestBlockPos(this.testBlockPos);
      }

      return gameTestState;
   }
}
