package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class TreeDecoratorType {
   public static final TreeDecoratorType TRUNK_VINE;
   public static final TreeDecoratorType LEAVE_VINE;
   public static final TreeDecoratorType PALE_MOSS;
   public static final TreeDecoratorType CREAKING_HEART;
   public static final TreeDecoratorType COCOA;
   public static final TreeDecoratorType BEEHIVE;
   public static final TreeDecoratorType ALTER_GROUND;
   public static final TreeDecoratorType ATTACHED_TO_LEAVES;
   public static final TreeDecoratorType PLACE_ON_GROUND;
   public static final TreeDecoratorType ATTACHED_TO_LOGS;
   private final MapCodec codec;

   private static TreeDecoratorType register(String id, MapCodec codec) {
      return (TreeDecoratorType)Registry.register(Registries.TREE_DECORATOR_TYPE, (String)id, new TreeDecoratorType(codec));
   }

   public TreeDecoratorType(MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   static {
      TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator.CODEC);
      LEAVE_VINE = register("leave_vine", LeavesVineTreeDecorator.CODEC);
      PALE_MOSS = register("pale_moss", PaleMossTreeDecorator.CODEC);
      CREAKING_HEART = register("creaking_heart", CreakingHeartTreeDecorator.CODEC);
      COCOA = register("cocoa", CocoaTreeDecorator.CODEC);
      BEEHIVE = register("beehive", BeehiveTreeDecorator.CODEC);
      ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator.CODEC);
      ATTACHED_TO_LEAVES = register("attached_to_leaves", AttachedToLeavesTreeDecorator.CODEC);
      PLACE_ON_GROUND = register("place_on_ground", PlaceOnGroundTreeDecorator.CODEC);
      ATTACHED_TO_LOGS = register("attached_to_logs", AttachedToLogsTreeDecorator.CODEC);
   }
}
