package git.jbredwards.bpatcher.api;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.repackage.com.nothome.delta.GDiffPatcher;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author jbred
 *
 */
public final class BytecodePatcher
{
    static final boolean RECALC_FRAMES = Boolean.parseBoolean(System.getProperty("FORGE_FORCE_FRAME_RECALC", "false"));
    static final int WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | (RECALC_FRAMES ? ClassWriter.COMPUTE_FRAMES : 0);
    static final int READER_FLAGS = RECALC_FRAMES ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES;

    @Nonnull static final GDiffPatcher PATCHER = new GDiffPatcher();
    @Nonnull final String[] patches;

    @Nonnull String printedPathName = "";
    byte printBefore, printAfter;
    boolean optional;

    BytecodePatcher(@Nonnull String patch, @Nonnull String[] fallbacks) {
        patches = new String[fallbacks.length + 1];
        patches[0] = patch;

        System.arraycopy(fallbacks, 0, patches, 1, fallbacks.length);
    }

    @Nonnull
    public static BytecodePatcher of(@Nonnull String patch, @Nonnull String... fallbacks) {
        return new BytecodePatcher(patch, fallbacks);
    }

    /**
     * Call this if you want the patch to be optional (ie. not crash the game if it fails to be applied).
     * Should only be called if this patch not being applied won't cause any issues down the line!
     */
    @Nonnull
    public BytecodePatcher setOptional() {
        optional = true;
        return this;
    }

    /**
     * @param path usually transformedName (recommended)
     * @param type see {@link DebugOutputType DebugOutputType}
     */
    @Nonnull
    public BytecodePatcher printBefore(@Nonnull String path, byte type) {
        printedPathName = path;
        printBefore = type;
        return this;
    }

    /**
     * @param path usually transformedName (recommended)
     * @param type see {@link DebugOutputType DebugOutputType}
     */
    @Nonnull
    public BytecodePatcher printAfter(@Nonnull String path, byte type) {
        printedPathName = path;
        printAfter = type;
        return this;
    }

    /**
     * @param path usually transformedName (recommended)
     * @param type see {@link DebugOutputType DebugOutputType}
     */
    @Nonnull
    public BytecodePatcher print(@Nonnull String path, byte type) {
        printBefore(path, type);
        printAfter(path, type);
        return this;
    }

    /**
     * Applies the patches.
     */
    @Nonnull
    public byte[] apply(@Nonnull byte[] basicClass) {
        DebugWriter.write(basicClass, printedPathName + "/before", printBefore);
        return apply_internal(obfuscateBasicClass(basicClass), 0);
    }

    @Nonnull
    byte[] apply_internal(@Nonnull byte[] basicClass, int index) {
        final FileInputStream patchFile;
        try { patchFile = new FileInputStream(patches[index]); }
        catch(FileNotFoundException e) { throw new RuntimeException(e); }

        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            PATCHER.patch(basicClass, patchFile, output);
            IOUtils.closeQuietly(patchFile);

            final byte[] newClass = deobfuscateIfNeeded(output.toByteArray());
            DebugWriter.write(newClass, printedPathName + "/after", printAfter);
            return newClass;
        }
        catch(IOException e) {
            IOUtils.closeQuietly(patchFile);
            //patch failed (likely due to a conflict), attempting fallback
            if(++index < patches.length) return apply_internal(basicClass, index);
        }

        //this transformer is optional so don't crash
        if(optional) return basicClass;
        //no fallbacks left, force crash as not crashing will likely result in issues down the line
        else throw new RuntimeException("Non-optional patch was not applied, nor were any fallbacks: " + patches[0]);
    }

    /**
     * Obfuscates the basicClass bytes, this is done so the patch can be applied
     * (patches are always obfuscated, as to boost performance outside development).
     */
    byte[] obfuscateBasicClass(@Nonnull byte[] basicClass) {
        if(FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            //TODO
        }

        //not in a deobfuscated environment, therefore the class is already obfuscated
        return basicClass;
    }

    /**
     * Deobfuscates the class if needed, this is done changes can be read while in a development environment.
     */
    byte[] deobfuscateIfNeeded(@Nonnull byte[] obfuscatedClass) {
        if(FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            final ClassWriter writer = new ClassWriter(WRITER_FLAGS);
            new ClassReader(obfuscatedClass).accept(new FMLRemappingAdapter(writer), READER_FLAGS);
            return writer.toByteArray();
        }

        //not in a deobfuscated environment, do nothing
        return obfuscatedClass;
    }
}
