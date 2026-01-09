package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public record StructurePiecesList(List pieces) {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Identifier JIGSAW = Identifier.ofVanilla("jigsaw");
   private static final Map ID_UPDATES;

   public StructurePiecesList(final List pieces) {
      this.pieces = List.copyOf(pieces);
   }

   public boolean isEmpty() {
      return this.pieces.isEmpty();
   }

   public boolean contains(BlockPos pos) {
      Iterator var2 = this.pieces.iterator();

      StructurePiece structurePiece;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         structurePiece = (StructurePiece)var2.next();
      } while(!structurePiece.getBoundingBox().contains(pos));

      return true;
   }

   public NbtElement toNbt(StructureContext context) {
      NbtList nbtList = new NbtList();
      Iterator var3 = this.pieces.iterator();

      while(var3.hasNext()) {
         StructurePiece structurePiece = (StructurePiece)var3.next();
         nbtList.add(structurePiece.toNbt(context));
      }

      return nbtList;
   }

   public static StructurePiecesList fromNbt(NbtList list, StructureContext context) {
      List list2 = Lists.newArrayList();

      for(int i = 0; i < list.size(); ++i) {
         NbtCompound nbtCompound = list.getCompoundOrEmpty(i);
         String string = nbtCompound.getString("id", "").toLowerCase(Locale.ROOT);
         Identifier identifier = Identifier.of(string);
         Identifier identifier2 = (Identifier)ID_UPDATES.getOrDefault(identifier, identifier);
         StructurePieceType structurePieceType = (StructurePieceType)Registries.STRUCTURE_PIECE.get(identifier2);
         if (structurePieceType == null) {
            LOGGER.error("Unknown structure piece id: {}", identifier2);
         } else {
            try {
               StructurePiece structurePiece = structurePieceType.load(context, nbtCompound);
               list2.add(structurePiece);
            } catch (Exception var10) {
               LOGGER.error("Exception loading structure piece with id {}", identifier2, var10);
            }
         }
      }

      return new StructurePiecesList(list2);
   }

   public BlockBox getBoundingBox() {
      return StructurePiece.boundingBox(this.pieces.stream());
   }

   public List pieces() {
      return this.pieces;
   }

   static {
      ID_UPDATES = ImmutableMap.builder().put(Identifier.ofVanilla("nvi"), JIGSAW).put(Identifier.ofVanilla("pcp"), JIGSAW).put(Identifier.ofVanilla("bastionremnant"), JIGSAW).put(Identifier.ofVanilla("runtime"), JIGSAW).build();
   }
}
