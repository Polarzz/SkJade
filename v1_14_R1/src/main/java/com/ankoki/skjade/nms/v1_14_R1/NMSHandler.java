package com.ankoki.skjade.nms.v1_14_R1;

import com.ankoki.skjade.api.NMS;
import net.minecraft.server.v1_14_R1.PacketPlayOutGameStateChange;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSHandler implements NMS {

    @Override
    public void sendDemo(Player player) {
        PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public boolean canBreak(ItemStack item, Material material) {
        return false;
    }
}