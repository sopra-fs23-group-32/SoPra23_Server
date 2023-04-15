package ch.uzh.ifi.hase.soprafs23.entity;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class FlickrImageCollector {
    private String API_KEY = "0c15e34e5d1c5ed988c007ab2992ed25";
    private String TAG;

    public FlickrImageCollector(String cityTag) {
        TAG = cityTag;
    }

    public URL getImageUrl() throws URISyntaxException, IOException {
        URL imageUrl = null;
        try {
            // Build Flickr API to request the URL
            URIBuilder uriBuilder = new URIBuilder("https://www.flickr.com/services/rest/");
            uriBuilder.addParameter("method", "flickr.photos.search");
            uriBuilder.addParameter("api_key", this.API_KEY);
            uriBuilder.addParameter("tags", this.TAG);
            uriBuilder.addParameter("format", "json");
            uriBuilder.addParameter("nojsoncallback", "1");
            uriBuilder.addParameter("per_page", "1");
            Random rand = new Random(10);
            int pageIndex = rand.nextInt();
            uriBuilder.addParameter("page", Integer.toString(pageIndex));
    
            // Send the API request and parse the response JSON
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(uriBuilder.build());
            String responseJson = EntityUtils.toString(client.execute(request).getEntity());
            JSONObject response = new JSONObject(responseJson);
            JSONArray images = response.getJSONObject("photos").getJSONArray("photo");
    
            // Get the photo
            JSONObject image = images.getJSONObject(0);
            // Get the
            imageUrl = new URL(String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                    image.getInt("farm"), image.getString("server"), image.getString("id"), image.getString("secret")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUrl;
    }
}
