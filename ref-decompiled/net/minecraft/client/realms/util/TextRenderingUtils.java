/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.util.TextRenderingUtils
 *  net.minecraft.client.realms.util.TextRenderingUtils$Line
 *  net.minecraft.client.realms.util.TextRenderingUtils$LineSegment
 */
package net.minecraft.client.realms.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.TextRenderingUtils;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TextRenderingUtils {
    private TextRenderingUtils() {
    }

    @VisibleForTesting
    protected static List<String> lineBreak(String text) {
        return Arrays.asList(text.split("\\n"));
    }

    public static List<Line> decompose(String text, LineSegment ... links) {
        return TextRenderingUtils.decompose((String)text, Arrays.asList(links));
    }

    private static List<Line> decompose(String text, List<LineSegment> links) {
        List list = TextRenderingUtils.lineBreak((String)text);
        return TextRenderingUtils.insertLinks((List)list, links);
    }

    private static List<Line> insertLinks(List<String> lines, List<LineSegment> links) {
        int i = 0;
        ArrayList list = Lists.newArrayList();
        for (String string : lines) {
            ArrayList list2 = Lists.newArrayList();
            List list3 = TextRenderingUtils.split((String)string, (String)"%link");
            for (String string2 : list3) {
                if ("%link".equals(string2)) {
                    list2.add(links.get(i++));
                    continue;
                }
                list2.add(LineSegment.text((String)string2));
            }
            list.add(new Line((List)list2));
        }
        return list;
    }

    public static List<String> split(String line, String delimiter) {
        int j;
        if (delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be the empty string");
        }
        ArrayList list = Lists.newArrayList();
        int i = 0;
        while ((j = line.indexOf(delimiter, i)) != -1) {
            if (j > i) {
                list.add(line.substring(i, j));
            }
            list.add(delimiter);
            i = j + delimiter.length();
        }
        if (i < line.length()) {
            list.add(line.substring(i));
        }
        return list;
    }
}

