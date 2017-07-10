/**
 * Created by KinshukBasu on 27-Jun-17.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Main {



    public static void main(String args[]) throws Exception {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        AccessAuth auth = new AccessAuth();
        twitter = auth.login();

        SearchFunctions search = new SearchFunctions();

        /*
        List<Status> statuses = twitter.getHomeTimeline();
        System.out.println("Showing home timeline.");
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
            System.out.println("----------------------------------");
        }
        */

        search.searchByUsername(twitter);
    }
















}
