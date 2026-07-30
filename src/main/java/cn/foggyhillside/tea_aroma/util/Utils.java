package cn.foggyhillside.tea_aroma.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Utils {
    public static void addItem(ItemStack pStack, Player pPlayer, ItemStack pRemainingStack) {
        boolean flag = pPlayer.getAbilities().instabuild;
        pPlayer.awardStat(Stats.ITEM_USED.get(pStack.getItem()));
        if (flag) {
            if (pRemainingStack != null) {
                if (!pPlayer.getInventory().contains(pRemainingStack)) {
                    pPlayer.getInventory().add(pRemainingStack);
                }
            }
        } else {
            pStack.shrink(1);
            if (pRemainingStack != null) {
                if (!pPlayer.getInventory().add(pRemainingStack)) {
                    pPlayer.drop(pRemainingStack, false);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static ItemStack getResultItem(Recipe<?> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        } else {
            RegistryAccess registryAccess = level.registryAccess();
            return recipe.getResultItem(registryAccess);
        }
    }
}
