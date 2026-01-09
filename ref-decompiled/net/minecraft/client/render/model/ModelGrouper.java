package net.minecraft.client.render.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public class ModelGrouper {
   static final int field_53673 = -1;
   private static final int field_53674 = 0;

   public static Object2IntMap group(BlockColors colors, BlockStatesLoader.LoadedModels definition) {
      Map map = new HashMap();
      Map map2 = new HashMap();
      definition.models().forEach((state, model) -> {
         List list = (List)map.computeIfAbsent(state.getBlock(), (block) -> {
            return List.copyOf(colors.getProperties(block));
         });
         GroupKey groupKey = ModelGrouper.GroupKey.of(state, model, list);
         ((Set)map2.computeIfAbsent(groupKey, (key) -> {
            return Sets.newIdentityHashSet();
         })).add(state);
      });
      int i = 1;
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      object2IntMap.defaultReturnValue(-1);
      Iterator var6 = map2.values().iterator();

      while(var6.hasNext()) {
         Set set = (Set)var6.next();
         Iterator iterator = set.iterator();

         while(iterator.hasNext()) {
            BlockState blockState = (BlockState)iterator.next();
            if (blockState.getRenderType() != BlockRenderType.MODEL) {
               iterator.remove();
               object2IntMap.put(blockState, 0);
            }
         }

         if (set.size() > 1) {
            int j = i++;
            set.forEach((state) -> {
               object2IntMap.put(state, j);
            });
         }
      }

      return object2IntMap;
   }

   @Environment(EnvType.CLIENT)
   static record GroupKey(Object equalityGroup, List coloringValues) {
      private GroupKey(Object object, List list) {
         this.equalityGroup = object;
         this.coloringValues = list;
      }

      public static GroupKey of(BlockState state, BlockStateModel.UnbakedGrouped model, List properties) {
         List list = getColoringValues(state, properties);
         Object object = model.getEqualityGroup(state);
         return new GroupKey(object, list);
      }

      private static List getColoringValues(BlockState state, List properties) {
         Object[] objects = new Object[properties.size()];

         for(int i = 0; i < properties.size(); ++i) {
            objects[i] = state.get((Property)properties.get(i));
         }

         return List.of(objects);
      }

      public Object equalityGroup() {
         return this.equalityGroup;
      }

      public List coloringValues() {
         return this.coloringValues;
      }
   }
}
