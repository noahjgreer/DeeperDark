package net.minecraft.dialog.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import net.minecraft.command.MacroInvocation;

public class ParsedTemplate {
   public static final Codec CODEC;
   public static final Codec NAME_CODEC;
   private final String raw;
   private final MacroInvocation parsed;

   private ParsedTemplate(String raw, MacroInvocation parsed) {
      this.raw = raw;
      this.parsed = parsed;
   }

   private static DataResult parse(String raw) {
      MacroInvocation macroInvocation;
      try {
         macroInvocation = MacroInvocation.parse(raw);
      } catch (Exception var3) {
         return DataResult.error(() -> {
            return "Failed to parse template " + raw + ": " + var3.getMessage();
         });
      }

      return DataResult.success(new ParsedTemplate(raw, macroInvocation));
   }

   public String apply(Map args) {
      List list = this.parsed.variables().stream().map((variable) -> {
         return (String)args.getOrDefault(variable, "");
      }).toList();
      return this.parsed.apply(list);
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(ParsedTemplate::parse, (parsedTemplate) -> {
         return parsedTemplate.raw;
      });
      NAME_CODEC = Codec.STRING.validate((name) -> {
         return MacroInvocation.isValidMacroName(name) ? DataResult.success(name) : DataResult.error(() -> {
            return name + " is not a valid input name";
         });
      });
   }
}
