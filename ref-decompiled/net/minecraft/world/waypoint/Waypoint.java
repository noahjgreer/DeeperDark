package net.minecraft.world.waypoint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public interface Waypoint {
   int DEFAULT_PLAYER_RANGE = 60000000;
   EntityAttributeModifier DISABLE_TRACKING = new EntityAttributeModifier(Identifier.ofVanilla("waypoint_transmit_range_hide"), -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

   static Item.Settings disableTracking(Item.Settings settings) {
      return settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.WAYPOINT_TRANSMIT_RANGE, DISABLE_TRACKING, AttributeModifierSlot.HEAD, AttributeModifiersComponent.Display.getHidden()).build());
   }

   public static class Config {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RegistryKey.createCodec(WaypointStyles.REGISTRY).fieldOf("style").forGetter((config) -> {
            return config.style;
         }), Codecs.RGB.optionalFieldOf("color").forGetter((config) -> {
            return config.color;
         })).apply(instance, Config::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Config DEFAULT;
      public RegistryKey style;
      public Optional color;

      public Config() {
         this.style = WaypointStyles.DEFAULT;
         this.color = Optional.empty();
      }

      private Config(RegistryKey style, Optional color) {
         this.style = WaypointStyles.DEFAULT;
         this.color = Optional.empty();
         this.style = style;
         this.color = color;
      }

      public boolean hasCustomStyle() {
         return this.style != WaypointStyles.DEFAULT || this.color.isPresent();
      }

      public Config withTeamColorOf(LivingEntity entity) {
         RegistryKey registryKey = this.getStyle();
         Optional optional = this.color.or(() -> {
            return Optional.ofNullable(entity.getScoreboardTeam()).map((team) -> {
               return team.getColor().getColorValue();
            }).map((color) -> {
               return color == 0 ? -13619152 : color;
            });
         });
         return registryKey == this.style && optional.isEmpty() ? this : new Config(registryKey, optional);
      }

      private RegistryKey getStyle() {
         return this.style != WaypointStyles.DEFAULT ? this.style : WaypointStyles.DEFAULT;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(RegistryKey.createPacketCodec(WaypointStyles.REGISTRY), (config) -> {
            return config.style;
         }, PacketCodecs.optional(PacketCodecs.RGB), (config) -> {
            return config.color;
         }, Config::new);
         DEFAULT = new Config();
      }
   }
}
