package net.minecraft.structure;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class StructureStart {
   public static final String INVALID = "INVALID";
   public static final StructureStart DEFAULT = new StructureStart((Structure)null, new ChunkPos(0, 0), 0, new StructurePiecesList(List.of()));
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Structure structure;
   private final StructurePiecesList children;
   private final ChunkPos pos;
   private int references;
   @Nullable
   private volatile BlockBox boundingBox;

   public StructureStart(Structure structure, ChunkPos pos, int references, StructurePiecesList children) {
      this.structure = structure;
      this.pos = pos;
      this.references = references;
      this.children = children;
   }

   @Nullable
   public static StructureStart fromNbt(StructureContext context, NbtCompound nbt, long seed) {
      String string = nbt.getString("id", "");
      if ("INVALID".equals(string)) {
         return DEFAULT;
      } else {
         Registry registry = context.registryManager().getOrThrow(RegistryKeys.STRUCTURE);
         Structure structure = (Structure)registry.get(Identifier.of(string));
         if (structure == null) {
            LOGGER.error("Unknown stucture id: {}", string);
            return null;
         } else {
            ChunkPos chunkPos = new ChunkPos(nbt.getInt("ChunkX", 0), nbt.getInt("ChunkZ", 0));
            int i = nbt.getInt("references", 0);
            NbtList nbtList = nbt.getListOrEmpty("Children");

            try {
               StructurePiecesList structurePiecesList = StructurePiecesList.fromNbt(nbtList, context);
               if (structure instanceof OceanMonumentStructure) {
                  structurePiecesList = OceanMonumentStructure.modifyPiecesOnRead(chunkPos, seed, structurePiecesList);
               }

               return new StructureStart(structure, chunkPos, i, structurePiecesList);
            } catch (Exception var11) {
               LOGGER.error("Failed Start with id {}", string, var11);
               return null;
            }
         }
      }
   }

   public BlockBox getBoundingBox() {
      BlockBox blockBox = this.boundingBox;
      if (blockBox == null) {
         blockBox = this.structure.expandBoxIfShouldAdaptNoise(this.children.getBoundingBox());
         this.boundingBox = blockBox;
      }

      return blockBox;
   }

   public void place(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos) {
      List list = this.children.pieces();
      if (!list.isEmpty()) {
         BlockBox blockBox = ((StructurePiece)list.get(0)).boundingBox;
         BlockPos blockPos = blockBox.getCenter();
         BlockPos blockPos2 = new BlockPos(blockPos.getX(), blockBox.getMinY(), blockPos.getZ());
         Iterator var11 = list.iterator();

         while(var11.hasNext()) {
            StructurePiece structurePiece = (StructurePiece)var11.next();
            if (structurePiece.getBoundingBox().intersects(chunkBox)) {
               structurePiece.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, blockPos2);
            }
         }

         this.structure.postPlace(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, this.children);
      }
   }

   public NbtCompound toNbt(StructureContext context, ChunkPos chunkPos) {
      NbtCompound nbtCompound = new NbtCompound();
      if (this.hasChildren()) {
         nbtCompound.putString("id", context.registryManager().getOrThrow(RegistryKeys.STRUCTURE).getId(this.structure).toString());
         nbtCompound.putInt("ChunkX", chunkPos.x);
         nbtCompound.putInt("ChunkZ", chunkPos.z);
         nbtCompound.putInt("references", this.references);
         nbtCompound.put("Children", this.children.toNbt(context));
         return nbtCompound;
      } else {
         nbtCompound.putString("id", "INVALID");
         return nbtCompound;
      }
   }

   public boolean hasChildren() {
      return !this.children.isEmpty();
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public boolean isNeverReferenced() {
      return this.references < this.getMinReferencedStructureReferenceCount();
   }

   public void incrementReferences() {
      ++this.references;
   }

   public int getReferences() {
      return this.references;
   }

   protected int getMinReferencedStructureReferenceCount() {
      return 1;
   }

   public Structure getStructure() {
      return this.structure;
   }

   public List getChildren() {
      return this.children.pieces();
   }
}
