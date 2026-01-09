package net.minecraft.datafixer.fix;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;

public class EntityHealthFix extends DataFix {
   private static final Set ENTITIES = Sets.newHashSet(new String[]{"ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie"});

   public EntityHealthFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   public Dynamic fixHealth(Dynamic entityDynamic) {
      Optional optional = entityDynamic.get("HealF").asNumber().result();
      Optional optional2 = entityDynamic.get("Health").asNumber().result();
      float f;
      if (optional.isPresent()) {
         f = ((Number)optional.get()).floatValue();
         entityDynamic = entityDynamic.remove("HealF");
      } else {
         if (!optional2.isPresent()) {
            return entityDynamic;
         }

         f = ((Number)optional2.get()).floatValue();
      }

      return entityDynamic.set("Health", entityDynamic.createFloat(f));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(TypeReferences.ENTITY), (entityTyped) -> {
         return entityTyped.update(DSL.remainderFinder(), this::fixHealth);
      });
   }
}
