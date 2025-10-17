package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private static final String API_URL = "https://dog.ceo/api/breed/%s/list";
    private static final String MESSAGE = "message";
    private static final String STATUS_CODE = "status";
    private static final String SUCCESS_CODE = "success";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        final Request request = new Request.Builder()
                .url(String.format(API_URL, breed))
                .build();

        try {
            final Response response = client.newCall(request).execute();
            assert response.body() != null;
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString(STATUS_CODE).equals(SUCCESS_CODE)) {
                final JSONArray arr = responseBody.getJSONArray(MESSAGE);
                List<String> subbreeds = new ArrayList<>();

                for(int i = 0; i < arr.length(); i++){
                    subbreeds.add(arr.getString(i));
                }
                return subbreeds;
            }
            else {
                throw new BreedNotFoundException(breed);
            }
        }
        catch (IOException | JSONException event) {
            throw new BreedNotFoundException(breed);
        }
    }
}