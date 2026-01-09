package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.PlayerInput;

public record InputPredicate(Optional forward, Optional backward, Optional left, Optional right, Optional jump, Optional sneak, Optional sprint) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("forward").forGetter(InputPredicate::forward), Codec.BOOL.optionalFieldOf("backward").forGetter(InputPredicate::backward), Codec.BOOL.optionalFieldOf("left").forGetter(InputPredicate::left), Codec.BOOL.optionalFieldOf("right").forGetter(InputPredicate::right), Codec.BOOL.optionalFieldOf("jump").forGetter(InputPredicate::jump), Codec.BOOL.optionalFieldOf("sneak").forGetter(InputPredicate::sneak), Codec.BOOL.optionalFieldOf("sprint").forGetter(InputPredicate::sprint)).apply(instance, InputPredicate::new);
   });

   public InputPredicate(Optional optional, Optional optional2, Optional optional3, Optional optional4, Optional optional5, Optional optional6, Optional optional7) {
      this.forward = optional;
      this.backward = optional2;
      this.left = optional3;
      this.right = optional4;
      this.jump = optional5;
      this.sneak = optional6;
      this.sprint = optional7;
   }

   public boolean matches(PlayerInput playerInput) {
      return this.keyMatches(this.forward, playerInput.forward()) && this.keyMatches(this.backward, playerInput.backward()) && this.keyMatches(this.left, playerInput.left()) && this.keyMatches(this.right, playerInput.right()) && this.keyMatches(this.jump, playerInput.jump()) && this.keyMatches(this.sneak, playerInput.sneak()) && this.keyMatches(this.sprint, playerInput.sprint());
   }

   private boolean keyMatches(Optional keyPressed, boolean inputPressed) {
      return (Boolean)keyPressed.map((pressed) -> {
         return pressed == inputPressed;
      }).orElse(true);
   }

   public Optional forward() {
      return this.forward;
   }

   public Optional backward() {
      return this.backward;
   }

   public Optional left() {
      return this.left;
   }

   public Optional right() {
      return this.right;
   }

   public Optional jump() {
      return this.jump;
   }

   public Optional sneak() {
      return this.sneak;
   }

   public Optional sprint() {
      return this.sprint;
   }
}
