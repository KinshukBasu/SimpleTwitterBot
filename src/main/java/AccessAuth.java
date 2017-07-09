/**
 * Created by KinshukBasu on 27-Jun-17.
 **/

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class AccessAuth {

    private static final String APIkey = "cNUF64EhJGHTt1qnroyE4XIJ8";
    private static final String APIsecret = "wwKYqUQP9GhSba9QNyLH215656qNTmKAFGhW6pJs8tUEvFDJiX";

    private Connection establishConnection(){
        String url = "jdbc:mysql://localhost:3306/javabase";
        String username = "admin";
        String password = "password";

        System.out.println("Connecting database...");

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    private AccessToken retrieveLoginFromDB() throws Exception {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String username = new String();
        String password = new String();
        String db_password;
        AccessToken accessToken = null;

        try {
            conn = this.establishConnection();

            pstmt = conn.prepareStatement("SELECT * FROM TEMP WHERE USERNAME = ? LIMIT 1");


            do {
                System.out.println("Enter username : ");
                username = br.readLine();
            } while (username.length() <= 0);

            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Username not found in DB");
                return null;
            } else {
                db_password = rs.getString("password");

                for (int i = 0; i < 3; i++) {
                    System.out.println("Enter password : ");
                    password = br.readLine();
                    if (password.equals(db_password)) {
                        System.out.println("Login successful");
                        String token = rs.getString("token");
                        String tokensecret = rs.getString("tokensecret");
                        return new AccessToken(token, tokensecret);

                    } else {
                        System.out.println("Incorrect password");
                    }
                }
                return null;
            }

        } catch (IllegalStateException e) {
            System.out.println("Could not reach database");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("In the finally block!");
            if (rs != null) try {
                rs.close();
            } catch (SQLException logOrIgnore) {
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException logOrIgnore) {
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException logOrIgnore) {
            }
        }

        return null;
    }




    public Twitter login() throws Exception{

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(APIkey, APIsecret);


        AccessToken accessToken = null;

        accessToken = this.retrieveLoginFromDB();

        if(null==accessToken){
            accessToken = this.externalLogin(twitter, accessToken);
            this.storeAccessInDB(accessToken);
        }
        twitter.setOAuthAccessToken(accessToken);
        return twitter;
    }

    private AccessToken externalLogin(Twitter twitter, AccessToken accessToken) throws Exception {

        RequestToken requestToken = twitter.getOAuthRequestToken();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if available) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                    return null;
                } else {
                    te.printStackTrace();
                    return null;
                }
            }
        }

        return(accessToken);
    }

    private void storeAccessInDB(AccessToken accessToken) throws Exception{
        Connection conn = null;
        PreparedStatement pstmt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            conn = this.establishConnection();
            pstmt = conn.prepareStatement("INSERT INTO temp (username, password, token, tokensecret) VALUES (?,?,?,?)");

            System.out.println("Enter password again: ");
            String password = br.readLine();


            pstmt.setString (1, accessToken.getScreenName());
            pstmt.setString(2,password);
            pstmt.setString(3,accessToken.getToken());
            pstmt.setString(4,accessToken.getTokenSecret());

            pstmt.executeUpdate();
        } catch (IllegalStateException e) {
            System.out.println("Could not reach database");
        } finally {

            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException logOrIgnore) {
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException logOrIgnore) {
            }
        }
    }


}
