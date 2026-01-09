package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.Heightmap;

public class HeightmapArgumentType extends EnumArgumentType {
   private static final Codec HEIGHTMAP_CODEC = StringIdentifiable.createCodec(HeightmapArgumentType::getHeightmapTypes, (name) -> {
      return name.toLowerCase(Locale.ROOT);
   });

   private static Heightmap.Type[] getHeightmapTypes() {
      return (Heightmap.Type[])Arrays.stream(Heightmap.Type.values()).filter(Heightmap.Type::isStoredServerSide).toArray((i) -> {
         return new Heightmap.Type[i];
      });
   }

   private HeightmapArgumentType() {
      super(HEIGHTMAP_CODEC, HeightmapArgumentType::getHeightmapTypes);
   }

   public static HeightmapArgumentType heightmap() {
      return new HeightmapArgumentType();
   }

   public static Heightmap.Type getHeightmap(CommandContext context, String id) {
      return (Heightmap.Type)context.getArgument(id, Heightmap.Type.class);
   }

   protected String transformValueName(String name) {
      return name.toLowerCase(Locale.ROOT);
   }
}
