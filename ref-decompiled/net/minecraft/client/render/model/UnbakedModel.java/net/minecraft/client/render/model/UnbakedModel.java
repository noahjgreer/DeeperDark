/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface UnbakedModel {
    public static final String PARTICLE_TEXTURE = "particle";

    default public @Nullable Boolean ambientOcclusion() {
        return null;
    }

    default public @Nullable GuiLight guiLight() {
        return null;
    }

    default public @Nullable ModelTransformation transformations() {
        return null;
    }

    default public ModelTextures.Textures textures() {
        return ModelTextures.Textures.EMPTY;
    }

    default public @Nullable Geometry geometry() {
        return null;
    }

    default public @Nullable Identifier parent() {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class GuiLight
    extends Enum<GuiLight> {
        public static final /* enum */ GuiLight ITEM = new GuiLight("front");
        public static final /* enum */ GuiLight BLOCK = new GuiLight("side");
        private final String name;
        private static final /* synthetic */ GuiLight[] field_21861;

        public static GuiLight[] values() {
            return (GuiLight[])field_21861.clone();
        }

        public static GuiLight valueOf(String string) {
            return Enum.valueOf(GuiLight.class, string);
        }

        private GuiLight(String name) {
            this.name = name;
        }

        public static GuiLight byName(String value) {
            for (GuiLight guiLight : GuiLight.values()) {
                if (!guiLight.name.equals(value)) continue;
                return guiLight;
            }
            throw new IllegalArgumentException("Invalid gui light: " + value);
        }

        public boolean isSide() {
            return this == BLOCK;
        }

        private static /* synthetic */ GuiLight[] method_36920() {
            return new GuiLight[]{ITEM, BLOCK};
        }

        static {
            field_21861 = GuiLight.method_36920();
        }
    }
}
