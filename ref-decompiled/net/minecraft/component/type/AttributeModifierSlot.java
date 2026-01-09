package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum AttributeModifierSlot implements StringIdentifiable, Iterable {
   ANY(0, "any", (slot) -> {
      return true;
   }),
   MAINHAND(1, "mainhand", EquipmentSlot.MAINHAND),
   OFFHAND(2, "offhand", EquipmentSlot.OFFHAND),
   HAND(3, "hand", (slot) -> {
      return slot.getType() == EquipmentSlot.Type.HAND;
   }),
   FEET(4, "feet", EquipmentSlot.FEET),
   LEGS(5, "legs", EquipmentSlot.LEGS),
   CHEST(6, "chest", EquipmentSlot.CHEST),
   HEAD(7, "head", EquipmentSlot.HEAD),
   ARMOR(8, "armor", EquipmentSlot::isArmorSlot),
   BODY(9, "body", EquipmentSlot.BODY),
   SADDLE(10, "saddle", EquipmentSlot.SADDLE);

   public static final IntFunction ID_TO_VALUE = ValueLists.createIndexToValueFunction((id) -> {
      return id.id;
   }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final Codec CODEC = StringIdentifiable.createCodec(AttributeModifierSlot::values);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, (id) -> {
      return id.id;
   });
   private final int id;
   private final String name;
   private final Predicate slotPredicate;
   private final List slots;

   private AttributeModifierSlot(final int id, final String name, final Predicate slotPredicate) {
      this.id = id;
      this.name = name;
      this.slotPredicate = slotPredicate;
      this.slots = EquipmentSlot.VALUES.stream().filter(slotPredicate).toList();
   }

   private AttributeModifierSlot(final int id, final String name, final EquipmentSlot slot) {
      this(id, name, (slotx) -> {
         return slotx == slot;
      });
   }

   public static AttributeModifierSlot forEquipmentSlot(EquipmentSlot slot) {
      AttributeModifierSlot var10000;
      switch (slot) {
         case MAINHAND:
            var10000 = MAINHAND;
            break;
         case OFFHAND:
            var10000 = OFFHAND;
            break;
         case FEET:
            var10000 = FEET;
            break;
         case LEGS:
            var10000 = LEGS;
            break;
         case CHEST:
            var10000 = CHEST;
            break;
         case HEAD:
            var10000 = HEAD;
            break;
         case BODY:
            var10000 = BODY;
            break;
         case SADDLE:
            var10000 = SADDLE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String asString() {
      return this.name;
   }

   public boolean matches(EquipmentSlot slot) {
      return this.slotPredicate.test(slot);
   }

   public List getSlots() {
      return this.slots;
   }

   public Iterator iterator() {
      return this.slots.iterator();
   }

   // $FF: synthetic method
   private static AttributeModifierSlot[] method_57285() {
      return new AttributeModifierSlot[]{ANY, MAINHAND, OFFHAND, HAND, FEET, LEGS, CHEST, HEAD, ARMOR, BODY, SADDLE};
   }
}
