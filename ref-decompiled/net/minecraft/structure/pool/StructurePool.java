package net.minecraft.structure.pool;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructurePool {
   private static final int DEFAULT_Y = Integer.MIN_VALUE;
   private static final MutableObject FALLBACK = new MutableObject();
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      MutableObject var10001 = FALLBACK;
      Objects.requireNonNull(var10001);
      return instance.group(Codec.lazyInitialized(var10001::getValue).fieldOf("fallback").forGetter(StructurePool::getFallback), Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((pool) -> {
         return pool.elementWeights;
      })).apply(instance, StructurePool::new);
   });
   public static final Codec REGISTRY_CODEC;
   private final List elementWeights;
   private final ObjectArrayList elements;
   private final RegistryEntry fallback;
   private int highestY = Integer.MIN_VALUE;

   public StructurePool(RegistryEntry fallback, List elementWeights) {
      this.elementWeights = elementWeights;
      this.elements = new ObjectArrayList();
      Iterator var3 = elementWeights.iterator();

      while(var3.hasNext()) {
         Pair pair = (Pair)var3.next();
         StructurePoolElement structurePoolElement = (StructurePoolElement)pair.getFirst();

         for(int i = 0; i < (Integer)pair.getSecond(); ++i) {
            this.elements.add(structurePoolElement);
         }
      }

      this.fallback = fallback;
   }

   public StructurePool(RegistryEntry fallback, List elementWeightsByGetters, Projection projection) {
      this.elementWeights = Lists.newArrayList();
      this.elements = new ObjectArrayList();
      Iterator var4 = elementWeightsByGetters.iterator();

      while(var4.hasNext()) {
         Pair pair = (Pair)var4.next();
         StructurePoolElement structurePoolElement = (StructurePoolElement)((Function)pair.getFirst()).apply(projection);
         this.elementWeights.add(Pair.of(structurePoolElement, (Integer)pair.getSecond()));

         for(int i = 0; i < (Integer)pair.getSecond(); ++i) {
            this.elements.add(structurePoolElement);
         }
      }

      this.fallback = fallback;
   }

   public int getHighestY(StructureTemplateManager structureTemplateManager) {
      if (this.highestY == Integer.MIN_VALUE) {
         this.highestY = this.elements.stream().filter((element) -> {
            return element != EmptyPoolElement.INSTANCE;
         }).mapToInt((element) -> {
            return element.getBoundingBox(structureTemplateManager, BlockPos.ORIGIN, BlockRotation.NONE).getBlockCountY();
         }).max().orElse(0);
      }

      return this.highestY;
   }

   @VisibleForTesting
   public List getElementWeights() {
      return this.elementWeights;
   }

   public RegistryEntry getFallback() {
      return this.fallback;
   }

   public StructurePoolElement getRandomElement(Random random) {
      return (StructurePoolElement)(this.elements.isEmpty() ? EmptyPoolElement.INSTANCE : (StructurePoolElement)this.elements.get(random.nextInt(this.elements.size())));
   }

   public List getElementIndicesInRandomOrder(Random random) {
      return Util.copyShuffled(this.elements, random);
   }

   public int getElementCount() {
      return this.elements.size();
   }

   static {
      RegistryElementCodec var10000 = RegistryElementCodec.of(RegistryKeys.TEMPLATE_POOL, CODEC);
      MutableObject var10001 = FALLBACK;
      Objects.requireNonNull(var10001);
      REGISTRY_CODEC = (Codec)Util.make(var10000, var10001::setValue);
   }

   public static enum Projection implements StringIdentifiable {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(Projection::values);
      private final String id;
      private final ImmutableList processors;

      private Projection(final String id, final ImmutableList processors) {
         this.id = id;
         this.processors = processors;
      }

      public String getId() {
         return this.id;
      }

      public static Projection getById(String id) {
         return (Projection)CODEC.byId(id);
      }

      public ImmutableList getProcessors() {
         return this.processors;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Projection[] method_36758() {
         return new Projection[]{TERRAIN_MATCHING, RIGID};
      }
   }
}
