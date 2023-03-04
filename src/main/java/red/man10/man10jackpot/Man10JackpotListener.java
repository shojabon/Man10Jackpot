package red.man10.man10jackpot;

import ToolMenu.NumericInputMenu;
import com.shojabon.mcutils.Utils.BaseUtils;
import com.shojabon.mcutils.Utils.SInventory.SInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by sho-pc on 2017/04/19.
 */
public class Man10JackpotListener implements Listener {

    private final Man10Jackpot plugin;

    public Man10JackpotListener(Man10Jackpot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(plugin.someOneInMenu == false){
            return;
        }
        if (!plugin.playersInMenu.contains(p.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        if(plugin.inGame == true){
            return;
        }
        if(plugin.playerMenuState.get(p).equalsIgnoreCase("main")){
            e.setCancelled(true);
            if (e.getSlot() == 53) {
                p.closeInventory();
                plugin.playersInMenu.remove(p.getUniqueId());
                if (plugin.playersInMenu.isEmpty()) {
                    plugin.someOneInMenu = false;
                }
                return;
            }
            if (e.getSlot() == 50) {
                if (plugin.itemList.size() < 36) {
                    return;
                }
                int r = plugin.playerMenuPage.get(p) + 1;
                plugin.playerMenuPage.put(p, r);
                for (int i = 0; i < 36; i++) {
                    e.getInventory().setItem(i, plugin.itemList.get((plugin.playerMenuPage.get(p) - 1) * 36 + i));
                }
                return;
            }
            if(e.getSlot() == 49){
                plugin.playerMenuState.remove(p);

                NumericInputMenu menu = new NumericInputMenu("金額を入力してください", plugin);
                menu.setOnConfirm(number -> {
                    if(number < plugin.getConfig().getInt("minimum_bet")){
                        p.sendMessage(plugin.prefix + "最低ベットは" + BaseUtils.priceString(plugin.getConfig().getInt("minimum_bet")) + "円からです");
                        return;
                    }
                    if(plugin.vault.getBalance(p.getUniqueId()) < (Double.valueOf(number) * (double) plugin.ticket_price)){
                        p.sendMessage(plugin.prefix + "十分な所持金を持っていません");
                        return;
                    }
                    if(plugin.playersInGame.size() > plugin.icons.size()){
                        p.sendMessage(plugin.prefix + "満員です");
                        return;
                    }
                    new Thread(() -> plugin.mysql.execute("insert into jackpot_bet values ('0','" + plugin.gameID + "','" + p.getUniqueId() + "','" + p.getName() + "','" + number + "','" + plugin.ticket_price + "',NOW());")).start();
                    plugin.placeBet(p, Double.valueOf(number));
                });
                menu.setOnCancel(ee -> {
                    Bukkit.getScheduler().runTask(plugin, ()-> {
                        plugin.openMainMenuForPlayer(p);
                    });
                });

                menu.setOnClose(ee -> {
                    Bukkit.getScheduler().runTask(plugin, ()-> {
                        plugin.openMainMenuForPlayer(p);
                    });
                });
                menu.open(p);
                plugin.playerMenuState.put(p,"bet");
            }
            if (e.getSlot() == 48) {
                if (plugin.playerMenuPage.get(p) == 1) {
                    return;
                }
                if (plugin.itemList.size() < 36) {
                    return;
                }
                int r = plugin.playerMenuPage.get(p) - 1;
                plugin.playerMenuPage.put(p, r);
                for (int i = 0; i < 36; i++) {
                    e.getInventory().setItem(i, plugin.itemList.get((plugin.playerMenuPage.get(p) - 1) * 36 + i));
                }

                return;
            }
            return;
        }
    }



    public void changeConfirmPrice(Inventory inv,Player p){
        String[] lore = {"§e§l" + plugin.playerCalcValue.get(p) + "口","§e§l" + String.valueOf(Double.valueOf(plugin.playerCalcValue.get(p)) * Integer.valueOf(plugin.ticket_price)) + "円"};
        ItemMeta buttonMeta = inv.getItem(50).getItemMeta();
        buttonMeta.setLore(Arrays.asList(lore));
        inv.getItem(50).setItemMeta(buttonMeta);
    }

    public void moveD(Inventory inv){
        if(inv.getItem(8) != null){
            plugin.menu.moveDisplay(inv);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e){
        if(plugin.someOneInMenu == false){
            return;
        }
        Player p = (Player) e.getPlayer();
        if(!plugin.playersInMenu.contains(p.getUniqueId())){
            return;
        }
        if(!plugin.playerMenuState.containsKey(p)){
            return;
        }
        plugin.playerCalcValue.remove(p);
        plugin.playersInMenu.remove(p.getUniqueId());
        plugin.playerMenuPage.remove(p);
        if(plugin.playersInMenu.isEmpty()){
            plugin.someOneInMenu = false;
        }
    }
}
