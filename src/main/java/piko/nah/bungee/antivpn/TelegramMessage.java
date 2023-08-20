package piko.nah.bungee.antivpn;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class TelegramMessage implements Runnable{

    private static String msg;
    private static String token = AntiVPN.telegram_token;
    private static String chat_id = AntiVPN.telegram_chat_id;

    public TelegramMessage(String message){
        msg = message;
    }

    public static void sendToTelegram() {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

        urlString = String.format(urlString, token, chat_id, msg);

        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            StringBuilder sb = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String response = sb.toString();

        } catch (IOException e) {
            System.out.println("\n-----------------------\n[AntiVPN][Telegram][Error]: " + e.getMessage() + "\n-----------------------\n");
        }
    }

    @Override
    public void run() {
        sendToTelegram();
    }
}
