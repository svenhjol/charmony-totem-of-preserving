package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import svenhjol.charmony.api.TotemType;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.AnvilEvents;

import java.util.*;

public final class Handlers extends Setup<TotemOfPreserving> {
    public final Map<ResourceLocation, List<BlockPos>> protectedPositions = new HashMap<>();

    public Handlers(TotemOfPreserving feature) {
        super(feature);
    }

    public void playerEnteredBlock(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Player player
            && !player.level().isClientSide()
            && player.isAlive()
            && level.getBlockEntity(pos) instanceof TotemBlockEntity totemBlockEntity
            && (!feature().ownerOnly()
                || (totemBlockEntity.getOwner().equals(player.getUUID())
                || player.getAbilities().instabuild))
        ) {
            var serverLevel = (ServerLevel)level;
            var dimension = serverLevel.dimension().location();

            // Create a new totem item and give it to player.
            log().debug("Player has interacted with totem holder block at pos: " + pos + ", player: " + player);
            var totem = new ItemStack(feature().registers.item.get());

            // Get the data out of the block entity.
            var items = totemBlockEntity.getItems();
            var message = totemBlockEntity.getMessage();
            var damage = totemBlockEntity.getDamage();

            // Set data for the totem.
            TotemData.create()
                .setItems(items)
                .setMessage(message)
                .save(totem);

            totem.setDamageValue(damage);
            TotemItem.setGlint(totem, true);

            log().debug("Adding totem item to player's inventory: " + player);
            player.getInventory().add(totem);

            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6f, 1.0f);

            if (feature().handlers.protectedPositions.containsKey(dimension)) {
                feature().handlers.protectedPositions.get(dimension).remove(pos);
            }

            // Remove the totem block.
            log().debug("Removing totem holder block and block entity: " + pos);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    public void serverWantsToRemoveBlock(Level level, BlockPos pos) {
        var dimension = level.dimension().location();

        if (feature().handlers.protectedPositions.containsKey(dimension)
            && feature().handlers.protectedPositions.get(dimension).contains(pos)
            && level.getBlockEntity(pos) instanceof TotemBlockEntity totem
            && !level.isClientSide) {

            log().debug("Something wants to overwrite the totem block, emergency item drop");
            var items = totem.getItems();
            for (ItemStack stack : items) {
                var itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                level.addFreshEntity(itemEntity);
            }
        }
        log().debug("Going to remove a totem block");
    }

    @SuppressWarnings("unused")
    public InteractionResult playerInventoryDrop(Player player, Inventory inventory) {
        if (player.level().isClientSide) {
            return InteractionResult.PASS;
        }

        var totemItem = feature().registers.item.get();
        var found = ItemStack.EMPTY;
        var damage = 0; // Track how much damage the totem has taken
        var serverPlayer = (ServerPlayer)player;

        // Get items to preserve.
        List<ItemStack> preserveItems = new ArrayList<>();
        for (var provider : feature().registers.preservingProviders) {
            preserveItems.addAll(provider
                .getInventoryItemsForTotem(player)
                .stream().filter(i -> !i.isEmpty())
                .toList());
        }

        // When not in grave mode, look through inventory items for the first empty totem of preserving.
        if (!feature().graveMode()) {
            if (!feature().mustBeInHand()) {
                for (var provider : feature().registers.inventoryCheckProviders) {
                    var opt = provider.findTotemFromInventory(player, TotemType.Preserving);
                    if (opt.isPresent()) {
                        // Found totem in inventory.
                        log().debug("Found totem in inventory");
                        found = opt.get();
                        damage = found.getDamageValue();
                        break;
                    }
                }
            } else {
                for (EquipmentSlot slot : EquipmentSlotGroup.HAND) {
                    var held = player.getItemBySlot(slot);
                    if (!held.is(feature().registers.item.get())) {
                        continue;
                    }

                    var data = TotemData.get(held);
                    if (!data.items().isEmpty()) {
                        continue;
                    }

                    // Found totem in hand.
                    log().debug("Found totem in hand");
                    damage = held.getDamageValue();
                    found = held;
                    break;
                }
            }

            if (found.isEmpty()) {
                log().debug("Could not find an empty totem, giving up");
                return InteractionResult.PASS;
            }

            // Give up if the player doesn't have anything to preserve
            if (preserveItems.isEmpty() || preserveItems.size() == 1) {
                log().debug("No items to store in totem (graveMode = false), giving up");
                return InteractionResult.PASS;
            }
        }

        // Give up if the player doesn't have anything to preserve
        if (preserveItems.isEmpty()) {
            log().debug("No items to store in totem (graveMode = true), giving up");
            return InteractionResult.PASS;
        }

        // Remove the totem from the inventory so it doesn't get included in the preserved items.
        // This doesn't do anything in graveMode because found is always empty.
        if (!found.isEmpty()) {
            found.setCount(0);
            preserveItems = preserveItems.stream().filter(i -> !i.isEmpty()).toList(); // refilter to remove air
        }

        // Place a totem block in the world.
        var result = tryCreateTotemBlock(serverPlayer, preserveItems, damage);
        if (!result) {
            return InteractionResult.PASS;
        }

        // Delete player inventory items.
        for (var provider : feature().registers.preservingProviders) {
            provider.deleteInventoryItems(player);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean tryCreateTotemBlock(ServerPlayer player, List<ItemStack> preserve, int damage) {
        var level = player.level();
        var random = player.getRandom();
        var uuid = player.getUUID();
        var message = player.getScoreboardName();

        // Get the death position.
        var pos = player.blockPosition();
        var vehicle = player.getVehicle();
        if (vehicle != null) {
            pos = vehicle.blockPosition();
        }

        // Do spawn checks.
        BlockPos spawnPos = null;
        var maxHeight = level.getMaxY() - 1;
        var minHeight = level.getMinY() + 1;
        var state = level.getBlockState(pos);
        var fluid = level.getFluidState(pos);

        // Adjust for void.
        if (pos.getY() < minHeight) {
            pos = new BlockPos(pos.getX(), level.getSeaLevel(), pos.getZ());
            log().debug("(Void check) Adjusting, new pos: " + pos);
        }

        if (state.isAir() || fluid.is(FluidTags.WATER)) {

            // Air and water are valid spawn positions.
            log().debug("(Standard check) Found an air/water block to spawn in: " + pos);
            spawnPos = pos;

        } else if (fluid.is(FluidTags.LAVA)) {

            // Lava: Keep moving upward to find air pocket, give up if solid (or void) reached.
            for (int tries = 0; tries < 20; tries++) {
                var y = pos.getY() + tries;
                if (y >= maxHeight) break;

                var tryPos = new BlockPos(pos.getX(), y, pos.getZ());
                var tryState = level.getBlockState(tryPos);
                var tryFluid = level.getFluidState(tryPos);
                if (tryFluid.is(FluidTags.LAVA)) continue;

                if (tryState.isAir() || tryFluid.is(FluidTags.WATER)) {
                    log().debug("(Lava check) Found an air/water block to spawn in after checking " + tries + " times: " + pos);
                    spawnPos = tryPos;
                }

                break;
            }

            // If that failed, replace the lava with the totem.
            if (spawnPos == null) {
                log().debug("(Lava check) Going to replace lava with totem at: " + pos);
                spawnPos = pos;
            }

        } else {

            // Solid block: Check above and nesw for an air or water pocket.
            List<Direction> directions = Arrays.asList(
                Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH
            );
            for (var direction : directions) {
                var tryPos = pos.relative(direction);
                var tryState = level.getBlockState(tryPos);
                var tryFluid = level.getFluidState(tryPos);

                if (tryPos.getY() >= maxHeight) continue;
                if (tryState.isAir() || tryFluid.is(FluidTags.WATER)) {
                    log().debug("(Solid check) Found an air/water block to spawn in, direction: " + direction + ", pos: " + pos);
                    spawnPos = tryPos;
                    break;
                }
            }
        }

        if (spawnPos == null) {

            // Try and find a valid pos within 8 blocks of the death position.
            for (var tries = 0; tries < 8; tries++) {
                var x = pos.getX() + random.nextInt(tries + 1) - tries;
                var z = pos.getZ() + random.nextInt(tries + 1) - tries;

                // If upper void reached, count downward.
                var y = pos.getY() + tries;
                if (y > maxHeight) {
                    y = pos.getY() - tries;
                    if (y < minHeight) continue;
                }

                // Look for fluid and replacable solid blocks at increasing distance from the death point.
                var tryPos = new BlockPos(x, y, z);
                var tryState = level.getBlockState(tryPos);
                var tryFluid = level.getFluidState(tryPos);
                if (tryState.isAir() || tryFluid.is(FluidTags.WATER)) {
                    log().debug("(Distance check) Found an air/water block to spawn in after checking " + tries + " times: " + pos);
                    spawnPos = tryPos;
                    break;
                }
            }
        }

        if (spawnPos == null) {
            log().debug("Could not find a block to spawn totem, giving up.");
            return false;
        }

        level.setBlockAndUpdate(spawnPos, feature().registers.block.get().defaultBlockState());
        if (!(level.getBlockEntity(spawnPos) instanceof TotemBlockEntity totemBlockEntity)) {
            log().debug("Not a valid block entity at pos, giving up. Pos: " + pos);
            return false;
        }

        totemBlockEntity.setItems(preserve);
        totemBlockEntity.setMessage(message);
        totemBlockEntity.setOwner(uuid);
        totemBlockEntity.setDamage(damage);
        totemBlockEntity.setChanged();
        totemBlockEntity.setDirty();

        feature().handlers.protectedPositions
            .computeIfAbsent(level.dimension().location(), a -> new ArrayList<>())
            .add(spawnPos);

        log().info("Spawned a totem at: " + spawnPos);
        feature().advancements.usedTotem(player);
        level.playSound(null, spawnPos, feature().registers.storeSound.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

        // Show the death position as chat message.
        if (feature().showDeathPositionInChat()) {
            var x = spawnPos.getX();
            var y = spawnPos.getY();
            var z = spawnPos.getZ();

            player.displayClientMessage(Component.translatable("gui.charmony-totem-of-preserving.death_position", x, y, z), false);
        }

        return true;
    }

    @SuppressWarnings("unused")
    public Optional<AnvilEvents.AnvilRecipe> anvilUpdate(Player player, ItemStack input, ItemStack material, long cost) {
        if (input.is(feature().registers.item.get()) && input.isDamaged() && material.is(Items.ECHO_SHARD)) {
            var recipe = new AnvilEvents.AnvilRecipe();
            recipe.output = input.copy();
            recipe.output.setDamageValue(input.getDamageValue() - 1);
            recipe.experienceCost = 1;
            recipe.materialCost = 1;
            return Optional.of(recipe);
        }
        return Optional.empty();
    }

    public InteractionResult useTotemInHand(Level level, Player player, InteractionHand hand) {
        var totem = player.getItemInHand(hand);
        var data = TotemData.get(totem);
        var pos = player.blockPosition();
        var destroyTotem = false;

        // Don't break totem if it's empty.
        if (data.items().isEmpty()) {
            var newTotem = givePlayerCleanTotem(player, hand);
            newTotem.setDamageValue(totem.getDamageValue());
            return InteractionResult.PASS;
        }

        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6f, 1.0f);

        if (feature().graveMode()) {

            // Always destroy totem in Grave Mode.
            destroyTotem = true;

        } else if (feature().durability() <= 0) {

            givePlayerCleanTotem(player, hand);

        } else {

            // Durability config of 1 or more causes damage to the totem.
            var damage = totem.getDamageValue();
            if (damage < totem.getMaxDamage() && (totem.getMaxDamage() - damage > 1)) {

                var newTotem = givePlayerCleanTotem(player, hand);
                newTotem.setDamageValue(damage + 1);

            } else {
                destroyTotem = true;
            }
        }

        if (!destroyTotem) {
            level.playSound(null, pos, feature().registers.releaseSound.get(), SoundSource.PLAYERS, 0.8f, 1.0f);
        } else {
            destroyTotem(totem, player);
        }

        if (!level.isClientSide) {
            // Add totem items to the world.
            for (var stack : data.items()) {
                var itemEntity = new ItemEntity(level, pos.getX(), pos.getY() + 0.5d, pos.getZ(), stack);
                level.addFreshEntity(itemEntity);
            }
        }

        return InteractionResult.PASS;
    }


    private ItemStack givePlayerCleanTotem(Player player, InteractionHand hand) {
        var stack = new ItemStack(feature().registers.item.get());
        player.setItemInHand(hand, stack);
        return stack;
    }

    private void destroyTotem(ItemStack stack, Player player) {
        if (!player.level().isClientSide) {
            destroyTotemServer(player, stack);
        } else {
            destroyTotemClient(player.blockPosition());
        }
    }

    private void destroyTotemServer(Player player, ItemStack totem) {
        if (player.hasInfiniteMaterials()) {
            return;
        }

        totem.setCount(0);

        if (!player.level().isClientSide) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8f, 1.0f);
        }
    }

    public void destroyTotemClient(BlockPos pos) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        var spread = 1.5d;
        for (int i = 0; i < 10; i++) {
            var px = pos.getX() + 0.5d + (Math.random() - 0.5d) * spread;
            var py = pos.getY() + 0.5d + (Math.random() - 0.5d) * spread;
            var pz = pos.getZ() + 0.5d + (Math.random() - 0.5d) * spread;
            minecraft.level.addParticle(ParticleTypes.LARGE_SMOKE, px, py, pz, 0.0d, 0.1d, 0.0d);
        }
    }
}
