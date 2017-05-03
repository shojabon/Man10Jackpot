package red.man10.man10jackpot;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import red.man10.man10jackpot.Man10Jackpot;

/**
 * Created by sho-pc on 2017/04/19.
 */
public class Man10JackpotCommand implements CommandExecutor {

    private final Man10Jackpot plugin;

    public Man10JackpotCommand(Man10Jackpot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;
        if(plugin.needToReturn == true){
            plugin.loadPlayerDataAndPay();
            plugin.needToReturn = false;
        }
        if(plugin.inGame == true){
            p.openInventory(plugin.gameMenu);
            return true;
        }
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("list")){
                p.sendMessage(String.valueOf(plugin.totalBetInt));
                plugin.list(p);

                return true;
            }
            if(args[0].equalsIgnoreCase("cancel")){
                plugin.cancelGame();
                return true;
            }
            if(args[0].equalsIgnoreCase("menu")){
                plugin.menu.setUpGameMenu();
                p.openInventory(plugin.gameMenu);
                return true;
            }
            if(args[0].equalsIgnoreCase("runnable")){
                p.openInventory(plugin.gameMenu);
                plugin.runnable.onSpin();
                return true;
            }
            if(args[0].equalsIgnoreCase("bet")){
                plugin.placeBet(p,100);
                Player player = Bukkit.getPlayer("hashing_bot");
                plugin.placeBet(player,100);
                return true;
            }
            if(args[0].equalsIgnoreCase("listt")){
                plugin.playersInMenu.add(p);
                plugin.someOneInMenu = true;
                plugin.playerMenuPage.put(p,1);
                plugin.playerMenuState.put(p,"dev");
                Inventory inv = Bukkit.createInventory(null,54,"put items");
                p.openInventory(inv);
                return true;
            }
            if(args[0].equalsIgnoreCase("reload")){
                plugin.reloadConfig();
                plugin.tax = plugin.getConfig().getInt("tax_percentage");
                plugin.ticket_price = plugin.getConfig().getInt("ticket_price");
                plugin.timer_time = plugin.getConfig().getInt("timmer");
                plugin.winner_broadcast = plugin.getConfig().getString("winner_broadcast");
                plugin.loser_broadcast = plugin.getConfig().getString("loser_broadcast");
                p.sendMessage(plugin.prefix + "リロードが完了しました");
                return true;
            }
        }
        plugin.playersInMenu.add(p);
        plugin.someOneInMenu = true;
        plugin.playerMenuPage.put(p,1);
        plugin.playerMenuState.put(p,"main");
        plugin.game.openInventory(p,plugin.game.setUpMainInv(p));
        return false;
    }
}
