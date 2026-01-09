package net.minecraft.structure.pool;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ListPoolElement extends StructurePoolElement {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter((pool) -> {
         return pool.elements;
      }), projectionGetter()).apply(instance, ListPoolElement::new);
   });
   private final List elements;

   public ListPoolElement(List elements, StructurePool.Projection projection) {
      super(projection);
      if (elements.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = elements;
         this.setAllElementsProjection(projection);
      }
   }

   public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
      int i = 0;
      int j = 0;
      int k = 0;

      Vec3i vec3i;
      for(Iterator var6 = this.elements.iterator(); var6.hasNext(); k = Math.max(k, vec3i.getZ())) {
         StructurePoolElement structurePoolElement = (StructurePoolElement)var6.next();
         vec3i = structurePoolElement.getStart(structureTemplateManager, rotation);
         i = Math.max(i, vec3i.getX());
         j = Math.max(j, vec3i.getY());
      }

      return new Vec3i(i, j, k);
   }

   public List getStructureBlockInfos(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random) {
      return ((StructurePoolElement)this.elements.get(0)).getStructureBlockInfos(structureTemplateManager, pos, rotation, random);
   }

   public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation) {
      Stream stream = this.elements.stream().filter((element) -> {
         return element != EmptyPoolElement.INSTANCE;
      }).map((element) -> {
         return element.getBoundingBox(structureTemplateManager, pos, rotation);
      });
      Objects.requireNonNull(stream);
      return (BlockBox)BlockBox.encompass(stream::iterator).orElseThrow(() -> {
         return new IllegalStateException("Unable to calculate boundingbox for ListPoolElement");
      });
   }

   public boolean generate(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, StructureLiquidSettings liquidSettings, boolean keepJigsaws) {
      Iterator var12 = this.elements.iterator();

      StructurePoolElement structurePoolElement;
      do {
         if (!var12.hasNext()) {
            return true;
         }

         structurePoolElement = (StructurePoolElement)var12.next();
      } while(structurePoolElement.generate(structureTemplateManager, world, structureAccessor, chunkGenerator, pos, pivot, rotation, box, random, liquidSettings, keepJigsaws));

      return false;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.LIST_POOL_ELEMENT;
   }

   public StructurePoolElement setProjection(StructurePool.Projection projection) {
      super.setProjection(projection);
      this.setAllElementsProjection(projection);
      return this;
   }

   public String toString() {
      Stream var10000 = this.elements.stream().map(Object::toString);
      return "List[" + (String)var10000.collect(Collectors.joining(", ")) + "]";
   }

   private void setAllElementsProjection(StructurePool.Projection projection) {
      this.elements.forEach((element) -> {
         element.setProjection(projection);
      });
   }

   @VisibleForTesting
   public List getElements() {
      return this.elements;
   }
}
