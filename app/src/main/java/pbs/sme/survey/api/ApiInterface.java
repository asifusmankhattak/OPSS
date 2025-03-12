package pbs.sme.survey.api;

import java.util.List;

import pbs.sme.survey.model.Forgot;
import pbs.sme.survey.model.Result;
import pbs.sme.survey.model.User;
import pbs.sme.survey.online.Returning;
import pbs.sme.survey.online.Sync;
import pbs.sme.survey.model.Import;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @GET("iac/test")
    Call<Result> apiResponse();

    @POST("iac/authenticate?")
    @FormUrlEncoded
    Call<List<User>> authenticate(@Field("uname") String uname, @Field("pass") String pass,
                  @Field("deviceID") String deviceID, @Field("deviceName") String deviceName,
                  @Field("mac") String mac, @Field("os") String os, @Field("referer") String referer,
                  @Field("lat") double lat, @Field("lon") double lon);

    @POST("iac/forget_password?")
    @FormUrlEncoded
    Call<Forgot> forgot(@Field("app") int app,  @Field("username") String username);


    @POST("iac/change_password?")
    @FormUrlEncoded
    Call<Result> change(@Field("app") int app,  @Field("username") String username, @Field("password") String password);


    @POST("iac/fetch_listing?")
    @FormUrlEncoded
    Call<Import> fetch(@Field("app") int app, @Field("vcode") String vcode, @Field("uid") int uid, @Field("sid") long sid, @Field("mid") int mid, @Field("utime") String utime, @Field("ctime") String ctime, @Field("cnt") long cnt);


    @POST("iac/sync_listing2?")
    Call<Returning> upload(@Body Sync data);
}
