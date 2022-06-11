package git.jbredwards.bpatcher.lib.forge;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * A bare bones IFMLLoadingPlugin implementation for other mods to use.
 * @author jbred
 *
 */
public abstract class FMLLoadingPlugin implements IFMLLoadingPlugin
{
    /**
     * Set to the correct value by the time
     * {@link net.minecraft.launchwrapper.IClassTransformer#transform(String, String, byte[]) IClassTransformer.transform()}
     * is called.
     */
    private static boolean obfuscated;
    public static boolean isObfuscated() { return obfuscated; }

    /**
     * Apply {@link Nonnull} annotation for clearer indication that this should not be null.
     */
    @Nonnull
    @Override
    public abstract String[] getASMTransformerClass();

    /**
     * Initializes the runtime obfuscation variable.
     */
    @Override
    public final void injectData(@Nonnull Map<String, Object> data) {
        obfuscated = (Boolean)data.get("runtimeDeobfuscationEnabled");
        gatherData(data);
    }

    /**
     * Same functionality as {@link FMLLoadingPlugin#injectData}, this exists to ensure the obfuscation variable is always set.
     */
    protected void gatherData(@Nonnull Map<String, Object> data) { }

    /**
     * Return null here since most mods don't use this.
     */
    @Nullable
    @Override
    public String getModContainerClass() { return null; }

    /**
     * Return null here since most mods don't use this.
     */
    @Nullable
    @Override
    public String getSetupClass() { return null; }

    /**
     * Return null here since most mods don't use this.
     */
    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }
}
