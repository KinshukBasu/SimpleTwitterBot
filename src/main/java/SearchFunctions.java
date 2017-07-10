import twitter4j.*;

import java.util.List;

/**
 * Created by KinshukBasu on 09-Jul-17.
 */
public class SearchFunctions {

    public void searchByUsername(Twitter twitter){

        try {
            Query query = new Query("@Kinshuk_basu");
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }
}
