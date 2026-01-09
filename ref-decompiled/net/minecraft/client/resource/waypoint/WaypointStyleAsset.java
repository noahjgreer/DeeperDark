package net.minecraft.client.resource.waypoint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public record WaypointStyleAsset(int nearDistance, int farDistance, List sprites, List spriteLocations) {
   public static final int DEFAULT_NEAR_DISTANCE = 128;
   public static final int DEFAULT_FAR_DISTANCE = 332;
   private static final Codec DISTANCE_CODEC = Codec.intRange(0, 60000000);
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(DISTANCE_CODEC.optionalFieldOf("near_distance", 128).forGetter(WaypointStyleAsset::nearDistance), DISTANCE_CODEC.optionalFieldOf("far_distance", 332).forGetter(WaypointStyleAsset::farDistance), Codecs.nonEmptyList(Identifier.CODEC.listOf()).fieldOf("sprites").forGetter(WaypointStyleAsset::sprites)).apply(instance, WaypointStyleAsset::new);
   }).validate(WaypointStyleAsset::validate);

   public WaypointStyleAsset(int nearDistance, int farDistance, List sprites) {
      this(nearDistance, farDistance, sprites, sprites.stream().map((id) -> {
         return id.withPrefixedPath("hud/locator_bar_dot/");
      }).toList());
   }

   public WaypointStyleAsset(int i, int j, List list, List list2) {
      this.nearDistance = i;
      this.farDistance = j;
      this.sprites = list;
      this.spriteLocations = list2;
   }

   private DataResult validate() {
      return this.nearDistance >= this.farDistance ? DataResult.error(() -> {
         return "Far distance (" + this.farDistance + ") cannot be closer or equal to near distance (" + this.nearDistance + ")";
      }) : DataResult.success(this);
   }

   public Identifier getSpriteForDistance(float distance) {
      if (distance <= (float)this.nearDistance) {
         return (Identifier)this.spriteLocations.getFirst();
      } else if (distance >= (float)this.farDistance) {
         return (Identifier)this.spriteLocations.getLast();
      } else {
         int i = MathHelper.lerp((distance - (float)this.nearDistance) / (float)(this.farDistance - this.nearDistance), 0, this.spriteLocations.size());
         return (Identifier)this.spriteLocations.get(i);
      }
   }

   public int nearDistance() {
      return this.nearDistance;
   }

   public int farDistance() {
      return this.farDistance;
   }

   public List sprites() {
      return this.sprites;
   }

   public List spriteLocations() {
      return this.spriteLocations;
   }
}
