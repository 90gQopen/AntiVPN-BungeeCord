package piko.nah.bungee.antivpn;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public final class AntiVPN extends Plugin implements Listener {

    public static String COMMAND;
    public static String CUS;
    public static String CUS_TOKEN;
    public static Integer Timeout;
    public static String kick_message;
    public static boolean custom_command;

    public static HashMap<String, String> offlinePlayers = new HashMap<>();

    public static Path db_path;

    public static String telegram_token;
    public static String telegram_message;
    public static String telegram_chat_id;
    public static boolean telegram_active;

    Database db = new Database();


    public void registerEvents() throws IOException, ClassNotFoundException {
        loadConfig();
        getProxy().getPluginManager().registerListener(this, new Events(this));
        getProxy().getPluginManager().registerCommand(this, new Commands());
    }

    @Override
    public void onEnable() {
        try {
            registerEvents();
            if(db.loadTempWhitelist()){
                System.out.println("[AntiVPN][AntiVPN][Start-up]: Initiated without any errors.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[AntiVPN][AntiVPN][Error]: " + e.getMessage());
            getProxy().getPluginManager().getPlugin(this.getClass().getName()).onDisable();
        }
    }

    @Override
    public void onDisable() {
        if(db.saveTempWhitelist(offlinePlayers)){
            System.out.println("[ANTIVPN][Save Event]: Successfully saved HashMap state.");
        }else{
            System.out.println("[ANTIVPN][Save Event]: Unsuccessfully saved HashMap state or HashMap is empty.");
        }
    }


    public boolean loadDefaultConfig(){
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");
        db_path = getDataFolder().toPath();
        System.out.println("[AntiVPN][DB][Path]: " + db_path + db_path.toString());
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
                return true;
            } catch (IOException e) {
                System.out.println("[AntiVPN][AntiVPN][Error]: " + e.getMessage());
                return false;
            }
        }else{
            return true;
        }
    }

    public void loadConfig() throws IOException, ClassNotFoundException {
        if(loadDefaultConfig()){
            Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            COMMAND = conf.getString("Command");
            CUS = conf.getString("CUS");
            CUS_TOKEN = conf.getString("CUS_TOKEN");
            custom_command = conf.getBoolean("use_custom_command");
            kick_message = conf.getString("message");

            telegram_token = conf.getString("telegram_token");
            telegram_message = conf.getString("telegram_message");
            telegram_chat_id = conf.getString("telegram_chat_id");
            telegram_active = conf.getBoolean("telegram");

        }
    }



}
