package net.minecraft.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InteractionEntity extends Entity implements Attackable, Targeter {
   private static final TrackedData WIDTH;
   private static final TrackedData HEIGHT;
   private static final TrackedData RESPONSE;
   private static final String WIDTH_KEY = "width";
   private static final String HEIGHT_KEY = "height";
   private static final String ATTACK_KEY = "attack";
   private static final String INTERACTION_KEY = "interaction";
   private static final String RESPONSE_KEY = "response";
   private static final float DEFAULT_WIDTH = 1.0F;
   private static final float DEFAULT_HEIGHT = 1.0F;
   private static final boolean DEFAULT_RESPONSE = false;
   @Nullable
   private Interaction attack;
   @Nullable
   private Interaction interaction;

   public InteractionEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.noClip = true;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(WIDTH, 1.0F);
      builder.add(HEIGHT, 1.0F);
      builder.add(RESPONSE, false);
   }

   protected void readCustomData(ReadView view) {
      this.setInteractionWidth(view.getFloat("width", 1.0F));
      this.setInteractionHeight(view.getFloat("height", 1.0F));
      this.attack = (Interaction)view.read("attack", InteractionEntity.Interaction.CODEC).orElse((Object)null);
      this.interaction = (Interaction)view.read("interaction", InteractionEntity.Interaction.CODEC).orElse((Object)null);
      this.setResponse(view.getBoolean("response", false));
      this.setBoundingBox(this.calculateBoundingBox());
   }

   protected void writeCustomData(WriteView view) {
      view.putFloat("width", this.getInteractionWidth());
      view.putFloat("height", this.getInteractionHeight());
      view.putNullable("attack", InteractionEntity.Interaction.CODEC, this.attack);
      view.putNullable("interaction", InteractionEntity.Interaction.CODEC, this.interaction);
      view.putBoolean("response", this.shouldRespond());
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (HEIGHT.equals(data) || WIDTH.equals(data)) {
         this.calculateDimensions();
      }

   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean canHit() {
      return true;
   }

   public PistonBehavior getPistonBehavior() {
      return PistonBehavior.IGNORE;
   }

   public boolean canAvoidTraps() {
      return true;
   }

   public boolean handleAttack(Entity attacker) {
      if (attacker instanceof PlayerEntity playerEntity) {
         this.attack = new Interaction(playerEntity.getUuid(), this.getWorld().getTime());
         if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, this, playerEntity.getDamageSources().generic(), 1.0F, 1.0F, false);
         }

         return !this.shouldRespond();
      } else {
         return false;
      }
   }

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      return false;
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      if (this.getWorld().isClient) {
         return this.shouldRespond() ? ActionResult.SUCCESS : ActionResult.CONSUME;
      } else {
         this.interaction = new Interaction(player.getUuid(), this.getWorld().getTime());
         return ActionResult.CONSUME;
      }
   }

   public void tick() {
   }

   @Nullable
   public LivingEntity getLastAttacker() {
      return this.attack != null ? this.getWorld().getPlayerByUuid(this.attack.player()) : null;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.interaction != null ? this.getWorld().getPlayerByUuid(this.interaction.player()) : null;
   }

   public final void setInteractionWidth(float width) {
      this.dataTracker.set(WIDTH, width);
   }

   public final float getInteractionWidth() {
      return (Float)this.dataTracker.get(WIDTH);
   }

   public final void setInteractionHeight(float height) {
      this.dataTracker.set(HEIGHT, height);
   }

   public final float getInteractionHeight() {
      return (Float)this.dataTracker.get(HEIGHT);
   }

   public final void setResponse(boolean response) {
      this.dataTracker.set(RESPONSE, response);
   }

   public final boolean shouldRespond() {
      return (Boolean)this.dataTracker.get(RESPONSE);
   }

   private EntityDimensions getDimensions() {
      return EntityDimensions.changing(this.getInteractionWidth(), this.getInteractionHeight());
   }

   public EntityDimensions getDimensions(EntityPose pose) {
      return this.getDimensions();
   }

   protected Box calculateDefaultBoundingBox(Vec3d pos) {
      return this.getDimensions().getBoxAt(pos);
   }

   static {
      WIDTH = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
      HEIGHT = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
      RESPONSE = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   static record Interaction(UUID player, long timestamp) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Uuids.INT_STREAM_CODEC.fieldOf("player").forGetter(Interaction::player), Codec.LONG.fieldOf("timestamp").forGetter(Interaction::timestamp)).apply(instance, Interaction::new);
      });

      Interaction(UUID uUID, long l) {
         this.player = uUID;
         this.timestamp = l;
      }

      public UUID player() {
         return this.player;
      }

      public long timestamp() {
         return this.timestamp;
      }
   }
}
