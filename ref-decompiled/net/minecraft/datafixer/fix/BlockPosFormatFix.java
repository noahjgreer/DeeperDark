package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class BlockPosFormatFix extends DataFix {
   private static final List PATROL_TARGET_ENTITY_IDS = List.of("minecraft:witch", "minecraft:ravager", "minecraft:pillager", "minecraft:illusioner", "minecraft:evoker", "minecraft:vindicator");

   public BlockPosFormatFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   private Typed fixOldBlockPosFormat(Typed typed, Map oldToNewKey) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         Map.Entry entry;
         for(Iterator var2 = oldToNewKey.entrySet().iterator(); var2.hasNext(); dynamic = dynamic.renameAndFixField((String)entry.getKey(), (String)entry.getValue(), FixUtil::fixBlockPos)) {
            entry = (Map.Entry)var2.next();
         }

         return dynamic;
      });
   }

   private Dynamic fixMapItemFrames(Dynamic dynamic) {
      return dynamic.update("frames", (frames) -> {
         return frames.createList(frames.asStream().map((frame) -> {
            frame = frame.renameAndFixField("Pos", "pos", FixUtil::fixBlockPos);
            frame = frame.renameField("Rotation", "rotation");
            frame = frame.renameField("EntityId", "entity_id");
            return frame;
         }));
      }).update("banners", (banners) -> {
         return banners.createList(banners.asStream().map((banner) -> {
            banner = banner.renameField("Pos", "pos");
            banner = banner.renameField("Color", "color");
            banner = banner.renameField("Name", "name");
            return banner;
         }));
      });
   }

   public TypeRewriteRule makeRule() {
      List list = new ArrayList();
      this.addEntityFixes(list);
      this.addBlockEntityFixes(list);
      list.add(this.writeFixAndRead("BlockPos format for map frames", this.getInputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA), this.getOutputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA), (dynamic) -> {
         return dynamic.update("data", this::fixMapItemFrames);
      }));
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      list.add(this.fixTypeEverywhereTyped("BlockPos format for compass target", type, ItemNbtFix.fixNbt(type, "minecraft:compass"::equals, (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.update("LodestonePos", FixUtil::fixBlockPos);
         });
      })));
      return TypeRewriteRule.seq(list);
   }

   private void addEntityFixes(List rules) {
      rules.add(this.createFixRule(TypeReferences.ENTITY, "minecraft:bee", Map.of("HivePos", "hive_pos", "FlowerPos", "flower_pos")));
      rules.add(this.createFixRule(TypeReferences.ENTITY, "minecraft:end_crystal", Map.of("BeamTarget", "beam_target")));
      rules.add(this.createFixRule(TypeReferences.ENTITY, "minecraft:wandering_trader", Map.of("WanderTarget", "wander_target")));
      Iterator var2 = PATROL_TARGET_ENTITY_IDS.iterator();

      while(var2.hasNext()) {
         String string = (String)var2.next();
         rules.add(this.createFixRule(TypeReferences.ENTITY, string, Map.of("PatrolTarget", "patrol_target")));
      }

      rules.add(this.fixTypeEverywhereTyped("BlockPos format in Leash for mobs", this.getInputSchema().getType(TypeReferences.ENTITY), (typed) -> {
         return typed.update(DSL.remainderFinder(), (entityDynamic) -> {
            return entityDynamic.renameAndFixField("Leash", "leash", FixUtil::fixBlockPos);
         });
      }));
   }

   private void addBlockEntityFixes(List rules) {
      rules.add(this.createFixRule(TypeReferences.BLOCK_ENTITY, "minecraft:beehive", Map.of("FlowerPos", "flower_pos")));
      rules.add(this.createFixRule(TypeReferences.BLOCK_ENTITY, "minecraft:end_gateway", Map.of("ExitPortal", "exit_portal")));
   }

   private TypeRewriteRule createFixRule(DSL.TypeReference typeReference, String id, Map oldToNewKey) {
      String string = "BlockPos format in " + String.valueOf(oldToNewKey.keySet()) + " for " + id + " (" + typeReference.typeName() + ")";
      OpticFinder opticFinder = DSL.namedChoice(id, this.getInputSchema().getChoiceType(typeReference, id));
      return this.fixTypeEverywhereTyped(string, this.getInputSchema().getType(typeReference), (typed) -> {
         return typed.updateTyped(opticFinder, (typedx) -> {
            return this.fixOldBlockPosFormat(typedx, oldToNewKey);
         });
      });
   }
}
