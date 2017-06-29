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

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public void login() throws Exception{

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(APIkey, APIsecret);

        Connection DBconnection;
        AccessToken accessToken = null;

        try{
            DBconnection = this.establishConnection();
        } catch (IllegalStateException e){
            System.out.println("Could not reach database");
            return;
        }
        //TODO : Retrieve previous data, if available
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String username = new String();
        String password = new String();

        do {
            System.out.println("Enter username : ");
            username = br.readLine();
            System.out.println("Enter password : ");
            password = br.readLine();
        }while (username.length()>0 && password.length()>0);



        while(null==accessToken){
            accessToken = this.externalLogin(twitter, accessToken);
        }
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

}
