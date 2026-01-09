package net.minecraft.data.validate;

import com.mojang.logging.LogUtils;
import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.structure.StructureTemplate;
import org.slf4j.Logger;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PATH_PREFIX;

   public NbtCompound write(String name, NbtCompound nbt) {
      return name.startsWith(PATH_PREFIX) ? update(name, nbt) : nbt;
   }

   public static NbtCompound update(String name, NbtCompound nbt) {
      StructureTemplate structureTemplate = new StructureTemplate();
      int i = NbtHelper.getDataVersion((NbtCompound)nbt, 500);
      int j = true;
      if (i < 4420) {
         LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", new Object[]{i, 4420, name});
      }

      NbtCompound nbtCompound = DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbt, i);
      structureTemplate.readNbt(Registries.BLOCK, nbtCompound);
      return structureTemplate.writeNbt(new NbtCompound());
   }

   static {
      PATH_PREFIX = ResourceType.SERVER_DATA.getDirectory() + "/minecraft/structure/";
   }
}
