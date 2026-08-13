package cn.foggyhillside.tea_aroma.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

    public static Component buildProcessTooltip(List<String> completedKeys, List<String> nextKeys) {
        MutableComponent line = Component.empty();
        for (int i = 0; i < completedKeys.size(); i++) {
            if (i > 0) line.append(" > ");
            line.append(Component.translatable(completedKeys.get(i)).withStyle(ChatFormatting.GRAY));
        }
        if (!nextKeys.isEmpty()) {
            line.append(Component.literal(" > ").withStyle(ChatFormatting.GRAY));
            line.append(Component.literal("[").withStyle(ChatFormatting.WHITE));
            for (int i = 0; i < nextKeys.size(); i++) {
                if (i > 0) line.append(" / ");
                line.append(Component.translatable(nextKeys.get(i)).withStyle(ChatFormatting.WHITE));
            }
            line.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
        }
        return line;
    }
}