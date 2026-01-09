package net.minecraft.loot.provider.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import org.jetbrains.annotations.Nullable;

public class ContextLootNbtProvider implements LootNbtProvider {
   private static final String BLOCK_ENTITY_TARGET_NAME = "block_entity";
   private static final Target BLOCK_ENTITY_TARGET = new Target() {
      public NbtElement getNbt(LootContext context) {
         BlockEntity blockEntity = (BlockEntity)context.get(LootContextParameters.BLOCK_ENTITY);
         return blockEntity != null ? blockEntity.createNbtWithIdentifyingData(blockEntity.getWorld().getRegistryManager()) : null;
      }

      public String getName() {
         return "block_entity";
      }

      public Set getRequiredParameters() {
         return Set.of(LootContextParameters.BLOCK_ENTITY);
      }
   };
   public static final ContextLootNbtProvider BLOCK_ENTITY;
   private static final Codec TARGET_CODEC;
   public static final MapCodec CODEC;
   public static final Codec INLINE_CODEC;
   private final Target target;

   private static Target getTarget(final LootContext.EntityTarget entityTarget) {
      return new Target() {
         @Nullable
         public NbtElement getNbt(LootContext context) {
            Entity entity = (Entity)context.get(entityTarget.getParameter());
            return entity != null ? NbtPredicate.entityToNbt(entity) : null;
         }

         public String getName() {
            return entityTarget.name();
         }

         public Set getRequiredParameters() {
            return Set.of(entityTarget.getParameter());
         }
      };
   }

   private ContextLootNbtProvider(Target target) {
      this.target = target;
   }

   public LootNbtProviderType getType() {
      return LootNbtProviderTypes.CONTEXT;
   }

   @Nullable
   public NbtElement getNbt(LootContext context) {
      return this.target.getNbt(context);
   }

   public Set getRequiredParameters() {
      return this.target.getRequiredParameters();
   }

   public static LootNbtProvider fromTarget(LootContext.EntityTarget target) {
      return new ContextLootNbtProvider(getTarget(target));
   }

   static {
      BLOCK_ENTITY = new ContextLootNbtProvider(BLOCK_ENTITY_TARGET);
      TARGET_CODEC = Codec.STRING.xmap((type) -> {
         if (type.equals("block_entity")) {
            return BLOCK_ENTITY_TARGET;
         } else {
            LootContext.EntityTarget entityTarget = LootContext.EntityTarget.fromString(type);
            return getTarget(entityTarget);
         }
      }, Target::getName);
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TARGET_CODEC.fieldOf("target").forGetter((provider) -> {
            return provider.target;
         })).apply(instance, ContextLootNbtProvider::new);
      });
      INLINE_CODEC = TARGET_CODEC.xmap(ContextLootNbtProvider::new, (provider) -> {
         return provider.target;
      });
   }

   private interface Target {
      @Nullable
      NbtElement getNbt(LootContext context);

      String getName();

      Set getRequiredParameters();
   }
}
