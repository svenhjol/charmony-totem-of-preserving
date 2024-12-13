package svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving;

import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.Handlers;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.Registers;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemOfPreserving;

public final class Common {
    public final Registers registers;
    public final Handlers handlers;

    public Common() {
        var common = TotemOfPreserving.feature();
        registers = common.registers;
        handlers = common.handlers;
    }
}
