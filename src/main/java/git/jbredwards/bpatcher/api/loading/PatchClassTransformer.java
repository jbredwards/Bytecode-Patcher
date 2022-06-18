package git.jbredwards.bpatcher.api.loading;

import git.jbredwards.bpatcher.api.BytecodePatcher;
import net.minecraft.launchwrapper.IClassTransformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic class transformer that can apply patches at runtime.
 * @author jbred
 *
 */
public abstract class PatchClassTransformer implements IClassTransformer
{
    @Nonnull
    public final Map<String, BytecodePatcher> patches = new HashMap<>();
    public PatchClassTransformer() { gatherPatches(patches); }

    /**
     * Applies the registered patches.
     */
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        final @Nullable BytecodePatcher patch = patches.get(transformedName);
        return patch == null ? basicClass : patch.apply(basicClass);
    }

    /**
     * Use {@link Map#put patches.put} to register new patch entries for runtime,
     * where the key is the class location, and the value is the patch.
     */
    public abstract void gatherPatches(@Nonnull Map<String, BytecodePatcher> patches);
}