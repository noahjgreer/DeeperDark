package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TropicalFishEntity extends SchoolingFishEntity {
   public static final Variant DEFAULT_VARIANT;
   private static final TrackedData VARIANT;
   public static final List COMMON_VARIANTS;
   private boolean commonSpawn = true;

   public TropicalFishEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public static String getToolTipForVariant(int variant) {
      return "entity.minecraft.tropical_fish.predefined." + variant;
   }

   static int getVariantId(Pattern variety, DyeColor baseColor, DyeColor patternColor) {
      return variety.getIndex() & '\uffff' | (baseColor.getIndex() & 255) << 16 | (patternColor.getIndex() & 255) << 24;
   }

   public static DyeColor getBaseColor(int variant) {
      return DyeColor.byIndex(variant >> 16 & 255);
   }

   public static DyeColor getPatternColor(int variant) {
      return DyeColor.byIndex(variant >> 24 & 255);
   }

   public static Pattern getVariety(int variant) {
      return TropicalFishEntity.Pattern.byIndex(variant & '\uffff');
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, DEFAULT_VARIANT.getId());
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("Variant", TropicalFishEntity.Variant.CODEC, new Variant(this.getTropicalFishVariant()));
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      Variant variant = (Variant)view.read("Variant", TropicalFishEntity.Variant.CODEC).orElse(DEFAULT_VARIANT);
      this.setTropicalFishVariant(variant.getId());
   }

   private void setTropicalFishVariant(int variant) {
      this.dataTracker.set(VARIANT, variant);
   }

   public boolean spawnsTooManyForEachTry(int count) {
      return !this.commonSpawn;
   }

   private int getTropicalFishVariant() {
      return (Integer)this.dataTracker.get(VARIANT);
   }

   public DyeColor getBaseColor() {
      return getBaseColor(this.getTropicalFishVariant());
   }

   public DyeColor getPatternColor() {
      return getPatternColor(this.getTropicalFishVariant());
   }

   public Pattern getVariety() {
      return getVariety(this.getTropicalFishVariant());
   }

   private void setVariety(Pattern variety) {
      int i = this.getTropicalFishVariant();
      DyeColor dyeColor = getBaseColor(i);
      DyeColor dyeColor2 = getPatternColor(i);
      this.setTropicalFishVariant(getVariantId(variety, dyeColor, dyeColor2));
   }

   private void setBaseColor(DyeColor baseColor) {
      int i = this.getTropicalFishVariant();
      Pattern pattern = getVariety(i);
      DyeColor dyeColor = getPatternColor(i);
      this.setTropicalFishVariant(getVariantId(pattern, baseColor, dyeColor));
   }

   private void setPatternColor(DyeColor patternColor) {
      int i = this.getTropicalFishVariant();
      Pattern pattern = getVariety(i);
      DyeColor dyeColor = getBaseColor(i);
      this.setTropicalFishVariant(getVariantId(pattern, dyeColor, patternColor));
   }

   @Nullable
   public Object get(ComponentType type) {
      if (type == DataComponentTypes.TROPICAL_FISH_PATTERN) {
         return castComponentValue(type, this.getVariety());
      } else if (type == DataComponentTypes.TROPICAL_FISH_BASE_COLOR) {
         return castComponentValue(type, this.getBaseColor());
      } else {
         return type == DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR ? castComponentValue(type, this.getPatternColor()) : super.get(type);
      }
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.TROPICAL_FISH_PATTERN);
      this.copyComponentFrom(from, DataComponentTypes.TROPICAL_FISH_BASE_COLOR);
      this.copyComponentFrom(from, DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.TROPICAL_FISH_PATTERN) {
         this.setVariety((Pattern)castComponentValue(DataComponentTypes.TROPICAL_FISH_PATTERN, value));
         return true;
      } else if (type == DataComponentTypes.TROPICAL_FISH_BASE_COLOR) {
         this.setBaseColor((DyeColor)castComponentValue(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, value));
         return true;
      } else if (type == DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR) {
         this.setPatternColor((DyeColor)castComponentValue(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public void copyDataToStack(ItemStack stack) {
      super.copyDataToStack(stack);
      stack.copy(DataComponentTypes.TROPICAL_FISH_PATTERN, this);
      stack.copy(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, this);
      stack.copy(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, this);
   }

   public ItemStack getBucketItem() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      EntityData entityData = super.initialize(world, difficulty, spawnReason, entityData);
      Random random = world.getRandom();
      Variant variant;
      if (entityData instanceof TropicalFishData tropicalFishData) {
         variant = tropicalFishData.variant;
      } else if ((double)random.nextFloat() < 0.9) {
         variant = (Variant)Util.getRandom(COMMON_VARIANTS, random);
         entityData = new TropicalFishData(this, variant);
      } else {
         this.commonSpawn = false;
         Pattern[] patterns = TropicalFishEntity.Pattern.values();
         DyeColor[] dyeColors = DyeColor.values();
         Pattern pattern = (Pattern)Util.getRandom((Object[])patterns, random);
         DyeColor dyeColor = (DyeColor)Util.getRandom((Object[])dyeColors, random);
         DyeColor dyeColor2 = (DyeColor)Util.getRandom((Object[])dyeColors, random);
         variant = new Variant(pattern, dyeColor, dyeColor2);
      }

      this.setTropicalFishVariant(variant.getId());
      return (EntityData)entityData;
   }

   public static boolean canTropicalFishSpawn(EntityType type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
      return world.getFluidState(pos.down()).isIn(FluidTags.WATER) && world.getBlockState(pos.up()).isOf(Blocks.WATER) && (world.getBiome(pos).isIn(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterCreatureEntity.canSpawn(type, world, reason, pos, random));
   }

   static {
      DEFAULT_VARIANT = new Variant(TropicalFishEntity.Pattern.KOB, DyeColor.WHITE, DyeColor.WHITE);
      VARIANT = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
      COMMON_VARIANTS = List.of(new Variant(TropicalFishEntity.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new Variant(TropicalFishEntity.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new Variant(TropicalFishEntity.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new Variant(TropicalFishEntity.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFishEntity.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new Variant(TropicalFishEntity.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), new Variant(TropicalFishEntity.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new Variant(TropicalFishEntity.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new Variant(TropicalFishEntity.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new Variant(TropicalFishEntity.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFishEntity.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFishEntity.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new Variant(TropicalFishEntity.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), new Variant(TropicalFishEntity.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new Variant(TropicalFishEntity.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFishEntity.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), new Variant(TropicalFishEntity.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFishEntity.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFishEntity.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFishEntity.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new Variant(TropicalFishEntity.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new Variant(TropicalFishEntity.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW));
   }

   public static enum Pattern implements StringIdentifiable, TooltipAppender {
      KOB("kob", TropicalFishEntity.Size.SMALL, 0),
      SUNSTREAK("sunstreak", TropicalFishEntity.Size.SMALL, 1),
      SNOOPER("snooper", TropicalFishEntity.Size.SMALL, 2),
      DASHER("dasher", TropicalFishEntity.Size.SMALL, 3),
      BRINELY("brinely", TropicalFishEntity.Size.SMALL, 4),
      SPOTTY("spotty", TropicalFishEntity.Size.SMALL, 5),
      FLOPPER("flopper", TropicalFishEntity.Size.LARGE, 0),
      STRIPEY("stripey", TropicalFishEntity.Size.LARGE, 1),
      GLITTER("glitter", TropicalFishEntity.Size.LARGE, 2),
      BLOCKFISH("blockfish", TropicalFishEntity.Size.LARGE, 3),
      BETTY("betty", TropicalFishEntity.Size.LARGE, 4),
      CLAYFISH("clayfish", TropicalFishEntity.Size.LARGE, 5);

      public static final Codec CODEC = StringIdentifiable.createCodec(Pattern::values);
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(Pattern::getIndex, values(), (Object)KOB);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Pattern::getIndex);
      private final String id;
      private final Text text;
      private final Size size;
      private final int index;

      private Pattern(final String id, final Size size, final int index) {
         this.id = id;
         this.size = size;
         this.index = size.index | index << 8;
         this.text = Text.translatable("entity.minecraft.tropical_fish.type." + this.id);
      }

      public static Pattern byIndex(int index) {
         return (Pattern)INDEX_MAPPER.apply(index);
      }

      public Size getSize() {
         return this.size;
      }

      public int getIndex() {
         return this.index;
      }

      public String asString() {
         return this.id;
      }

      public Text getText() {
         return this.text;
      }

      public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
         DyeColor dyeColor = (DyeColor)components.getOrDefault(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, TropicalFishEntity.DEFAULT_VARIANT.baseColor());
         DyeColor dyeColor2 = (DyeColor)components.getOrDefault(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, TropicalFishEntity.DEFAULT_VARIANT.patternColor());
         Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
         int i = TropicalFishEntity.COMMON_VARIANTS.indexOf(new Variant(this, dyeColor, dyeColor2));
         if (i != -1) {
            textConsumer.accept(Text.translatable(TropicalFishEntity.getToolTipForVariant(i)).formatted(formattings));
         } else {
            textConsumer.accept(this.text.copyContentOnly().formatted(formattings));
            MutableText mutableText = Text.translatable("color.minecraft." + dyeColor.getId());
            if (dyeColor != dyeColor2) {
               mutableText.append(", ").append((Text)Text.translatable("color.minecraft." + dyeColor2.getId()));
            }

            mutableText.formatted(formattings);
            textConsumer.accept(mutableText);
         }
      }

      // $FF: synthetic method
      private static Pattern[] method_36643() {
         return new Pattern[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
      }
   }

   public static record Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
      public static final Codec CODEC;

      public Variant(int id) {
         this(TropicalFishEntity.getVariety(id), TropicalFishEntity.getBaseColor(id), TropicalFishEntity.getPatternColor(id));
      }

      public Variant(Pattern pattern, DyeColor dyeColor, DyeColor dyeColor2) {
         this.pattern = pattern;
         this.baseColor = dyeColor;
         this.patternColor = dyeColor2;
      }

      public int getId() {
         return TropicalFishEntity.getVariantId(this.pattern, this.baseColor, this.patternColor);
      }

      public Pattern pattern() {
         return this.pattern;
      }

      public DyeColor baseColor() {
         return this.baseColor;
      }

      public DyeColor patternColor() {
         return this.patternColor;
      }

      static {
         CODEC = Codec.INT.xmap(Variant::new, Variant::getId);
      }
   }

   private static class TropicalFishData extends SchoolingFishEntity.FishData {
      final Variant variant;

      TropicalFishData(TropicalFishEntity leader, Variant variant) {
         super(leader);
         this.variant = variant;
      }
   }

   public static enum Size {
      SMALL(0),
      LARGE(1);

      final int index;

      private Size(final int index) {
         this.index = index;
      }

      // $FF: synthetic method
      private static Size[] method_47866() {
         return new Size[]{SMALL, LARGE};
      }
   }
}
