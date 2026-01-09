package net.minecraft.datafixer.fix;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ParticleUnflatteningFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ParticleUnflatteningFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.PARTICLE);
      Type type2 = this.getOutputSchema().getType(TypeReferences.PARTICLE);
      return this.writeFixAndRead("ParticleUnflatteningFix", type, type2, this::fixParticle);
   }

   private Dynamic fixParticle(Dynamic dynamic) {
      Optional optional = dynamic.asString().result();
      if (optional.isEmpty()) {
         return dynamic;
      } else {
         String string = (String)optional.get();
         String[] strings = string.split(" ", 2);
         String string2 = IdentifierNormalizingSchema.normalize(strings[0]);
         Dynamic dynamic2 = dynamic.createMap(Map.of(dynamic.createString("type"), dynamic.createString(string2)));
         Dynamic var10000;
         switch (string2) {
            case "minecraft:item":
               var10000 = strings.length > 1 ? this.fixItemParticle(dynamic2, strings[1]) : dynamic2;
               break;
            case "minecraft:block":
            case "minecraft:block_marker":
            case "minecraft:falling_dust":
            case "minecraft:dust_pillar":
               var10000 = strings.length > 1 ? this.fixBlockParticle(dynamic2, strings[1]) : dynamic2;
               break;
            case "minecraft:dust":
               var10000 = strings.length > 1 ? this.fixDustParticle(dynamic2, strings[1]) : dynamic2;
               break;
            case "minecraft:dust_color_transition":
               var10000 = strings.length > 1 ? this.fixDustColorTransitionParticle(dynamic2, strings[1]) : dynamic2;
               break;
            case "minecraft:sculk_charge":
               var10000 = strings.length > 1 ? this.fixSculkChargeParticle(dynamic2, strings[1]) : dynamic2;
               break;
            case "minecraft:vibration":
               var10000 = strings.length > 1 ? this.fixVibrationParticle(dynamic2, strings[1]) : dynamic2;
               break;
            case "minecraft:shriek":
               var10000 = strings.length > 1 ? this.fixShriekParticle(dynamic2, strings[1]) : dynamic2;
               break;
            default:
               var10000 = dynamic2;
         }

         return var10000;
      }
   }

   private Dynamic fixItemParticle(Dynamic dynamic, String params) {
      int i = params.indexOf("{");
      Dynamic dynamic2 = dynamic.createMap(Map.of(dynamic.createString("Count"), dynamic.createInt(1)));
      if (i == -1) {
         dynamic2 = dynamic2.set("id", dynamic.createString(params));
      } else {
         dynamic2 = dynamic2.set("id", dynamic.createString(params.substring(0, i)));
         Dynamic dynamic3 = tryParse(dynamic.getOps(), params.substring(i));
         if (dynamic3 != null) {
            dynamic2 = dynamic2.set("tag", dynamic3);
         }
      }

      return dynamic.set("item", dynamic2);
   }

   @Nullable
   private static Dynamic tryParse(DynamicOps dynamicOps, String string) {
      try {
         return new Dynamic(dynamicOps, StringNbtReader.fromOps(dynamicOps).read(string));
      } catch (Exception var3) {
         LOGGER.warn("Failed to parse tag: {}", string, var3);
         return null;
      }
   }

   private Dynamic fixBlockParticle(Dynamic dynamic, String params) {
      int i = params.indexOf("[");
      Dynamic dynamic2 = dynamic.emptyMap();
      if (i == -1) {
         dynamic2 = dynamic2.set("Name", dynamic.createString(IdentifierNormalizingSchema.normalize(params)));
      } else {
         dynamic2 = dynamic2.set("Name", dynamic.createString(IdentifierNormalizingSchema.normalize(params.substring(0, i))));
         Map map = parseBlockProperties(dynamic, params.substring(i));
         if (!map.isEmpty()) {
            dynamic2 = dynamic2.set("Properties", dynamic.createMap(map));
         }
      }

      return dynamic.set("block_state", dynamic2);
   }

   private static Map parseBlockProperties(Dynamic dynamic, String propertiesStr) {
      try {
         Map map = new HashMap();
         StringReader stringReader = new StringReader(propertiesStr);
         stringReader.expect('[');
         stringReader.skipWhitespace();

         while(stringReader.canRead() && stringReader.peek() != ']') {
            stringReader.skipWhitespace();
            String string = stringReader.readString();
            stringReader.skipWhitespace();
            stringReader.expect('=');
            stringReader.skipWhitespace();
            String string2 = stringReader.readString();
            stringReader.skipWhitespace();
            map.put(dynamic.createString(string), dynamic.createString(string2));
            if (stringReader.canRead()) {
               if (stringReader.peek() != ',') {
                  break;
               }

               stringReader.skip();
            }
         }

         stringReader.expect(']');
         return map;
      } catch (Exception var6) {
         LOGGER.warn("Failed to parse block properties: {}", propertiesStr, var6);
         return Map.of();
      }
   }

   private static Dynamic parseColor(Dynamic dynamic, StringReader paramsReader) throws CommandSyntaxException {
      float f = paramsReader.readFloat();
      paramsReader.expect(' ');
      float g = paramsReader.readFloat();
      paramsReader.expect(' ');
      float h = paramsReader.readFloat();
      Stream var10001 = Stream.of(f, g, h);
      Objects.requireNonNull(dynamic);
      return dynamic.createList(var10001.map(dynamic::createFloat));
   }

   private Dynamic fixDustParticle(Dynamic dynamic, String params) {
      try {
         StringReader stringReader = new StringReader(params);
         Dynamic dynamic2 = parseColor(dynamic, stringReader);
         stringReader.expect(' ');
         float f = stringReader.readFloat();
         return dynamic.set("color", dynamic2).set("scale", dynamic.createFloat(f));
      } catch (Exception var6) {
         LOGGER.warn("Failed to parse particle options: {}", params, var6);
         return dynamic;
      }
   }

   private Dynamic fixDustColorTransitionParticle(Dynamic dynamic, String params) {
      try {
         StringReader stringReader = new StringReader(params);
         Dynamic dynamic2 = parseColor(dynamic, stringReader);
         stringReader.expect(' ');
         float f = stringReader.readFloat();
         stringReader.expect(' ');
         Dynamic dynamic3 = parseColor(dynamic, stringReader);
         return dynamic.set("from_color", dynamic2).set("to_color", dynamic3).set("scale", dynamic.createFloat(f));
      } catch (Exception var7) {
         LOGGER.warn("Failed to parse particle options: {}", params, var7);
         return dynamic;
      }
   }

   private Dynamic fixSculkChargeParticle(Dynamic dynamic, String params) {
      try {
         StringReader stringReader = new StringReader(params);
         float f = stringReader.readFloat();
         return dynamic.set("roll", dynamic.createFloat(f));
      } catch (Exception var5) {
         LOGGER.warn("Failed to parse particle options: {}", params, var5);
         return dynamic;
      }
   }

   private Dynamic fixVibrationParticle(Dynamic dynamic, String params) {
      try {
         StringReader stringReader = new StringReader(params);
         float f = (float)stringReader.readDouble();
         stringReader.expect(' ');
         float g = (float)stringReader.readDouble();
         stringReader.expect(' ');
         float h = (float)stringReader.readDouble();
         stringReader.expect(' ');
         int i = stringReader.readInt();
         Dynamic dynamic2 = dynamic.createIntList(IntStream.of(new int[]{MathHelper.floor(f), MathHelper.floor(g), MathHelper.floor(h)}));
         Dynamic dynamic3 = dynamic.createMap(Map.of(dynamic.createString("type"), dynamic.createString("minecraft:block"), dynamic.createString("pos"), dynamic2));
         return dynamic.set("destination", dynamic3).set("arrival_in_ticks", dynamic.createInt(i));
      } catch (Exception var10) {
         LOGGER.warn("Failed to parse particle options: {}", params, var10);
         return dynamic;
      }
   }

   private Dynamic fixShriekParticle(Dynamic dynamic, String params) {
      try {
         StringReader stringReader = new StringReader(params);
         int i = stringReader.readInt();
         return dynamic.set("delay", dynamic.createInt(i));
      } catch (Exception var5) {
         LOGGER.warn("Failed to parse particle options: {}", params, var5);
         return dynamic;
      }
   }
}
