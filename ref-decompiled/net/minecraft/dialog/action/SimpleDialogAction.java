package net.minecraft.dialog.action;

import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Util;

public record SimpleDialogAction(ClickEvent value) implements DialogAction {
   public static final Map CODECS = (Map)Util.make(() -> {
      Map map = new EnumMap(ClickEvent.Action.class);
      ClickEvent.Action[] var1 = (ClickEvent.Action[])ClickEvent.Action.class.getEnumConstants();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ClickEvent.Action action = var1[var3];
         if (action.isUserDefinable()) {
            MapCodec mapCodec = action.getCodec();
            map.put(action, mapCodec.xmap(SimpleDialogAction::new, SimpleDialogAction::value));
         }
      }

      return Collections.unmodifiableMap(map);
   });

   public SimpleDialogAction(ClickEvent clickEvent) {
      this.value = clickEvent;
   }

   public MapCodec getCodec() {
      return (MapCodec)CODECS.get(this.value.getAction());
   }

   public Optional createClickEvent(Map valueGetters) {
      return Optional.of(this.value);
   }

   public ClickEvent value() {
      return this.value;
   }
}
