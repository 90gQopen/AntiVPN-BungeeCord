package piko.nah.bungee.antivpn;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import piko.nah.bungee.antivpn.AntiVPN;
import javax.swing.text.StyledEditorKit;
import java.util.regex.Pattern;


public class Events implements Listener {
    private final AntiVPN antiVPN;
    private Database db = new Database();
    private LookUp lu = new LookUp();

    public Events(AntiVPN antiVPN){
        this.antiVPN = antiVPN;
    }

    public void sendMessage(String message){
        try{
            new Thread(new TelegramMessage(message)).start();
        }catch (Exception e){
            System.out.println("\n-----------------------\n[AntiVPN][Threading][Error]: " + e.getMessage() + "\n-----------------------\n");
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(this.antiVPN, new Runnable() {
            @Override
            public void run() {
                try{
                    ProxiedPlayer p = event.getPlayer();
                    String uuid = p.getUniqueId().toString().toLowerCase();
                    String username = p.getName().toLowerCase();
                    if(AntiVPN.offlinePlayers.containsKey(username)){
                        String value = AntiVPN.offlinePlayers.get(username);
                        if(value.equalsIgnoreCase("add")){
                            if(db.whitelistAdd(uuid)) {
                                AntiVPN.offlinePlayers.remove(username);
                                System.out.println("[AntiVPN][Event][Log]: Added user " + username + " to whitelist, action: " + value);
                                return;
                            }else{
                                AntiVPN.offlinePlayers.remove(username);
                                System.out.println("[AntiVPN][Event][Log]: Failed to add user " + username + " to whitelist, action: " + value);
                            }
                        }else{
                            db.whitelistRemove(uuid);
                            AntiVPN.offlinePlayers.remove(username);
                        }
                    }

                    if(db.isWhitelisted(uuid))
                        return;
                    String address = p.getSocketAddress().toString();
                    String addrs = address.replaceAll(Pattern.quote("\\"),"");
                    String[] a = addrs.split(":");

                    Boolean vpn = lu.fetch(p.getName(), a[0], uuid);
                    System.out.println("[AntiVPN][Lookup][Log] "+username +"["+uuid+"] returned " + vpn);
                    if (vpn) {
                        String cmd = AntiVPN.COMMAND.replaceAll("player", p.getName());
                        if(AntiVPN.custom_command){
                            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
                        }else {
                            p.disconnect(new TextComponent(AntiVPN.kick_message));
                        }

                        if(AntiVPN.telegram_active)
                            sendMessage(AntiVPN.telegram_message.replaceAll("player", p.getName()));

                    } else {

                    }


                }catch (Exception e){
                    System.out.println("\n-----------------------\n[AntiVPN][Event][Error]: " + e.getMessage() + "\n-----------------------\n");
                }
            }
        });

    }

}
