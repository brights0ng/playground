package net.brights0ng.playground.utilClasses;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlayerUtils {

    public static void sendMessage(ServerPlayerEntity player, String message, String color) {
        player.sendMessage(Text.literal(message).setStyle(Style.EMPTY.withColor(Formatting.valueOf(color.toUpperCase()))), false);
    }


}
