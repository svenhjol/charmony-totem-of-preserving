package svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving;

import svenhjol.charmony.core.annotations.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.enums.Side;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class TotemOfPreserving extends SidedFeature {
    public final Supplier<Common> common;
    public final Registers registers;

    public TotemOfPreserving(Mod mod) {
        super(mod);
        common = Common::new;
        registers = new Registers(this);
    }

    public static TotemOfPreserving feature() {
        return Mod.getSidedFeature(TotemOfPreserving.class);
    }
}
