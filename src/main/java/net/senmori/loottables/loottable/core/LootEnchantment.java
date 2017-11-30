package net.senmori.loottables.loottable.core;

import org.bukkit.enchantments.Enchantment;

/*
    Helper class to hold enchantments for loot tables specifically since the
    Enchantment's Rarity is required.
    Order isn't important here since they will eventually be shuffled to get randomly selected.

    I chose to use default Minecraft names to make it easier to serialize/deserialize them.
    Please do not change.
 */
public enum LootEnchantment {

    // Non-treasure enchantments
    PROTECTION(10, false, Enchantment.PROTECTION_ENVIRONMENTAL),
    FIRE_PROTECTION(5, false, Enchantment.PROTECTION_FIRE),
    PROJECTILE_PROTECTION(5, false, Enchantment.PROTECTION_PROJECTILE),
    BLAST_PROTECTION(2, false, Enchantment.PROTECTION_EXPLOSIONS),
    FEATHER_FALLING(5, false, Enchantment.PROTECTION_FALL),
    AQUA_AFFINITY(2, false, Enchantment.WATER_WORKER),
    RESPIRATION(2, false, Enchantment.OXYGEN),
    DEPTH_STRIDER(2, false, Enchantment.DEPTH_STRIDER),
    THORNS(1, false, Enchantment.THORNS),
    SHARPNESS(10, false, Enchantment.DAMAGE_ALL),
    BANE_OF_ARTHROPODS(5, false, Enchantment.DAMAGE_ARTHROPODS),
    KNOCKBACK(5, false, Enchantment.KNOCKBACK),
    SMITE(5, false, Enchantment.DAMAGE_UNDEAD),
    FIRE_ASPECT(2, false, Enchantment.FIRE_ASPECT),
    LOOTING(2, false, Enchantment.LOOT_BONUS_MOBS),
    EFFICIENCY(10, false, Enchantment.DIG_SPEED),
    UNBREAKING(5, false, Enchantment.DURABILITY),
    FORTUNE(2, false, Enchantment.LOOT_BONUS_BLOCKS),
    SILK_TOUCH(1, false, Enchantment.SILK_TOUCH),
    POWER(10, false, Enchantment.ARROW_DAMAGE),
    FLAME(2, false, Enchantment.ARROW_FIRE),
    PUNCH(2, false, Enchantment.ARROW_KNOCKBACK),
    INFINITY(1, false, Enchantment.ARROW_INFINITE),
    LUCK_OF_THE_SEA(2, false, Enchantment.LUCK),
    LURE(2, false, Enchantment.LURE),

    // Treasure enchantments
    MENDING(2, true, Enchantment.MENDING),
    FROST_WALKER(2, true, Enchantment.FROST_WALKER);


    private int weight;
    private boolean treasure;
    private Enchantment enchant;

    LootEnchantment(int weight, boolean isTreasure, Enchantment enchant) {
        this.weight = weight;
        this.treasure = isTreasure;
        this.enchant = enchant;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isTreasure() {
        return this.treasure;
    }

    public Enchantment getEnchant() {
        return this.enchant;
    }


    public static LootEnchantment fromEnchantment(Enchantment enchant) {
        for (LootEnchantment e : values()) {
            if (e.getEnchant().getName().equals(enchant.getName())) {
                return e;
            }
        }
        return null;
    }
}
