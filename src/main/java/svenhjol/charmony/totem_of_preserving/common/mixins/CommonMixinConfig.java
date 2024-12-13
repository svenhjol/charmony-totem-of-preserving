package svenhjol.charmony.totem_of_preserving.common.mixins;

import svenhjol.charmony.core.base.MixinConfig;
import svenhjol.charmony.core.enums.Side;
import svenhjol.charmony.totem_of_preserving.TotemOfPreservingMod;

public class CommonMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return TotemOfPreservingMod.ID;
    }

    @Override
    protected String modRoot() {
        return "svenhjol.charmony.totem_of_preserving";
    }

    @Override
    protected Side side() {
        return Side.Common;
    }
}
