package git.jbredwards.bpatcher.api.loading;

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
    @Nullable
    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(@Nonnull Map<String, Object> data) { }

    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }
}
