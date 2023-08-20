package piko.nah.bungee.antivpn;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


class Commands extends Command {
    public Commands() {
        super("vpnw");
    }

    Database db = new Database();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;
            if(p.hasPermission("vpnw.allow")){
                if(args.length == 1){
                    sender.sendMessage(new TextComponent(ChatColor.AQUA + "You need to enter a action and a name.\n"+ChatColor.AQUA+"Example: /vpnw <add/remove> <username>"));
                }else if(args.length > 1){
                    String action = args[0];
                    String username = args[1];

                    try {
                        ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(username);

                        if (pp == null) {
                            if (action.equalsIgnoreCase("add")) {
                                if(!AntiVPN.offlinePlayers.containsKey(username)) {
                                    AntiVPN.offlinePlayers.put(username.toLowerCase(), action.toLowerCase());
                                    sender.sendMessage(new TextComponent(ChatColor.AQUA + "User: " + ChatColor.GRAY + username + ChatColor.AQUA + " is currently offline but will be whitelisted when logging in."));
                                }else{
                                    sender.sendMessage(new TextComponent(ChatColor.AQUA + "User: " + ChatColor.GRAY + username + ChatColor.AQUA + " is already in que to be whitelisted."));
                                }
                                return;
                            }else if(action.equalsIgnoreCase("remove")){
                                if(!AntiVPN.offlinePlayers.containsKey(username)) {
                                    AntiVPN.offlinePlayers.put(username.toLowerCase(), action.toLowerCase());
                                    sender.sendMessage(new TextComponent(ChatColor.AQUA + "User: " + ChatColor.GRAY + username + ChatColor.AQUA + " is currently offline but will be removed from the whitelist when logging in."));
                                }else{
                                    if(AntiVPN.offlinePlayers.get(username).equalsIgnoreCase("add")){
                                        AntiVPN.offlinePlayers.remove(username);
                                        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Removed user: " + ChatColor.GRAY + username));
                                    }else{
                                        sender.sendMessage(new TextComponent(ChatColor.AQUA + "User: " + ChatColor.GRAY + username + ChatColor.AQUA + " is already in que to be removed from the whitelist."));
                                    }
                                }
                                return;
                            }else{
                                sender.sendMessage(new TextComponent(ChatColor.AQUA + "Available actions <add/remove>"));
                                return;
                            }
                        }

                    }catch (Exception e){
                        System.out.println("Player was not found!!! \n"+e.getMessage());
                    }

                    String user_uuid = ProxyServer.getInstance().getPlayer(username).getUniqueId().toString().toLowerCase();

                    if(action.equalsIgnoreCase("add")){
                        if(db.whitelistAdd(user_uuid)){
                            sender.sendMessage(new TextComponent(ChatColor.AQUA + "Added user: " + ChatColor.GRAY + username + " (" + ChatColor.DARK_GRAY + user_uuid + ChatColor.GRAY + ")"));
                        }else{
                            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to add: " + ChatColor.GRAY + username + " (" + ChatColor.DARK_GRAY + user_uuid + ChatColor.GRAY + ")"));
                        }
                    }else if(action.equalsIgnoreCase("remove")){
                        if(db.whitelistRemove(user_uuid)){
                            sender.sendMessage(new TextComponent(ChatColor.AQUA + "Removed user: " + ChatColor.GRAY + username + " (" + ChatColor.DARK_GRAY + user_uuid + ChatColor.GRAY + ")"));
                        }else{
                            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to remove: " + ChatColor.GRAY + username + " (" + ChatColor.DARK_GRAY + user_uuid + ChatColor.GRAY + ")"));
                        }
                    }else{
                        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Available actions <add/remove>"));
                    }
                }else{
                    sender.sendMessage(new TextComponent(ChatColor.AQUA + "You need to enter a action and a name.\n"+ChatColor.AQUA+"Example: /vpnw <add/remove> <username>"));
                }

            }else{
                sender.sendMessage(new TextComponent(ChatColor.RED + "You don't have the rights to execute this command "));
            }

        }else{

        }

    }
}
