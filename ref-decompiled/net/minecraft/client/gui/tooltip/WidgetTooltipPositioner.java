/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.tooltip.TooltipPositioner
 *  net.minecraft.client.gui.tooltip.WidgetTooltipPositioner
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Vector2i
 *  org.joml.Vector2ic
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class WidgetTooltipPositioner
implements TooltipPositioner {
    private static final int field_42159 = 5;
    private static final int field_42160 = 12;
    public static final int field_42157 = 3;
    public static final int field_42158 = 5;
    private final ScreenRect focus;

    public WidgetTooltipPositioner(ScreenRect focus) {
        this.focus = focus;
    }

    public Vector2ic getPosition(int screenWidth, int screenHeight, int x, int y, int width, int height) {
        int k;
        Vector2i vector2i = new Vector2i(x + 12, y);
        if (vector2i.x + width > screenWidth - 5) {
            vector2i.x = Math.max(x - 12 - width, 9);
        }
        vector2i.y += 3;
        int i = height + 3 + 3;
        int j = this.focus.getBottom() + 3 + WidgetTooltipPositioner.getOffsetY((int)0, (int)0, (int)this.focus.height());
        vector2i.y = j + i <= (k = screenHeight - 5) ? (vector2i.y += WidgetTooltipPositioner.getOffsetY((int)vector2i.y, (int)this.focus.getTop(), (int)this.focus.height())) : (vector2i.y -= i + WidgetTooltipPositioner.getOffsetY((int)vector2i.y, (int)this.focus.getBottom(), (int)this.focus.height()));
        return vector2i;
    }

    private static int getOffsetY(int tooltipY, int widgetY, int widgetHeight) {
        int i = Math.min(Math.abs(tooltipY - widgetY), widgetHeight);
        return Math.round(MathHelper.lerp((float)((float)i / (float)widgetHeight), (float)(widgetHeight - 3), (float)5.0f));
    }
}

