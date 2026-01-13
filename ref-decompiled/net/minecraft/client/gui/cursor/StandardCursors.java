/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.cursor.Cursor
 *  net.minecraft.client.gui.cursor.StandardCursors
 */
package net.minecraft.client.gui.cursor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.cursor.Cursor;

@Environment(value=EnvType.CLIENT)
public class StandardCursors {
    public static final Cursor ARROW = Cursor.createStandard((int)221185, (String)"arrow", (Cursor)Cursor.DEFAULT);
    public static final Cursor IBEAM = Cursor.createStandard((int)221186, (String)"ibeam", (Cursor)Cursor.DEFAULT);
    public static final Cursor CROSSHAIR = Cursor.createStandard((int)221187, (String)"crosshair", (Cursor)Cursor.DEFAULT);
    public static final Cursor POINTING_HAND = Cursor.createStandard((int)221188, (String)"pointing_hand", (Cursor)Cursor.DEFAULT);
    public static final Cursor RESIZE_NS = Cursor.createStandard((int)221190, (String)"resize_ns", (Cursor)Cursor.DEFAULT);
    public static final Cursor RESIZE_EW = Cursor.createStandard((int)221189, (String)"resize_ew", (Cursor)Cursor.DEFAULT);
    public static final Cursor RESIZE_ALL = Cursor.createStandard((int)221193, (String)"resize_all", (Cursor)Cursor.DEFAULT);
    public static final Cursor NOT_ALLOWED = Cursor.createStandard((int)221194, (String)"not_allowed", (Cursor)Cursor.DEFAULT);
}

