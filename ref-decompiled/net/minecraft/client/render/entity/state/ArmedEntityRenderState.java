package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class ArmedEntityRenderState extends LivingEntityRenderState {
   public Arm mainArm;
   public BipedEntityModel.ArmPose rightArmPose;
   public final ItemRenderState rightHandItemState;
   public BipedEntityModel.ArmPose leftArmPose;
   public final ItemRenderState leftHandItemState;

   public ArmedEntityRenderState() {
      this.mainArm = Arm.RIGHT;
      this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
      this.rightHandItemState = new ItemRenderState();
      this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
      this.leftHandItemState = new ItemRenderState();
   }

   public ItemRenderState getMainHandItemState() {
      return this.mainArm == Arm.RIGHT ? this.rightHandItemState : this.leftHandItemState;
   }

   public static void updateRenderState(LivingEntity entity, ArmedEntityRenderState state, ItemModelManager itemModelManager) {
      state.mainArm = entity.getMainArm();
      itemModelManager.updateForLivingEntity(state.rightHandItemState, entity.getStackInArm(Arm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
      itemModelManager.updateForLivingEntity(state.leftHandItemState, entity.getStackInArm(Arm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
   }
}
