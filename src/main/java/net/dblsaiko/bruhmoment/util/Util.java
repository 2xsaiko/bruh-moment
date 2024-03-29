package net.dblsaiko.bruhmoment.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

import net.dblsaiko.bruhmoment.Configuration;

public class Util {
    public static boolean canInteract(PlayerEntity source, Entity target, Hand hand) {
        if (!isFilterEnabled(source)) {
            return true;
        }

        Identifier entityId = Registry.ENTITY_TYPE.getId(target.getType());
        ItemStack item = source.getStackInHand(hand);
        Identifier itemId = Registry.ITEM.getId(item.getItem());

        if (Configuration.entityInteractBlacklist.stream().anyMatch(e -> e.entityId().matches(entityId) && e.itemId().matches(itemId))) {
            return false;
        }

        return true;
    }

    public static boolean canInteract(PlayerEntity source, BlockState target, Hand hand) {
        if (!isFilterEnabled(source)) {
            return true;
        }

        Identifier blockId = Registry.BLOCK.getId(target.getBlock());
        ItemStack item = source.getStackInHand(hand);
        Identifier itemId = Registry.ITEM.getId(item.getItem());

        if (Configuration.blockInteractBlacklist.stream().anyMatch(e -> e.blockId().matches(blockId) && e.itemId().matches(itemId))) {
            return false;
        }

        return true;
    }

    public static boolean canAttack(PlayerEntity source, Entity target) {
        if (!isFilterEnabled(source)) {
            return true;
        }

        Identifier entityId = Registry.ENTITY_TYPE.getId(target.getType());
        ItemStack item = source.getMainHandStack();
        Identifier itemId = Registry.ITEM.getId(item.getItem());

        if (Configuration.entityAttackBlacklist.stream().anyMatch(e -> e.entityId().matches(entityId) && e.itemId().matches(itemId))) {
            return false;
        }

        return true;
    }

    public static boolean canUse(PlayerEntity source, Hand hand) {
        if (!isFilterEnabled(source)) {
            return true;
        }

        ItemStack item = source.getStackInHand(hand);
        Identifier itemId = Registry.ITEM.getId(item.getItem());

        if (Configuration.itemInteractBlacklist.stream().anyMatch(e -> e.itemId().matches(itemId))) {
            return false;
        }

        return true;
    }

    public static boolean isFilterEnabled(Entity entity) {
        GameMode gamemode = getGameMode(entity);

        if (gamemode == GameMode.SURVIVAL && Configuration.filterSurvival.get()) {
            return true;
        }

        if (gamemode == GameMode.CREATIVE && Configuration.filterCreative.get()) {
            return true;
        }

        if (gamemode != GameMode.ADVENTURE || !Configuration.filterAdventure.get()) {
            return false;
        }

        return true;
    }

    public static GameMode getGameMode(Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            return ((ServerPlayerEntity) entity).interactionManager.getGameMode();
        } else if (entity instanceof ClientPlayerEntity clientEntity) {
            PlayerListEntry playerListEntry =
                MinecraftClient.getInstance().getNetworkHandler()
                    .getPlayerListEntry(clientEntity.getGameProfile().getId());
            return playerListEntry != null ? playerListEntry.getGameMode() : null;
        } else {
            return null;
        }
    }
}
