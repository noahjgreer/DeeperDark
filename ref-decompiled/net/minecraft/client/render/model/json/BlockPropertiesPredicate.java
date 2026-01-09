package net.minecraft.client.render.model.json;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BlockPropertiesPredicate {
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);

   public static Predicate parse(StateManager stateManager, String string) {
      Map map = new HashMap();
      Iterator var3 = COMMA_SPLITTER.split(string).iterator();

      while(true) {
         while(true) {
            Iterator iterator;
            do {
               if (!var3.hasNext()) {
                  return (state) -> {
                     Iterator var2 = map.entrySet().iterator();

                     Map.Entry entry;
                     do {
                        if (!var2.hasNext()) {
                           return true;
                        }

                        entry = (Map.Entry)var2.next();
                     } while(Objects.equals(state.get((Property)entry.getKey()), entry.getValue()));

                     return false;
                  };
               }

               String string2 = (String)var3.next();
               iterator = EQUAL_SIGN_SPLITTER.split(string2).iterator();
            } while(!iterator.hasNext());

            String string3 = (String)iterator.next();
            Property property = stateManager.getProperty(string3);
            if (property != null && iterator.hasNext()) {
               String string4 = (String)iterator.next();
               Comparable comparable = parse(property, string4);
               if (comparable == null) {
                  throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + String.valueOf(property.getValues()));
               }

               map.put(property, comparable);
            } else if (!string3.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
            }
         }
      }
   }

   @Nullable
   private static Comparable parse(Property property, String value) {
      return (Comparable)property.parse(value).orElse((Object)null);
   }
}
