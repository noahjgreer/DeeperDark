package net.minecraft.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TestRunContext {
   public static final int DEFAULT_TESTS_PER_ROW = 8;
   private static final Logger LOGGER = LogUtils.getLogger();
   final ServerWorld world;
   private final TestManager manager;
   private final List states;
   private ImmutableList batches;
   final List batchListeners = Lists.newArrayList();
   private final List toBeRetried = Lists.newArrayList();
   private final Batcher batcher;
   private boolean stopped = true;
   @Nullable
   private RegistryEntry environment;
   private final TestStructureSpawner reuseSpawner;
   private final TestStructureSpawner initialSpawner;
   final boolean stopAfterFailure;

   protected TestRunContext(Batcher batcher, Collection batches, ServerWorld world, TestManager manager, TestStructureSpawner reuseSpawner, TestStructureSpawner initialSpawner, boolean stopAfterFailure) {
      this.world = world;
      this.manager = manager;
      this.batcher = batcher;
      this.reuseSpawner = reuseSpawner;
      this.initialSpawner = initialSpawner;
      this.batches = ImmutableList.copyOf(batches);
      this.stopAfterFailure = stopAfterFailure;
      this.states = (List)this.batches.stream().flatMap((batch) -> {
         return batch.states().stream();
      }).collect(Util.toArrayList());
      manager.setRunContext(this);
      this.states.forEach((state) -> {
         state.addListener(new StructureTestListener());
      });
   }

   public List getStates() {
      return this.states;
   }

   public void start() {
      this.stopped = false;
      this.runBatch(0);
   }

   public void clear() {
      this.stopped = true;
      if (this.environment != null) {
         this.clearEnvironment();
      }

   }

   public void retry(GameTestState state) {
      GameTestState gameTestState = state.copy();
      state.streamListeners().forEach((listener) -> {
         listener.onRetry(state, gameTestState, this);
      });
      this.states.add(gameTestState);
      this.toBeRetried.add(gameTestState);
      if (this.stopped) {
         this.onFinish();
      }

   }

   void runBatch(final int batchIndex) {
      if (batchIndex >= this.batches.size()) {
         this.clearEnvironment();
         this.onFinish();
      } else {
         final GameTestBatch gameTestBatch = (GameTestBatch)this.batches.get(batchIndex);
         this.reuseSpawner.onBatch(this.world);
         this.initialSpawner.onBatch(this.world);
         Collection collection = this.prepareStructures(gameTestBatch.states());
         LOGGER.info("Running test environment '{}' batch {} ({} tests)...", new Object[]{gameTestBatch.environment().getIdAsString(), gameTestBatch.index(), collection.size()});
         if (this.environment != gameTestBatch.environment()) {
            this.clearEnvironment();
            this.environment = gameTestBatch.environment();
            ((TestEnvironmentDefinition)this.environment.value()).setup(this.world);
         }

         this.batchListeners.forEach((listener) -> {
            listener.onStarted(gameTestBatch);
         });
         final TestSet testSet = new TestSet();
         Objects.requireNonNull(testSet);
         collection.forEach(testSet::add);
         testSet.addListener(new TestListener() {
            private void onFinished() {
               if (testSet.isDone()) {
                  TestRunContext.this.batchListeners.forEach((listener) -> {
                     listener.onFinished(gameTestBatch);
                  });
                  LongSet longSet = new LongArraySet(TestRunContext.this.world.getForcedChunks());
                  longSet.forEach((chunkPos) -> {
                     TestRunContext.this.world.setChunkForced(ChunkPos.getPackedX(chunkPos), ChunkPos.getPackedZ(chunkPos), false);
                  });
                  TestRunContext.this.runBatch(batchIndex + 1);
               }

            }

            public void onStarted(GameTestState test) {
            }

            public void onPassed(GameTestState test, TestRunContext context) {
               this.onFinished();
            }

            public void onFailed(GameTestState test, TestRunContext context) {
               if (TestRunContext.this.stopAfterFailure) {
                  TestRunContext.this.clearEnvironment();
                  LongSet longSet = new LongArraySet(TestRunContext.this.world.getForcedChunks());
                  longSet.forEach((chunkPos) -> {
                     TestRunContext.this.world.setChunkForced(ChunkPos.getPackedX(chunkPos), ChunkPos.getPackedZ(chunkPos), false);
                  });
                  TestManager.INSTANCE.clear();
               } else {
                  this.onFinished();
               }

            }

            public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
            }
         });
         TestManager var10001 = this.manager;
         Objects.requireNonNull(var10001);
         collection.forEach(var10001::start);
      }
   }

   void clearEnvironment() {
      if (this.environment != null) {
         ((TestEnvironmentDefinition)this.environment.value()).teardown(this.world);
         this.environment = null;
      }

   }

   private void onFinish() {
      if (!this.toBeRetried.isEmpty()) {
         LOGGER.info("Starting re-run of tests: {}", this.toBeRetried.stream().map((state) -> {
            return state.getId().toString();
         }).collect(Collectors.joining(", ")));
         this.batches = ImmutableList.copyOf(this.batcher.batch(this.toBeRetried));
         this.toBeRetried.clear();
         this.stopped = false;
         this.runBatch(0);
      } else {
         this.batches = ImmutableList.of();
         this.stopped = true;
      }

   }

   public void addBatchListener(BatchListener batchListener) {
      this.batchListeners.add(batchListener);
   }

   private Collection prepareStructures(Collection oldStates) {
      return oldStates.stream().map(this::prepareStructure).flatMap(Optional::stream).toList();
   }

   private Optional prepareStructure(GameTestState oldState) {
      return oldState.getPos() == null ? this.initialSpawner.spawnStructure(oldState) : this.reuseSpawner.spawnStructure(oldState);
   }

   public static void clearDebugMarkers(ServerWorld world) {
      DebugInfoSender.clearGameTestMarkers(world);
   }

   public interface Batcher {
      Collection batch(Collection states);
   }

   public interface TestStructureSpawner {
      TestStructureSpawner REUSE = (oldState) -> {
         return Optional.ofNullable(oldState.init()).map((gameTestState) -> {
            return gameTestState.startCountdown(1);
         });
      };
      TestStructureSpawner NOOP = (oldState) -> {
         return Optional.empty();
      };

      Optional spawnStructure(GameTestState oldState);

      default void onBatch(ServerWorld world) {
      }
   }

   public static class Builder {
      private final ServerWorld world;
      private final TestManager manager;
      private Batcher batcher;
      private TestStructureSpawner reuseSpawner;
      private TestStructureSpawner initialSpawner;
      private final Collection batches;
      private boolean stopAfterFailure;

      private Builder(Collection batches, ServerWorld world) {
         this.manager = TestManager.INSTANCE;
         this.batcher = Batches.defaultBatcher();
         this.reuseSpawner = TestRunContext.TestStructureSpawner.REUSE;
         this.initialSpawner = TestRunContext.TestStructureSpawner.NOOP;
         this.stopAfterFailure = false;
         this.batches = batches;
         this.world = world;
      }

      public static Builder of(Collection batches, ServerWorld world) {
         return new Builder(batches, world);
      }

      public static Builder ofStates(Collection states, ServerWorld world) {
         return of(Batches.defaultBatcher().batch(states), world);
      }

      public Builder stopAfterFailure(boolean stopAfterFailure) {
         this.stopAfterFailure = stopAfterFailure;
         return this;
      }

      public Builder initialSpawner(TestStructureSpawner initialSpawner) {
         this.initialSpawner = initialSpawner;
         return this;
      }

      public Builder reuseSpawner(TestStructurePlacer reuseSpawner) {
         this.reuseSpawner = reuseSpawner;
         return this;
      }

      public Builder batcher(Batcher batcher) {
         this.batcher = batcher;
         return this;
      }

      public TestRunContext build() {
         return new TestRunContext(this.batcher, this.batches, this.world, this.manager, this.reuseSpawner, this.initialSpawner, this.stopAfterFailure);
      }
   }
}
