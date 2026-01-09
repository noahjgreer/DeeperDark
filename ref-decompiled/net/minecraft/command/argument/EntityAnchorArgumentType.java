package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityAnchorArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType((name) -> {
      return Text.stringifiedTranslatable("argument.anchor.invalid", name);
   });

   public static EntityAnchor getEntityAnchor(CommandContext context, String name) {
      return (EntityAnchor)context.getArgument(name, EntityAnchor.class);
   }

   public static EntityAnchorArgumentType entityAnchor() {
      return new EntityAnchorArgumentType();
   }

   public EntityAnchor parse(StringReader stringReader) throws CommandSyntaxException {
      int i = stringReader.getCursor();
      String string = stringReader.readUnquotedString();
      EntityAnchor entityAnchor = EntityAnchorArgumentType.EntityAnchor.fromId(string);
      if (entityAnchor == null) {
         stringReader.setCursor(i);
         throw INVALID_ANCHOR_EXCEPTION.createWithContext(stringReader, string);
      } else {
         return entityAnchor;
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching((Iterable)EntityAnchorArgumentType.EntityAnchor.ANCHORS.keySet(), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   public static enum EntityAnchor {
      FEET("feet", (pos, entity) -> {
         return pos;
      }),
      EYES("eyes", (pos, entity) -> {
         return new Vec3d(pos.x, pos.y + (double)entity.getStandingEyeHeight(), pos.z);
      });

      static final Map ANCHORS = (Map)Util.make(Maps.newHashMap(), (map) -> {
         EntityAnchor[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EntityAnchor entityAnchor = var1[var3];
            map.put(entityAnchor.id, entityAnchor);
         }

      });
      private final String id;
      private final BiFunction offset;

      private EntityAnchor(final String id, final BiFunction offset) {
         this.id = id;
         this.offset = offset;
      }

      @Nullable
      public static EntityAnchor fromId(String id) {
         return (EntityAnchor)ANCHORS.get(id);
      }

      public Vec3d positionAt(Entity entity) {
         return (Vec3d)this.offset.apply(entity.getPos(), entity);
      }

      public Vec3d positionAt(ServerCommandSource source) {
         Entity entity = source.getEntity();
         return entity == null ? source.getPosition() : (Vec3d)this.offset.apply(source.getPosition(), entity);
      }

      // $FF: synthetic method
      private static EntityAnchor[] method_36814() {
         return new EntityAnchor[]{FEET, EYES};
      }
   }
}
