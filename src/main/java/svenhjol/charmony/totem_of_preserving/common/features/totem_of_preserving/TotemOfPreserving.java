package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = "Protects your items on death.")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class TotemOfPreserving extends SidedFeature {
    public final Advancements advancements;
    public final Registers registers;
    public final Handlers handlers;
    public final Providers providers;

    @Configurable(
        name = "Grave mode",
        requireRestart = false,
        description = """
            If true, a totem of preserving will always be created when you die.
            If false, you must be holding a totem of preserving to preserve your items on death."""
    )
    private static boolean graveMode = false;

    @Configurable(
        name = "Must be in hand",
        requireRestart = false,
        description = """
            If true, you must be holding a totem of preserving in your main hand or off-hand for it to preserve your items.
            If false, the totem will work if it is in your hands or your inventory.
            Note: This has no effect if 'Grave mode' is enabled."""
    )
    private static boolean mustBeInHand = false;

    @Configurable(
        name = "Durability",
        description = """
            The maximum number of times a single totem can be used. Once a totem runs out of uses it is destroyed.
            A value of -1 means that the totem is never destroyed.
            You can add an echo shard on an anvil to increase the durability of the totem.
            Note: Durability has no effect if 'Grave mode' is enabled."""
    )
    private static int durability = 3;

    @Configurable(
        name = "Owner only",
        requireRestart = false,
        description = "If true, only the owner of the totem may pick it up."
    )
    private static boolean ownerOnly = false;

    @Configurable(
        name = "Show death position",
        requireRestart = false,
        description = "If true, the coordinates where you died will be added to the player's chat screen."
    )
    private static boolean showDeathPositionInChat = false;

    public TotemOfPreserving(Mod instance) {
        super(instance);

        advancements = new Advancements(this);
        handlers = new Handlers(this);
        registers = new Registers(this);
        providers = new Providers(this);
    }

    public static TotemOfPreserving feature() {
        return Mod.getSidedFeature(TotemOfPreserving.class);
    }

    public boolean graveMode() {
        return graveMode;
    }

    public boolean mustBeInHand() {
        return mustBeInHand;
    }

    public int durability() {
        return Mth.clamp(durability, -1, 1000);
    }

    public boolean ownerOnly() {
        return ownerOnly;
    }

    public boolean showDeathPositionInChat() {
        return showDeathPositionInChat;
    }

}
