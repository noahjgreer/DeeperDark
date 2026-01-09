package net.minecraft.util.math.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class RandomSequencesState extends PersistentState {
   public static final PersistentStateType STATE_TYPE;
   private final long seed;
   private int salt;
   private boolean includeWorldSeed = true;
   private boolean includeSequenceId = true;
   private final Map sequences = new Object2ObjectOpenHashMap();

   public RandomSequencesState(long seed) {
      this.seed = seed;
   }

   private RandomSequencesState(long seed, int salt, boolean includeWorldSeed, boolean includeSequenceId, Map sequences) {
      this.seed = seed;
      this.salt = salt;
      this.includeWorldSeed = includeWorldSeed;
      this.includeSequenceId = includeSequenceId;
      this.sequences.putAll(sequences);
   }

   public static Codec createCodec(long seed) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(RecordCodecBuilder.point(seed), Codec.INT.fieldOf("salt").forGetter((state) -> {
            return state.salt;
         }), Codec.BOOL.optionalFieldOf("include_world_seed", true).forGetter((state) -> {
            return state.includeWorldSeed;
         }), Codec.BOOL.optionalFieldOf("include_sequence_id", true).forGetter((state) -> {
            return state.includeSequenceId;
         }), Codec.unboundedMap(Identifier.CODEC, RandomSequence.CODEC).fieldOf("sequences").forGetter((state) -> {
            return state.sequences;
         })).apply(instance, RandomSequencesState::new);
      });
   }

   public Random getOrCreate(Identifier id) {
      Random random = ((RandomSequence)this.sequences.computeIfAbsent(id, this::createSequence)).getSource();
      return new WrappedRandom(random);
   }

   private RandomSequence createSequence(Identifier id) {
      return this.createSequence(id, this.salt, this.includeWorldSeed, this.includeSequenceId);
   }

   private RandomSequence createSequence(Identifier id, int salt, boolean includeWorldSeed, boolean includeSequenceId) {
      long l = (includeWorldSeed ? this.seed : 0L) ^ (long)salt;
      return new RandomSequence(l, includeSequenceId ? Optional.of(id) : Optional.empty());
   }

   public void forEachSequence(BiConsumer consumer) {
      this.sequences.forEach(consumer);
   }

   public void setDefaultParameters(int salt, boolean includeWorldSeed, boolean includeSequenceId) {
      this.salt = salt;
      this.includeWorldSeed = includeWorldSeed;
      this.includeSequenceId = includeSequenceId;
   }

   public int resetAll() {
      int i = this.sequences.size();
      this.sequences.clear();
      return i;
   }

   public void reset(Identifier id) {
      this.sequences.put(id, this.createSequence(id));
   }

   public void reset(Identifier id, int salt, boolean includeWorldSeed, boolean includeSequenceId) {
      this.sequences.put(id, this.createSequence(id, salt, includeWorldSeed, includeSequenceId));
   }

   static {
      STATE_TYPE = new PersistentStateType("random_sequences", (state) -> {
         return new RandomSequencesState(state.worldSeed());
      }, (state) -> {
         return createCodec(state.worldSeed());
      }, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);
   }

   private class WrappedRandom implements Random {
      private final Random random;

      WrappedRandom(final Random random) {
         this.random = random;
      }

      public Random split() {
         RandomSequencesState.this.markDirty();
         return this.random.split();
      }

      public RandomSplitter nextSplitter() {
         RandomSequencesState.this.markDirty();
         return this.random.nextSplitter();
      }

      public void setSeed(long seed) {
         RandomSequencesState.this.markDirty();
         this.random.setSeed(seed);
      }

      public int nextInt() {
         RandomSequencesState.this.markDirty();
         return this.random.nextInt();
      }

      public int nextInt(int bound) {
         RandomSequencesState.this.markDirty();
         return this.random.nextInt(bound);
      }

      public long nextLong() {
         RandomSequencesState.this.markDirty();
         return this.random.nextLong();
      }

      public boolean nextBoolean() {
         RandomSequencesState.this.markDirty();
         return this.random.nextBoolean();
      }

      public float nextFloat() {
         RandomSequencesState.this.markDirty();
         return this.random.nextFloat();
      }

      public double nextDouble() {
         RandomSequencesState.this.markDirty();
         return this.random.nextDouble();
      }

      public double nextGaussian() {
         RandomSequencesState.this.markDirty();
         return this.random.nextGaussian();
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o instanceof WrappedRandom) {
            WrappedRandom wrappedRandom = (WrappedRandom)o;
            return this.random.equals(wrappedRandom.random);
         } else {
            return false;
         }
      }
   }
}
