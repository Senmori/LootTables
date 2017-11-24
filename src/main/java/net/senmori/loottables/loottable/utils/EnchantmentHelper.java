package net.senmori.loottables.loottable.utils;

import com.google.common.collect.Maps;
import net.senmori.loottables.loottable.core.LootEnchantment;
import net.senmori.loottables.loottable.core.RandomValueRange;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class EnchantmentHelper {

    private static final Random rand = new Random();
    private static final Map<LootEnchantment, Map<Integer, RandomValueRange>> powerLevels = Maps.newHashMap();

    static {
        if (powerLevels.isEmpty()) {
            buildPowerLevels();
        }
    }


    public static ItemStack addRandomEnchant(ItemStack stack, int xpLevel, boolean allowTreasure) {
        List<LootEnchantment> list = buildEnchantmentList(stack, allowTreasure);
        if (list.isEmpty()) return stack;
        xpLevel = calcLevel(stack, xpLevel);
        int totalWeight = 0;
        for (LootEnchantment e : list) {
            totalWeight += e.getWeight();
        }

        Collections.shuffle(list); // randomize list
        Enchantment randEnchant = null;
        if (stack.getType().equals(Material.BOOK)) {
            stack.setType(Material.ENCHANTED_BOOK);
            while (rand.nextInt(50) <= xpLevel) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                randEnchant = getWeightedEnchant(list, totalWeight);
                int powerLevel = getPowerLevel(randEnchant, xpLevel);
                if (meta.hasStoredEnchant(randEnchant) && meta.getStoredEnchantLevel(randEnchant) < powerLevel) {
                    do {
                        randEnchant = getWeightedEnchant(list, totalWeight);
                    } while (! meta.hasStoredEnchant(randEnchant));
                    powerLevel = getPowerLevel(randEnchant, xpLevel);
                }
                meta.addStoredEnchant(randEnchant, powerLevel, false);
                stack.setItemMeta(meta);
                xpLevel /= 2;
            }
            return stack;
        } else {
            while (rand.nextInt(50) <= xpLevel) {
                randEnchant = getWeightedEnchant(list, totalWeight);
                int powerLevel = getPowerLevel(randEnchant, xpLevel);
                stack.addEnchantment(randEnchant, powerLevel);
                xpLevel /= 2;
            }
            return stack;
        }
    }

    private static Enchantment getWeightedEnchant(List<LootEnchantment> enchants, int totalWeight) {
        Collections.shuffle(enchants);
        for (LootEnchantment e : enchants) {
            totalWeight -= e.getWeight();
            if (totalWeight <= 0) {
                return e.getEnchant();
            }
        }
        return enchants.get(rand.nextInt(enchants.size())).getEnchant(); // completely random enchant
    }

    /** Returns a list of all possible enchantments possible for the given itemstack */
    public static List<LootEnchantment> buildEnchantmentList(ItemStack stack, boolean allowTreasure) {
        ArrayList<LootEnchantment> list = new ArrayList<>();
        if (getEnchantability(stack.getType()) <= 0) {
            return list;
        } else {
            for (LootEnchantment enchant : LootEnchantment.values()) {
                if (stack.getType().equals(Material.BOOK)) {
                    if (! allowTreasure && enchant.isTreasure()) continue;
                    list.add(enchant);
                    continue;
                }
                if (enchant.getEnchant().canEnchantItem(stack)) {
                    if (! allowTreasure && enchant.isTreasure()) continue;
                    list.add(enchant);
                }
            }
            return list;
        }
    }

    private static int calcLevel(ItemStack stack, int xpLevel) {
        int i = getEnchantability(stack.getType());
        xpLevel = xpLevel + 1 + rand.nextInt(i / 4 + 1) + rand.nextInt(i / 4 + 1);
        float f = ( rand.nextFloat() + rand.nextFloat() - 1.0F ) * .015F;
        xpLevel = MathHelper.clampInt(Math.round((float) xpLevel + (float) xpLevel * f), 1, 2147483647);
        return xpLevel < 1 ? 1 : xpLevel;
    }


    public static int getEnchantability(Material material) {
        switch (material) {
            case BOW:
            case BOOK:
                return 1;
            case WOOD_AXE:
            case WOOD_HOE:
            case WOOD_SPADE:
            case WOOD_PICKAXE:
            case WOOD_SWORD:
                return 15;
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
                return 15;
            case STONE_AXE:
            case STONE_HOE:
            case STONE_SPADE:
            case STONE_PICKAXE:
            case STONE_SWORD:
                return 5;
            case IRON_AXE:
            case IRON_HOE:
            case IRON_SPADE:
            case IRON_PICKAXE:
            case IRON_SWORD:
                return 14;
            case IRON_BOOTS:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_HELMET:
                return 9;
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
                return 12;
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_SPADE:
            case DIAMOND_PICKAXE:
            case DIAMOND_SWORD:
                return 10;
            case GOLD_AXE:
            case GOLD_HOE:
            case GOLD_SPADE:
            case GOLD_PICKAXE:
            case GOLD_SWORD:
                return 22;
            case GOLD_HELMET:
            case GOLD_LEGGINGS:
            case GOLD_CHESTPLATE:
            case GOLD_BOOTS:
                return 25;
            default:
                return - 1;
        }
    }

    /**
     * Returns a set containing two numbers(min & max), that will give you the given <br> enchantment at the appropriate
     * power level, with the given amount of xp levels. <br>
     *
     * @param enchant - the enchantment
     * @param xpLevel - the amount of experience(in levels)
     *
     * @return
     */
    private static int getPowerLevel(Enchantment enchant, int xpLevel) {
        LootEnchantment e = LootEnchantment.fromEnchantment(enchant);
        if (e == null) throw new IllegalArgumentException("Unknown enchantment \'" + enchant.getName() + "\'");
        Map<Integer, RandomValueRange> levels = powerLevels.get(e);
        int level = 0;
        int i = 0;
        for (Integer index : levels.keySet()) {
            RandomValueRange current = levels.get(index);
            if (current.isInRange(xpLevel) || ( ! current.isInRange(xpLevel) && xpLevel > current.getMax() )) {
                level = index > level ? index : level;
            }
            if (index >= enchant.getMaxLevel()) {
                return enchant.getMaxLevel();
            }
        }
        return level;
    }

    private static void buildPowerLevels() {
        // Protection
        Map<Integer, RandomValueRange> protection = new HashMap<>();
        protection.put(1, new RandomValueRange(1, 21));
        protection.put(2, new RandomValueRange(12, 32));
        protection.put(3, new RandomValueRange(23, 43));
        protection.put(4, new RandomValueRange(34, 54));
        powerLevels.put(LootEnchantment.PROTECTION, protection);

        // Fire Protection
        Map<Integer, RandomValueRange> fireProt = new HashMap<>();
        fireProt.put(1, new RandomValueRange(10, 22));
        fireProt.put(2, new RandomValueRange(18, 30));
        fireProt.put(3, new RandomValueRange(26, 38));
        fireProt.put(4, new RandomValueRange(34, 46));
        powerLevels.put(LootEnchantment.FIRE_PROTECTION, fireProt);

        // Feather Falling
        Map<Integer, RandomValueRange> featherFall = new HashMap<>();
        featherFall.put(1, new RandomValueRange(5, 15));
        featherFall.put(2, new RandomValueRange(11, 21));
        featherFall.put(3, new RandomValueRange(17, 27));
        featherFall.put(4, new RandomValueRange(23, 33));
        powerLevels.put(LootEnchantment.FEATHER_FALLING, featherFall);

        // Blast Protection
        Map<Integer, RandomValueRange> blast = new HashMap<>();
        blast.put(1, new RandomValueRange(5, 17));
        blast.put(2, new RandomValueRange(13, 25));
        blast.put(3, new RandomValueRange(21, 33));
        blast.put(4, new RandomValueRange(29, 41));
        powerLevels.put(LootEnchantment.BLAST_PROTECTION, blast);

        // Projectile Protection
        Map<Integer, RandomValueRange> projectile = new HashMap<>();
        projectile.put(1, new RandomValueRange(3, 18));
        projectile.put(2, new RandomValueRange(9, 24));
        projectile.put(3, new RandomValueRange(15, 30));
        projectile.put(4, new RandomValueRange(21, 36));
        powerLevels.put(LootEnchantment.PROJECTILE_PROTECTION, projectile);

        // Respiration
        Map<Integer, RandomValueRange> respiration = new HashMap<>();
        respiration.put(1, new RandomValueRange(10, 40));
        respiration.put(2, new RandomValueRange(20, 50));
        respiration.put(3, new RandomValueRange(30, 60));
        powerLevels.put(LootEnchantment.RESPIRATION, respiration);

        // Aqua Affinity
        Map<Integer, RandomValueRange> aquaAffinity = new HashMap<>();
        aquaAffinity.put(1, new RandomValueRange(1, 41));
        powerLevels.put(LootEnchantment.AQUA_AFFINITY, aquaAffinity);

        // Thorns
        Map<Integer, RandomValueRange> thorns = new HashMap<>();
        thorns.put(1, new RandomValueRange(10, 60));
        thorns.put(2, new RandomValueRange(30, 80));
        thorns.put(3, new RandomValueRange(50, 100));
        powerLevels.put(LootEnchantment.THORNS, thorns);

        // Depth Strider
        Map<Integer, RandomValueRange> depthStrider = new HashMap<>();
        depthStrider.put(1, new RandomValueRange(10, 25));
        depthStrider.put(2, new RandomValueRange(20, 35));
        depthStrider.put(3, new RandomValueRange(30, 45));
        powerLevels.put(LootEnchantment.DEPTH_STRIDER, depthStrider);

        // Frost Walker
        Map<Integer, RandomValueRange> frostWalker = new HashMap<>();
        frostWalker.put(1, new RandomValueRange(10, 25));
        frostWalker.put(2, new RandomValueRange(20, 35));
        powerLevels.put(LootEnchantment.FROST_WALKER, frostWalker);

        // Sharpness
        Map<Integer, RandomValueRange> sharpness = new HashMap<>();
        sharpness.put(1, new RandomValueRange(1, 21));
        sharpness.put(2, new RandomValueRange(12, 32));
        sharpness.put(3, new RandomValueRange(23, 43));
        sharpness.put(4, new RandomValueRange(34, 54));
        sharpness.put(5, new RandomValueRange(45, 65));
        powerLevels.put(LootEnchantment.SHARPNESS, sharpness);

        // Smite
        Map<Integer, RandomValueRange> smite = new HashMap<>();
        smite.put(1, new RandomValueRange(5, 25));
        smite.put(2, new RandomValueRange(12, 32));
        smite.put(3, new RandomValueRange(23, 43));
        smite.put(4, new RandomValueRange(34, 54));
        smite.put(5, new RandomValueRange(45, 65));
        powerLevels.put(LootEnchantment.SMITE, smite);

        // Bane of Arthropods
        Map<Integer, RandomValueRange> bane = new HashMap<>();
        bane.put(1, new RandomValueRange(5, 25));
        bane.put(2, new RandomValueRange(13, 33));
        bane.put(3, new RandomValueRange(21, 41));
        bane.put(4, new RandomValueRange(29, 49));
        bane.put(5, new RandomValueRange(37, 57));
        powerLevels.put(LootEnchantment.BANE_OF_ARTHROPODS, bane);

        // Knockback
        Map<Integer, RandomValueRange> knockback = new HashMap<>();
        knockback.put(1, new RandomValueRange(5, 55));
        knockback.put(2, new RandomValueRange(25, 75));
        powerLevels.put(LootEnchantment.KNOCKBACK, knockback);

        // Fire Aspect
        Map<Integer, RandomValueRange> fireAspect = new HashMap<>();
        fireAspect.put(1, new RandomValueRange(10, 60));
        fireAspect.put(2, new RandomValueRange(30, 80));
        powerLevels.put(LootEnchantment.FIRE_ASPECT, fireAspect);

        // Looting
        Map<Integer, RandomValueRange> looting = new HashMap<>();
        looting.put(1, new RandomValueRange(15, 65));
        looting.put(2, new RandomValueRange(24, 74));
        looting.put(3, new RandomValueRange(33, 83));
        powerLevels.put(LootEnchantment.LOOTING, looting);

        // Power
        Map<Integer, RandomValueRange> power = new HashMap<>();
        power.put(1, new RandomValueRange(1, 16));
        power.put(2, new RandomValueRange(11, 26));
        power.put(3, new RandomValueRange(21, 36));
        power.put(4, new RandomValueRange(31, 46));
        power.put(5, new RandomValueRange(41, 56));
        powerLevels.put(LootEnchantment.POWER, power);

        // Punch
        Map<Integer, RandomValueRange> punch = new HashMap<>();
        punch.put(1, new RandomValueRange(12, 37));
        punch.put(2, new RandomValueRange(32, 57));
        powerLevels.put(LootEnchantment.PUNCH, punch);

        // Flame
        Map<Integer, RandomValueRange> flame = new HashMap<>();
        flame.put(1, new RandomValueRange(20, 50));
        powerLevels.put(LootEnchantment.FLAME, flame);

        // Infinity
        Map<Integer, RandomValueRange> infinity = new HashMap<>();
        infinity.put(1, new RandomValueRange(20, 50));
        powerLevels.put(LootEnchantment.INFINITY, infinity);

        // Efficiency
        Map<Integer, RandomValueRange> efficiency = new HashMap<>();
        efficiency.put(1, new RandomValueRange(1, 51));
        efficiency.put(2, new RandomValueRange(11, 61));
        efficiency.put(3, new RandomValueRange(21, 71));
        efficiency.put(4, new RandomValueRange(31, 81));
        efficiency.put(5, new RandomValueRange(41, 91));
        powerLevels.put(LootEnchantment.EFFICIENCY, efficiency);

        // Silk Touch
        Map<Integer, RandomValueRange> silk = new HashMap<>();
        silk.put(1, new RandomValueRange(15, 65));
        powerLevels.put(LootEnchantment.SILK_TOUCH, silk);

        // Unbreaking
        Map<Integer, RandomValueRange> unbreaking = new HashMap<>();
        unbreaking.put(1, new RandomValueRange(5, 55));
        unbreaking.put(2, new RandomValueRange(13, 63));
        unbreaking.put(3, new RandomValueRange(21, 71));
        powerLevels.put(LootEnchantment.UNBREAKING, unbreaking);

        // Fortune
        Map<Integer, RandomValueRange> fortune = new HashMap<>();
        fortune.put(1, new RandomValueRange(15, 65));
        fortune.put(2, new RandomValueRange(24, 74));
        fortune.put(3, new RandomValueRange(33, 83));
        powerLevels.put(LootEnchantment.FORTUNE, fortune);

        // Luck of the Sea
        Map<Integer, RandomValueRange> luckOfSea = new HashMap<>();
        luckOfSea.put(1, new RandomValueRange(15, 65));
        luckOfSea.put(2, new RandomValueRange(24, 74));
        luckOfSea.put(3, new RandomValueRange(33, 83));
        powerLevels.put(LootEnchantment.LUCK_OF_THE_SEA, luckOfSea);

        // Lure
        Map<Integer, RandomValueRange> lure = new HashMap<>();
        lure.put(1, new RandomValueRange(15, 65));
        lure.put(2, new RandomValueRange(24, 74));
        lure.put(3, new RandomValueRange(33, 83));
        powerLevels.put(LootEnchantment.LURE, lure);

        // Mending
        Map<Integer, RandomValueRange> mending = new HashMap<>();
        mending.put(1, new RandomValueRange(25, 75));
        powerLevels.put(LootEnchantment.MENDING, mending);
    }
}
