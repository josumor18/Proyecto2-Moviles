package moviles.apps.proyecto2.friendtec.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetBitmap extends AsyncTask<String, Void, Bitmap> {
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected Bitmap doInBackground(String... strings){

        Bitmap cover;

        String address = strings[0];//Usuario_Singleton.getInstance().getUrl_foto();
        //String addressr = Usuario_Singleton.getInstance().getUrl_foto_rounded();

        try {

            //Create a URL object holding our url
            URL myUrl = new URL(address);

            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.setDoInput(true);
            connection.connect();

            //Create a new InputStream
            InputStream input = connection.getInputStream();
            cover = BitmapFactory.decodeStream(input);

        }
        catch(IOException e){
            e.printStackTrace();
            cover = null;
        }
        return cover;
    }
}