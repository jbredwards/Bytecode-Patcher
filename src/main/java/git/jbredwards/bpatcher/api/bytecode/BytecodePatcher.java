package git.jbredwards.bpatcher.api.bytecode;

import com.google.common.collect.ImmutableList;
import git.jbredwards.bpatcher.api.debugging.DebugPrinter;
import net.minecraftforge.fml.repackage.com.nothome.delta.GDiffPatcher;

import javax.annotation.Nonnull;
import java.io.*;

/**
 *
 * @author jbred
 *
 */
public final class BytecodePatcher
{
    @Nonnull static final GDiffPatcher PATCHER = new GDiffPatcher();
    @Nonnull final ImmutableList<String> patches;
    @Nonnull final byte[] basicClass;
    boolean optional;

    @Nonnull String printedPathName = "";
    byte printBefore, printAfter;

    BytecodePatcher(@Nonnull byte[] basicClassIn, @Nonnull String patch, @Nonnull String... fallbacks) {
        patches = ImmutableList.<String>builder().add(patch).add(fallbacks).build();
        basicClass = basicClassIn;
    }

    @Nonnull
    public static BytecodePatcher of(@Nonnull byte[] basicClassIn, @Nonnull String patch, @Nonnull String... fallbacks) {
        return new BytecodePatcher(basicClassIn, patch, fallbacks);
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
     * @param type see {@link git.jbredwards.bpatcher.api.debugging.DebugOutputType DebugOutputType}
     */
    @Nonnull
    public BytecodePatcher printBefore(@Nonnull String path, byte type) {
        printedPathName = path;
        printBefore = type;
        return this;
    }

    /**
     * @param path usually transformedName (recommended)
     * @param type see {@link git.jbredwards.bpatcher.api.debugging.DebugOutputType DebugOutputType}
     */
    @Nonnull
    public BytecodePatcher printAfter(@Nonnull String path, byte type) {
        printedPathName = path;
        printAfter = type;
        return this;
    }

    /**
     * @param path usually transformedName (recommended)
     * @param type see {@link git.jbredwards.bpatcher.api.debugging.DebugOutputType DebugOutputType}
     */
    @Nonnull
    public BytecodePatcher print(@Nonnull String path, byte type) {
        printBefore(path, type);
        printAfter(path, type);
        return this;
    }

    @Nonnull
    public byte[] apply() {
        DebugPrinter.print(basicClass, printedPathName, "before", printBefore);
        return apply_internal(0);
    }

    @Nonnull
    byte[] apply_internal(int index) {
        final FileInputStream patch;
        try { patch = new FileInputStream(patches.get(index)); }
        catch(FileNotFoundException e) { throw new RuntimeException(e); }

        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            PATCHER.patch(basicClass, patch, output);

            final byte[] newClass = output.toByteArray();
            DebugPrinter.print(newClass, printedPathName, "after", printAfter);
            return newClass;
        }
        catch(IOException e) {
            //patch failed (likely due to a conflict), attempting fallback
            if(++index < patches.size()) return apply_internal(index);
        }

        //this transformer is optional so don't crash
        if(optional) return basicClass;
        //no fallbacks left, force crash as not crashing will likely result in issues down the line
        else throw new RuntimeException("Non-optional patches were not applied: " + patches.toString());
    }
}
