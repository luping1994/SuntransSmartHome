package net.suntrans.suntranssmarthome.api;

import okhttp3.MultipartBody;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Looney on 2017/1/4.
 */
import net.suntrans.suntranssmarthome.homepage.device.DeviceInfoResult;
import net.suntrans.suntranssmarthome.homepage.device.switchs.ChannelResult;
import net.suntrans.suntranssmarthome.homepage.device.sensus.SensusResult;
import net.suntrans.suntranssmarthome.homepage.myhome.bean.HomeRoomResult;
import net.suntrans.suntranssmarthome.homepage.myhome.bean.HomeSceneResult;
import net.suntrans.suntranssmarthome.homepage.myhome.add.CreateModelResult;
import net.suntrans.suntranssmarthome.homepage.myhome.add.UpLoadImageMessage;
import net.suntrans.suntranssmarthome.homepage.personal.UserInfo;
import net.suntrans.suntranssmarthome.login.LoginResult;

public interface Api {


    /**
     * 登录api
     *
     * @param grant_type    默认填password
     * @param client_id     默认填6
     * @param client_secret 默认填test
     * @param username      账号
     * @param password      密码
     * @return
     */
    @FormUrlEncoded
    @POST("oauth/login")
    Observable<LoginResult> login(@Field("grant_type") String grant_type,
                                  @Field("client_id") String client_id,
                                  @Field("client_secret") String client_secret,
                                  @Field("username") String username,
                                  @Field("password") String password);

    @POST("user/info")
    Observable<UserInfo> getUserInfo();

    @POST("device/index")
    Observable<DeviceInfoResult> getDevicesInfo();


    @FormUrlEncoded
    @POST("device/slc10")
    Observable<ChannelResult> getSTSLC10Detail(@Field("dev_id") String id);

    @FormUrlEncoded
    @POST("device/slc6")
    Observable<ChannelResult> getSTSLC6Detail(@Field("dev_id") String id);

    @FormUrlEncoded
    @POST("device/sensus")
    Observable<SensusResult> getSensueDetail(@Field("dev_id") String id);

    @POST("home/scene")
    Observable<HomeSceneResult> getHomeScene();

    @POST("home/house")
    Observable<HomeRoomResult> getHomeHouse();

    @POST("home/environment")
    Observable<SensusResult> getHomeEnvironment();

    @FormUrlEncoded
    @POST("house/index")
    Observable<ChannelResult> getModelDetail(@Field("house_id") String id);

    @FormUrlEncoded
    @POST("channel/update ")
    Observable<ChannelResult> upDateChannel(@Field("channel_id") String id,
                                            @Field("img_id") String img_id,
                                            @Field("name") String name);

    @Multipart
    @POST("upload/image")
    Observable<UpLoadImageMessage> upload(
                              @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("scene/add")
    Observable<CreateModelResult> addScene(@Field("name") String name ,
                                     @Field("img_id") String img_id);

    @FormUrlEncoded
    @POST("scene/update")
    Observable<CreateModelResult> updateScene(@Field("name") String name,@Field("scene_id") String scene_id,
                                           @Field("img_id") String img_id);


    @FormUrlEncoded
    @POST("house/update")
    Observable<CreateModelResult> updateRoom(@Field("name") String name, @Field("house_id") String scene_id,
                                              @Field("img_id") String img_id);

    @FormUrlEncoded
    @POST("house/add")
    Observable<CreateModelResult> addHouse(@Field("name") String name,
                                           @Field("img_id") String img_id);
    @FormUrlEncoded
    @POST("house/delete")
    Observable<CreateModelResult> deleteRoom(@Field("house_id") String id);

    @FormUrlEncoded
    @POST("scene/delete")
    Observable<CreateModelResult> deleteScene(@Field("house_id") String id);

    @FormUrlEncoded
    @POST("user/editpassword")
    Observable<CreateModelResult> modifyPass(@Field("oldpassword ") String oldpassword,
                                             @Field("newpassword ") String newpassword,
                                             @Field("repassword  ") String repassword );
}
