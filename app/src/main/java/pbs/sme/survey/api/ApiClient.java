package pbs.sme.survey.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static String base_url="http://iacapi.pbos.gov.pk/";
    private static Retrofit retrofit;

    public static Retrofit getApiClient(){

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.writeTimeout(60, TimeUnit.SECONDS);
        client.readTimeout(60,TimeUnit.SECONDS);
        client.connectTimeout(60,TimeUnit.SECONDS);
        //client.addInterceptor(new ApiInterceptor());

        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}
