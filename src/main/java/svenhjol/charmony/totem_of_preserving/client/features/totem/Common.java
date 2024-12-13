package svenhjol.charmony.totem_of_preserving.client.features.totem;

import svenhjol.charmony.totem_of_preserving.common.features.totem.Handlers;
import svenhjol.charmony.totem_of_preserving.common.features.totem.Registers;
import svenhjol.charmony.totem_of_preserving.common.features.totem.Totem;

public final class Common {
    public final Registers registers;
    public final Handlers handlers;

    public Common() {
        var common = Totem.feature();
        registers = common.registers;
        handlers = common.handlers;
    }
}
