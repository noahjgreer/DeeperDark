package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public record MapBannerMarker(BlockPos pos, DyeColor color, Optional name) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(MapBannerMarker::pos), DyeColor.CODEC.lenientOptionalFieldOf("color", DyeColor.WHITE).forGetter(MapBannerMarker::color), TextCodecs.CODEC.lenientOptionalFieldOf("name").forGetter(MapBannerMarker::name)).apply(instance, MapBannerMarker::new);
   });

   public MapBannerMarker(BlockPos pos, DyeColor dyeColor, Optional optional) {
      this.pos = pos;
      this.color = dyeColor;
      this.name = optional;
   }

   @Nullable
   public static MapBannerMarker fromWorldBlock(BlockView blockView, BlockPos blockPos) {
      BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
      if (blockEntity instanceof BannerBlockEntity bannerBlockEntity) {
         DyeColor dyeColor = bannerBlockEntity.getColorForState();
         Optional optional = Optional.ofNullable(bannerBlockEntity.getCustomName());
         return new MapBannerMarker(blockPos, dyeColor, optional);
      } else {
         return null;
      }
   }

   public RegistryEntry getDecorationType() {
      RegistryEntry var10000;
      switch (this.color) {
         case WHITE:
            var10000 = MapDecorationTypes.BANNER_WHITE;
            break;
         case ORANGE:
            var10000 = MapDecorationTypes.BANNER_ORANGE;
            break;
         case MAGENTA:
            var10000 = MapDecorationTypes.BANNER_MAGENTA;
            break;
         case LIGHT_BLUE:
            var10000 = MapDecorationTypes.BANNER_LIGHT_BLUE;
            break;
         case YELLOW:
            var10000 = MapDecorationTypes.BANNER_YELLOW;
            break;
         case LIME:
            var10000 = MapDecorationTypes.BANNER_LIME;
            break;
         case PINK:
            var10000 = MapDecorationTypes.BANNER_PINK;
            break;
         case GRAY:
            var10000 = MapDecorationTypes.BANNER_GRAY;
            break;
         case LIGHT_GRAY:
            var10000 = MapDecorationTypes.BANNER_LIGHT_GRAY;
            break;
         case CYAN:
            var10000 = MapDecorationTypes.BANNER_CYAN;
            break;
         case PURPLE:
            var10000 = MapDecorationTypes.BANNER_PURPLE;
            break;
         case BLUE:
            var10000 = MapDecorationTypes.BANNER_BLUE;
            break;
         case BROWN:
            var10000 = MapDecorationTypes.BANNER_BROWN;
            break;
         case GREEN:
            var10000 = MapDecorationTypes.BANNER_GREEN;
            break;
         case RED:
            var10000 = MapDecorationTypes.BANNER_RED;
            break;
         case BLACK:
            var10000 = MapDecorationTypes.BANNER_BLACK;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String getKey() {
      int var10000 = this.pos.getX();
      return "banner-" + var10000 + "," + this.pos.getY() + "," + this.pos.getZ();
   }

   public BlockPos pos() {
      return this.pos;
   }

   public DyeColor color() {
      return this.color;
   }

   public Optional name() {
      return this.name;
   }
}
