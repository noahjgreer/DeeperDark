package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;

public record ParsedSelector(String comp_3067, EntitySelector comp_3068) {
   public static final Codec CODEC;

   public ParsedSelector(String string, EntitySelector entitySelector) {
      this.comp_3067 = string;
      this.comp_3068 = entitySelector;
   }

   public static DataResult parse(String selector) {
      try {
         EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(selector), true);
         return DataResult.success(new ParsedSelector(selector, entitySelectorReader.read()));
      } catch (CommandSyntaxException var2) {
         return DataResult.error(() -> {
            return "Invalid selector component: " + selector + ": " + var2.getMessage();
         });
      }
   }

   public boolean equals(Object o) {
      boolean var10000;
      if (o instanceof ParsedSelector parsedSelector) {
         if (this.comp_3067.equals(parsedSelector.comp_3067)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.comp_3067.hashCode();
   }

   public String toString() {
      return this.comp_3067;
   }

   public String comp_3067() {
      return this.comp_3067;
   }

   public EntitySelector comp_3068() {
      return this.comp_3068;
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(ParsedSelector::parse, ParsedSelector::comp_3067);
   }
}
