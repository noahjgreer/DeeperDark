package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public record ToolComponent(List rules, float defaultMiningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ToolComponent.Rule.CODEC.listOf().fieldOf("rules").forGetter(ToolComponent::rules), Codec.FLOAT.optionalFieldOf("default_mining_speed", 1.0F).forGetter(ToolComponent::defaultMiningSpeed), Codecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_block", 1).forGetter(ToolComponent::damagePerBlock), Codec.BOOL.optionalFieldOf("can_destroy_blocks_in_creative", true).forGetter(ToolComponent::canDestroyBlocksInCreative)).apply(instance, ToolComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public ToolComponent(List list, float f, int i, boolean bl) {
      this.rules = list;
      this.defaultMiningSpeed = f;
      this.damagePerBlock = i;
      this.canDestroyBlocksInCreative = bl;
   }

   public float getSpeed(BlockState state) {
      Iterator var2 = this.rules.iterator();

      Rule rule;
      do {
         if (!var2.hasNext()) {
            return this.defaultMiningSpeed;
         }

         rule = (Rule)var2.next();
      } while(!rule.speed.isPresent() || !state.isIn(rule.blocks));

      return (Float)rule.speed.get();
   }

   public boolean isCorrectForDrops(BlockState state) {
      Iterator var2 = this.rules.iterator();

      Rule rule;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         rule = (Rule)var2.next();
      } while(!rule.correctForDrops.isPresent() || !state.isIn(rule.blocks));

      return (Boolean)rule.correctForDrops.get();
   }

   public List rules() {
      return this.rules;
   }

   public float defaultMiningSpeed() {
      return this.defaultMiningSpeed;
   }

   public int damagePerBlock() {
      return this.damagePerBlock;
   }

   public boolean canDestroyBlocksInCreative() {
      return this.canDestroyBlocksInCreative;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(ToolComponent.Rule.PACKET_CODEC.collect(PacketCodecs.toList()), ToolComponent::rules, PacketCodecs.FLOAT, ToolComponent::defaultMiningSpeed, PacketCodecs.VAR_INT, ToolComponent::damagePerBlock, PacketCodecs.BOOLEAN, ToolComponent::canDestroyBlocksInCreative, ToolComponent::new);
   }

   public static record Rule(RegistryEntryList blocks, Optional speed, Optional correctForDrops) {
      final RegistryEntryList blocks;
      final Optional speed;
      final Optional correctForDrops;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("blocks").forGetter(Rule::blocks), Codecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Rule::speed), Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Rule::correctForDrops)).apply(instance, Rule::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public Rule(RegistryEntryList registryEntryList, Optional optional, Optional optional2) {
         this.blocks = registryEntryList;
         this.speed = optional;
         this.correctForDrops = optional2;
      }

      public static Rule ofAlwaysDropping(RegistryEntryList blocks, float speed) {
         return new Rule(blocks, Optional.of(speed), Optional.of(true));
      }

      public static Rule ofNeverDropping(RegistryEntryList blocks) {
         return new Rule(blocks, Optional.empty(), Optional.of(false));
      }

      public static Rule of(RegistryEntryList blocks, float speed) {
         return new Rule(blocks, Optional.of(speed), Optional.empty());
      }

      public RegistryEntryList blocks() {
         return this.blocks;
      }

      public Optional speed() {
         return this.speed;
      }

      public Optional correctForDrops() {
         return this.correctForDrops;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntryList(RegistryKeys.BLOCK), Rule::blocks, PacketCodecs.FLOAT.collect(PacketCodecs::optional), Rule::speed, PacketCodecs.BOOLEAN.collect(PacketCodecs::optional), Rule::correctForDrops, Rule::new);
      }
   }
}
