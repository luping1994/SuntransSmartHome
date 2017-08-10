package net.suntrans.smarthome.api;

import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Looney on 2017/1/4.
 */
import net.suntrans.smarthome.bean.AddSCResult;
import net.suntrans.smarthome.bean.AllDeviceResult;
import net.suntrans.smarthome.bean.AmeterEntity;
import net.suntrans.smarthome.bean.ChannelEdit;
import net.suntrans.smarthome.bean.ChannelResultNewSun;
import net.suntrans.smarthome.bean.DeviceInfoResult;
import net.suntrans.smarthome.bean.ChannelResult;
import net.suntrans.smarthome.bean.Msg;
import net.suntrans.smarthome.bean.RAC;
import net.suntrans.smarthome.bean.SceneChannelResult;
import net.suntrans.smarthome.bean.SensusResult;
import net.suntrans.smarthome.bean.HomeRoomResult;
import net.suntrans.smarthome.bean.HomeSceneResult;
import net.suntrans.smarthome.bean.CreateModelResult;
import net.suntrans.smarthome.bean.UnbindDev;
import net.suntrans.smarthome.bean.UpLoadImageMessage;
import net.suntrans.smarthome.bean.UserInfo;
import net.suntrans.smarthome.bean.VoiceResponse;
import net.suntrans.smarthome.login.LoginResult;

import java.util.List;
import java.util.Map;

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

    @POST("homeV2/environment")
    Observable<SensusResult> getHomeEnvironment();

    @FormUrlEncoded
    @POST("room/channel")
    Observable<ChannelResultNewSun> getRoomChannel(@Field("house_id") String id);

    @FormUrlEncoded
    @POST("channel/update")
    Observable<CreateModelResult> upDateChannel(@Field("channel_id") String id,
                                                @Field("img_id") String img_id,
                                                @Field("name") String name);

    @FormUrlEncoded
    @POST("channel/update")
    Observable<CreateModelResult> upDateChannel(@FieldMap Map<String,String> map);

    @FormUrlEncoded
    @POST("room/deleteChannel")
    Observable<List<AddSCResult>> deleteRoomChannel(@Field("channel_id") String id);

    @Multipart
    @POST("upload/image")
    Observable<UpLoadImageMessage> upload(
            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("scene/add")
    Observable<CreateModelResult> addScene(@Field("name") String name,
                                           @Field("img_id") String img_id);

    @FormUrlEncoded
    @POST("scene/update")
    Observable<CreateModelResult> updateScene(@Field("name") String name, @Field("scene_id") String scene_id,
                                              @Field("img_id") String img_id);

    @FormUrlEncoded
    @POST("scene/update")
    Observable<CreateModelResult> updateScene(@FieldMap Map<String,String> map);


    @FormUrlEncoded
    @POST("house/update")
    Observable<CreateModelResult> updateRoom(@Field("name") String name, @Field("house_id") String scene_id,
                                             @Field("img_id") String img_id);

    @FormUrlEncoded
    @POST("house/update")
    Observable<CreateModelResult> updateRoom(@FieldMap Map<String,String> map);

    @FormUrlEncoded
    @POST("house/add")
    Observable<CreateModelResult> addHouse(@Field("name") String name,
                                           @Field("img_id") String img_id);

    @FormUrlEncoded
    @POST("room/delete")
    Observable<CreateModelResult> deleteRoom(@Field("house_id") String id);

    @FormUrlEncoded
    @POST("scene/delete")
    Observable<CreateModelResult> deleteScene(@Field("house_id") String id);

    @POST("homeV2/userChannel")
    Observable<AllDeviceResult> getAllDevice();

    @FormUrlEncoded
    @POST("user/editpassword")
    Observable<CreateModelResult> modifyPass(@Field("oldpassword ") String oldpassword,
                                             @Field("newpassword ") String newpassword,
                                             @Field("repassword  ") String repassword);
    /*    分割线      */

    @FormUrlEncoded
    @POST("scene/sceneChannel")
    Observable<SceneChannelResult> getSceneChannel(@Field("scene_id") String id);

    @FormUrlEncoded
    @POST("channel/edit")
    Observable<ChannelEdit> getChannelInfo(@Field("channel_id") String id);

    @POST("room/getUnBindChannel")
    Observable<List<UnbindDev>> getUnbindChannel();

    @POST("scene/getChannelByHouse")
    Observable<RAC> getChannelByHouse();

    @FormUrlEncoded
    @POST("room/bindChannel")
    Observable<List<AllDeviceResult.ChannelInfo>> bindChannel2Room(@Field("house_id") String house_id,
                                                                   @Field("channel_id") String channel_id);

    @FormUrlEncoded
    @POST("scene/setChannel")
    Observable<Msg> setChannel(@Field("scene_id") String scene_id,
                               @Field("channel_id") String channel_id,
                               @Field("status") String status);

    @FormUrlEncoded
    @POST("scene/deleteChannel")
    Observable<Msg> deleteSceneChannel(@Field("scene_id") String scene_id,
                                       @Field("channel_id") String channel_id);

    @FormUrlEncoded
    @POST("scene/addChannel")
    Observable<List<AddSCResult>> addSceneChannel(@Field("scene_id") String scene_id,
                                                  @Field("channel_id") String channel_id);

    @FormUrlEncoded
    @POST("scene/voice")
    Observable<VoiceResponse> getSceneByVoice(@Field("name") String name);


    @POST("energy/ammeter")
    Observable<AmeterEntity> getAmmeter();
}
