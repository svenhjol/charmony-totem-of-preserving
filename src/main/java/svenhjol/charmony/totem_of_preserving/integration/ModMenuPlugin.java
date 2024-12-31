package svenhjol.charmony.totem_of_preserving.integration;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.integration.BaseModMenuPlugin;
import svenhjol.charmony.totem_of_preserving.TotemOfPreservingMod;

public class ModMenuPlugin extends BaseModMenuPlugin {
    @Override
    public Mod mod() {
        return TotemOfPreservingMod.instance();
    }
}
