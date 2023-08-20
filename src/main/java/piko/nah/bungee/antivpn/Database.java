package piko.nah.bungee.antivpn;

import java.sql.*;
import java.util.HashMap;

public class Database {
    /**
     * Connect to a sample database
     */

    private static String create_table = "CREATE TABLE IF NOT EXISTS avpn_wl (\n"
            + "	user text NOT NULL\n"
            + ");";

    private static Connection connect(String query) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + AntiVPN.db_path + "/AVPN_Whitelist.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();

            stmt.execute(query);
            stmt.close();

            return conn;
        } catch (SQLException e) {
            System.out.println("[AntiVPN][Database][Error]: "+ e.getMessage());
            return null;
        }
    }

    public boolean loadTempWhitelist(){
        String temp_wl = "CREATE TABLE IF NOT EXISTS temp_whitelist (\n"
                + "	user text NOT NULL,\n"
                + " action text NOT NULL"
                + ");";
        Connection conn = null;

        try{
            conn = connect(temp_wl);
            if(conn != null) {
                Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select * from temp_whitelist;");
                int i = 0;
                while (rs.next()) {
                    AntiVPN.offlinePlayers.put(rs.getString("user"), rs.getString("action"));
                    i++;
                }
                rs.close();
                stat.close();
                System.out.println("[AntiVPN][Database][Log]: Loaded "+i+" users from database to HashMap");
                return true;
            }else{
                System.out.println("[AntiVPN][Database][Warning]: Failed to connect to database.");
                return false;
            }

        }catch (SQLException | ClassNotFoundException e){
            System.out.println("[AntiVPN][Database][Error]: "+ e.getMessage());
            return false;
        }finally {
            closeConnection(conn);
        }
    }

    public boolean saveTempWhitelist(HashMap<String, String> hm){
        String temp_wl = "CREATE TABLE IF NOT EXISTS temp_whitelist (\n"
                + "	user text NOT NULL,\n"
                + " action text NOT NULL"
                + ");";
            if (!hm.isEmpty()) {
                Connection conn = null;
                try{
                    conn = connect(temp_wl);

                    if (conn != null) {

                        PreparedStatement prep = conn.prepareStatement("insert into temp_whitelist values (?,?);");

                        for (HashMap.Entry<String, String> entry : hm.entrySet()) {
                            prep.setString(1, entry.getKey());
                            prep.setString(2, entry.getValue());
                            prep.addBatch();
                        }
                        prep.executeBatch();
                        prep.close();

                        return true;
                    } else {
                        return false;
                    }

                } catch (ClassNotFoundException | SQLException e) {System.out.println("[AntiVPN][Database][Error]: "+e.getMessage());return false;}
                finally {
                    closeConnection(conn);
                }

            } else {
                return true;
            }



    }


    public boolean isWhitelisted(String name) {
        Connection conn = null;
        try {
            conn = connect(create_table);
            if (conn != null) {
                String sql = "SELECT * FROM avpn_wl WHERE user=?";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                ResultSet rs = pstmt.executeQuery();

                // loop through the result set
                if (rs.next()) {
                    rs.close();
                    pstmt.close();
                    System.out.println("[AntiVPN][Whitelist][Log]: User is whitelisted: "+name);
                    return true;
                } else {
                    rs.close();
                    pstmt.close();
                    System.out.println("[AntiVPN][Whitelist][Log]: User is not whitelisted: "+name);
                    return false;
                }

            } else {
                System.out.println("[AntiVPN][Database][Error]: Connection is Null");
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[AntiVPN][Database][Error]: "+ e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    private void closeConnection(Connection conn){
        if(conn != null){
            try{
                conn.close();
            }catch (SQLException e){

            }
        }
    }

    public boolean whitelistAdd(String user){
        String sql = "INSERT INTO avpn_wl(user) VALUES(?)";
        Connection conn = null;
        try {
            if(!isWhitelisted(user.toLowerCase())) {
                conn = connect(create_table);
                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, user.toLowerCase());
                pstmt.executeUpdate();
                pstmt.close();
                return true;
            }else{
                return false;
            }
        }catch (SQLException | ClassNotFoundException e){
            System.out.println("Error: "+ e.getMessage());
            return false;
        }finally {
            closeConnection(conn);
        }
    }

    public boolean whitelistRemove(String user){
        String sql = "DELETE FROM avpn_wl WHERE user=?";
        Connection conn = null;
        try {
            if(isWhitelisted(user.toLowerCase())) {
                conn = connect(create_table);
                if(conn != null) {
                    PreparedStatement pstmt = conn.prepareStatement(sql);

                    pstmt.setString(1, user.toLowerCase());
                    pstmt.executeUpdate();
                    pstmt.close();

                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch (SQLException | ClassNotFoundException e){
            System.out.println("[AntiVPN][Database][Error]: "+ e.getMessage());
            return false;
        }finally {
            closeConnection(conn);
        }
    }

}
