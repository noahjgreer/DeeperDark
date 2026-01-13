/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.FontLoader
 *  net.minecraft.client.font.FontLoader$Loadable
 *  net.minecraft.client.font.FontLoader$Reference
 *  net.minecraft.client.font.FontType
 *  net.minecraft.client.font.FreeTypeUtil
 *  net.minecraft.client.font.TrueTypeFont
 *  net.minecraft.client.font.TrueTypeFontLoader
 *  net.minecraft.client.font.TrueTypeFontLoader$Shift
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.util.Identifier
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FreeType
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

@Environment(value=EnvType.CLIENT)
public record TrueTypeFontLoader(Identifier location, float size, float oversample, Shift shift, String skip) implements FontLoader
{
    private final Identifier location;
    private final float size;
    private final float oversample;
    private final Shift shift;
    private final String skip;
    private static final Codec<String> SKIP_CODEC = Codec.withAlternative((Codec)Codec.STRING, (Codec)Codec.STRING.listOf(), chars -> String.join((CharSequence)"", chars));
    public static final MapCodec<TrueTypeFontLoader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("file").forGetter(TrueTypeFontLoader::location), (App)Codec.FLOAT.optionalFieldOf("size", (Object)Float.valueOf(11.0f)).forGetter(TrueTypeFontLoader::size), (App)Codec.FLOAT.optionalFieldOf("oversample", (Object)Float.valueOf(1.0f)).forGetter(TrueTypeFontLoader::oversample), (App)Shift.CODEC.optionalFieldOf("shift", (Object)Shift.NONE).forGetter(TrueTypeFontLoader::shift), (App)SKIP_CODEC.optionalFieldOf("skip", (Object)"").forGetter(TrueTypeFontLoader::skip)).apply((Applicative)instance, TrueTypeFontLoader::new));

    public TrueTypeFontLoader(Identifier location, float size, float oversample, Shift shift, String skip) {
        this.location = location;
        this.size = size;
        this.oversample = oversample;
        this.shift = shift;
        this.skip = skip;
    }

    public FontType getType() {
        return FontType.TTF;
    }

    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        return Either.left(arg_0 -> this.load(arg_0));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Font load(ResourceManager resourceManager) throws IOException {
        FT_Face fT_Face = null;
        ByteBuffer byteBuffer = null;
        try (InputStream inputStream = resourceManager.open(this.location.withPrefixedPath("font/"));){
            byteBuffer = TextureUtil.readResource((InputStream)inputStream);
            Object object = FreeTypeUtil.LOCK;
            synchronized (object) {
                try (MemoryStack memoryStack = MemoryStack.stackPush();){
                    PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                    FreeTypeUtil.checkFatalError((int)FreeType.FT_New_Memory_Face((long)FreeTypeUtil.initialize(), (ByteBuffer)byteBuffer, (long)0L, (PointerBuffer)pointerBuffer), (String)"Initializing font face");
                    fT_Face = FT_Face.create((long)pointerBuffer.get());
                }
                String string = FreeType.FT_Get_Font_Format((FT_Face)fT_Face);
                if (!"TrueType".equals(string)) {
                    throw new IOException("Font is not in TTF format, was " + string);
                }
                FreeTypeUtil.checkFatalError((int)FreeType.FT_Select_Charmap((FT_Face)fT_Face, (int)FreeType.FT_ENCODING_UNICODE), (String)"Find unicode charmap");
                TrueTypeFont trueTypeFont = new TrueTypeFont(byteBuffer, fT_Face, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
                return trueTypeFont;
            }
        }
        catch (Exception exception) {
            Object object = FreeTypeUtil.LOCK;
            synchronized (object) {
                if (fT_Face != null) {
                    FreeType.FT_Done_Face(fT_Face);
                }
            }
            MemoryUtil.memFree((Buffer)byteBuffer);
            throw exception;
        }
    }

    public Identifier location() {
        return this.location;
    }

    public float size() {
        return this.size;
    }

    public float oversample() {
        return this.oversample;
    }

    public Shift shift() {
        return this.shift;
    }

    public String skip() {
        return this.skip;
    }
}

