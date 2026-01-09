package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;

@Environment(EnvType.CLIENT)
public interface FeatureRendererContext {
   EntityModel getModel();
}
