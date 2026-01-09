package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

public record EntityNbtDataSource(String rawSelector, @Nullable EntitySelector selector) implements NbtDataSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("entity").forGetter(EntityNbtDataSource::rawSelector)).apply(instance, EntityNbtDataSource::new);
   });
   public static final NbtDataSource.Type TYPE;

   public EntityNbtDataSource(String rawPath) {
      this(rawPath, parseSelector(rawPath));
   }

   public EntityNbtDataSource(String rawPath, @Nullable EntitySelector entitySelector) {
      this.rawSelector = rawPath;
      this.selector = entitySelector;
   }

   @Nullable
   private static EntitySelector parseSelector(String rawSelector) {
      try {
         EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector), true);
         return entitySelectorReader.read();
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public Stream get(ServerCommandSource source) throws CommandSyntaxException {
      if (this.selector != null) {
         List list = this.selector.getEntities(source);
         return list.stream().map(NbtPredicate::entityToNbt);
      } else {
         return Stream.empty();
      }
   }

   public NbtDataSource.Type getType() {
      return TYPE;
   }

   public String toString() {
      return "entity=" + this.rawSelector;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof EntityNbtDataSource) {
            EntityNbtDataSource entityNbtDataSource = (EntityNbtDataSource)o;
            if (this.rawSelector.equals(entityNbtDataSource.rawSelector)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.rawSelector.hashCode();
   }

   public String rawSelector() {
      return this.rawSelector;
   }

   @Nullable
   public EntitySelector selector() {
      return this.selector;
   }

   static {
      TYPE = new NbtDataSource.Type(CODEC, "entity");
   }
}
