package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

public interface HoverEvent {
   Codec CODEC = HoverEvent.Action.CODEC.dispatch("action", HoverEvent::getAction, (action) -> {
      return action.codec;
   });

   Action getAction();

   public static enum Action implements StringIdentifiable {
      SHOW_TEXT("show_text", true, HoverEvent.ShowText.CODEC),
      SHOW_ITEM("show_item", true, HoverEvent.ShowItem.CODEC),
      SHOW_ENTITY("show_entity", true, HoverEvent.ShowEntity.CODEC);

      public static final Codec UNVALIDATED_CODEC = StringIdentifiable.createBasicCodec(Action::values);
      public static final Codec CODEC = UNVALIDATED_CODEC.validate(Action::validate);
      private final String name;
      private final boolean parsable;
      final MapCodec codec;

      private Action(final String name, final boolean parsable, final MapCodec codec) {
         this.name = name;
         this.parsable = parsable;
         this.codec = codec;
      }

      public boolean isParsable() {
         return this.parsable;
      }

      public String asString() {
         return this.name;
      }

      public String toString() {
         return "<action " + this.name + ">";
      }

      private static DataResult validate(Action action) {
         return !action.isParsable() ? DataResult.error(() -> {
            return "Action not allowed: " + String.valueOf(action);
         }) : DataResult.success(action, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] method_66576() {
         return new Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY};
      }
   }

   public static class EntityContent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Registries.ENTITY_TYPE.getCodec().fieldOf("id").forGetter((content) -> {
            return content.entityType;
         }), Uuids.STRICT_CODEC.fieldOf("uuid").forGetter((content) -> {
            return content.uuid;
         }), TextCodecs.CODEC.optionalFieldOf("name").forGetter((content) -> {
            return content.name;
         })).apply(instance, EntityContent::new);
      });
      public final EntityType entityType;
      public final UUID uuid;
      public final Optional name;
      @Nullable
      private List tooltip;

      public EntityContent(EntityType entityType, UUID uuid, @Nullable Text name) {
         this(entityType, uuid, Optional.ofNullable(name));
      }

      public EntityContent(EntityType entityType, UUID uuid, Optional name) {
         this.entityType = entityType;
         this.uuid = uuid;
         this.name = name;
      }

      public List asTooltip() {
         if (this.tooltip == null) {
            this.tooltip = new ArrayList();
            Optional var10000 = this.name;
            List var10001 = this.tooltip;
            Objects.requireNonNull(var10001);
            var10000.ifPresent(var10001::add);
            this.tooltip.add(Text.translatable("gui.entity_tooltip.type", this.entityType.getName()));
            this.tooltip.add(Text.literal(this.uuid.toString()));
         }

         return this.tooltip;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            EntityContent entityContent = (EntityContent)o;
            return this.entityType.equals(entityContent.entityType) && this.uuid.equals(entityContent.uuid) && this.name.equals(entityContent.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.entityType.hashCode();
         i = 31 * i + this.uuid.hashCode();
         i = 31 * i + this.name.hashCode();
         return i;
      }
   }

   public static record ShowEntity(EntityContent entity) implements HoverEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(HoverEvent.EntityContent.CODEC.forGetter(ShowEntity::entity)).apply(instance, ShowEntity::new);
      });

      public ShowEntity(EntityContent entityContent) {
         this.entity = entityContent;
      }

      public Action getAction() {
         return HoverEvent.Action.SHOW_ENTITY;
      }

      public EntityContent entity() {
         return this.entity;
      }
   }

   public static record ShowItem(ItemStack item) implements HoverEvent {
      public static final MapCodec CODEC;

      public ShowItem(ItemStack stack) {
         stack = stack.copy();
         this.item = stack;
      }

      public Action getAction() {
         return HoverEvent.Action.SHOW_ITEM;
      }

      public boolean equals(Object o) {
         boolean var10000;
         if (o instanceof ShowItem showItem) {
            if (ItemStack.areEqual(this.item, showItem.item)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         return ItemStack.hashCode(this.item);
      }

      public ItemStack item() {
         return this.item;
      }

      static {
         CODEC = ItemStack.MAP_CODEC.xmap(ShowItem::new, ShowItem::item);
      }
   }

   public static record ShowText(Text value) implements HoverEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TextCodecs.CODEC.fieldOf("value").forGetter(ShowText::value)).apply(instance, ShowText::new);
      });

      public ShowText(Text text) {
         this.value = text;
      }

      public Action getAction() {
         return HoverEvent.Action.SHOW_TEXT;
      }

      public Text value() {
         return this.value;
      }
   }
}
