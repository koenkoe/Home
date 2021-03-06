package nl.koenhabets.home.api;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class RefillFood extends Request<String>{
    private static String url = "https://koenhabets.nl/api/fish/refill";

    private Response.Listener<String> responListener;

    public RefillFood(Response.Listener<String> responseListener,
                Response.ErrorListener errorListener) {

        super(Request.Method.POST, url, errorListener);

        this.responListener = responseListener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }
        return Response.success(data, null);
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
