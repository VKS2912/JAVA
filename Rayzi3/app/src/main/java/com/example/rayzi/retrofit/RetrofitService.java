package com.example.rayzi.retrofit;

import com.example.rayzi.audioLive.AudioLiveStreamRoot;
import com.example.rayzi.audioLive.ThemeRoot;
import com.example.rayzi.modelclass.AdsRoot;
import com.example.rayzi.modelclass.BannerRoot;
import com.example.rayzi.modelclass.CallRequestRoot;
import com.example.rayzi.modelclass.ChatListRoot;
import com.example.rayzi.modelclass.ChatTopicRoot;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.modelclass.CoinRecordRoot;
import com.example.rayzi.modelclass.ComplainRoot;
import com.example.rayzi.modelclass.DiamondPlanRoot;
import com.example.rayzi.modelclass.FollowUnfollowResponse;
import com.example.rayzi.modelclass.GiftCategoryRoot;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.GuestUsersListRoot;
import com.example.rayzi.modelclass.HeshtagsRoot;
import com.example.rayzi.modelclass.HistoryListRoot;
import com.example.rayzi.modelclass.IpAddressRoot_e;
import com.example.rayzi.modelclass.LevelRoot;
import com.example.rayzi.modelclass.LiveStreamRoot;
import com.example.rayzi.modelclass.LiveSummaryRoot;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.modelclass.PostCommentRoot;
import com.example.rayzi.modelclass.PostRoot;
import com.example.rayzi.modelclass.ReedemListRoot;
import com.example.rayzi.modelclass.ReliteRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.SearchLocationRoot;
import com.example.rayzi.modelclass.SettingRoot;
import com.example.rayzi.modelclass.SongRoot;
import com.example.rayzi.modelclass.StickerRoot;
import com.example.rayzi.modelclass.StripePaymentRoot2_e;
import com.example.rayzi.modelclass.UploadImageRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.modelclass.VipPlanRoot;
import com.example.rayzi.models.CoinRoot;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("json")
    Call<IpAddressRoot_e> getIp();

    @GET("setting")
    Call<SettingRoot> getSettings();

    @POST("/loginSignup")
    Call<UserRoot> createUser(@Body JsonObject jsonObject);

    @GET("/user/profile")
    Call<UserRoot> getUser(@Query("userId") String type);

    @POST("/income/seeAd")
    Call<UserRoot> addDiamondFromAds(@Body JsonObject jsonObject);

    @POST("/getUser")
    Call<GuestProfileRoot> getGuestUser(@Body JsonObject jsonObject);

    @Multipart
    @POST("/user/update")
    Call<UserRoot> updateUser(@PartMap Map<String, RequestBody> partMap,
                              @Part MultipartBody.Part requestBody);


    @POST("/follow")
    Call<RestResponse> followUser(@Body JsonObject jsonObject);

    @POST("/unFollow")
    Call<RestResponse> unFollowUser(@Body JsonObject jsonObject);

    @POST("/followUnfollow")
    Call<FollowUnfollowResponse> toggleFollowUnfollow(@Body JsonObject jsonObject);

    @GET("/banner")
    Call<BannerRoot> getBanner(@Query("type") String type);

    @POST("/checkUsername")
    Call<RestResponse> checkUserName(@Query("username") String username, @Query("userId") String userId);

    @POST("/followerList")
    Call<GuestUsersListRoot> getFollowrsList(@Body JsonObject jsonObject);

    @POST("/followingList")
    Call<GuestUsersListRoot> getFollowingList(@Body JsonObject jsonObject);


    @POST("/user/search")
    Call<GuestUsersListRoot> searchUser(@Body JsonObject jsonObject);

    @GET("/coinPlan")
    Call<DiamondPlanRoot> getDiamondsPlan();


    @GET("/vipPlan")
    Call<VipPlanRoot> getVipPlan();


    @GET("/location/search")
    Call<SearchLocationRoot> searchLocation(@Query("value") String keyword);

    @GET("/hashtag/search")
    Call<HeshtagsRoot> searchHashtag(@Query("value") String keyword);

    @Multipart
    @POST("/uploadPost")
    Call<RestResponse> uploadPost(@PartMap Map<String, RequestBody> partMap,
                                  @Part MultipartBody.Part requestBody
    );

    @Multipart
    @POST("/uploadRelite")
    Call<RestResponse> uploadRelite(@PartMap Map<String, RequestBody> partMap,
                                    @Part MultipartBody.Part requestBody1,
                                    @Part MultipartBody.Part requestBody2,
                                    @Part MultipartBody.Part requestBody3
    );

    @GET("/song")
    Call<SongRoot> getSongs();

    @GET("/getPopularLatestPost")
    Call<PostRoot> getPostList(@Query("userId") String uId, @Query("type") String type,
                               @Query("start") int start, @Query("limit") int limit);

    @GET("/user/post")
    Call<PostRoot> getUserPostList(@Query("userId") String uId,
                                   @Query("start") int start, @Query("limit") int limit);

    @GET("/getFollowingPost")
    Call<PostRoot> getFollowingPost(@Query("userId") String uId, @Query("type") String type,
                                    @Query("start") int start, @Query("limit") int limit);


    @GET("/getRelite")
    Call<ReliteRoot> getRelites(@Query("userId") String uId, @Query("type") String type,
                                @Query("start") int start, @Query("limit") int limit);


    @POST("/likeUnlike")
    Call<RestResponse> toggleLikePost(@Body JsonObject jsonObject);

    @GET("/comment")
    Call<PostCommentRoot> getPostCommentList(@Query("userId") String uId, @Query("postId") String postId,
                                             @Query("start") int start, @Query("limit") int limit);

    @GET("/likes")
    Call<PostCommentRoot> getPostLikeList(@Query("userId") String uId, @Query("postId") String postId,
                                          @Query("start") int start, @Query("limit") int limit);

    @GET("/comment")
    Call<PostCommentRoot> getReliteCommentList(@Query("userId") String uId, @Query("videoId") String postId,
                                               @Query("start") int start, @Query("limit") int limit);

    @GET("/likes")
    Call<PostCommentRoot> getReliteLikeList(@Query("userId") String uId, @Query("videoId") String postId,
                                            @Query("start") int start, @Query("limit") int limit);

    @POST("/comment")
    Call<RestResponse> addComment(@Body JsonObject jsonObject);


    @POST("/user/live")
    Call<LiveStreamRoot> makeliveUser(@Body JsonObject jsonObject);

    @POST("/user/live")
    Call<AudioLiveStreamRoot> makeAudioLiveUser(@Body JsonObject jsonObject);

    @GET("/liveUser")
    Call<LiveUserRoot> getLiveUsersList(@Query("userId") String uId, @Query("type") String type,  @Query("start") int start, @Query("limit") int limit, @Query("isFake") String isFake);

    @GET("/getStreamingSummary")
    Call<LiveSummaryRoot> getLiveSummary(@Query("liveStreamingId") String liveStreamingId);


    @Multipart
    @POST("/complain")
    Call<RestResponse> addSupport(
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part requestBody);

    @GET("/complain/userList")
    Call<ComplainRoot> getComplains(@Query("userId") String userid);


    @POST("/redeem")
    Call<RestResponse> cashOutDiamonds(@Body JsonObject jsonObject);


    @POST("/convertRcoinToDiamond")
    Call<UserRoot> convertRcoinToDiamond(@Body JsonObject jsonObject);

    @GET("/redeem/user")
    Call<ReedemListRoot> getReedemHistotry(@Query("userId") String userid);

    @POST("/addReferralCode")
    Call<UserRoot> reedemReferalCode(@Body JsonObject jsonObject);

    @GET("/giftCategory")
    Call<GiftCategoryRoot> getGiftCategory();

    @GET("/gift/{cId}")
    Call<GiftRoot> getGiftsByCategory(@Path("cId") String categoryId);

    @POST("/coinPlan/purchase/googlePlay")
    Call<UserRoot> callPurchaseApiGooglePayDiamond(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/googlePlay")
    Call<UserRoot> callPurchaseApiGooglePayVip(@Body JsonObject jsonObject);


    @POST("/coinPlan/purchase/stripe")
    Call<StripePaymentRoot2_e> setStripeDiamonds(@Body JsonObject jsonObject);

    @POST("/coinPlan/purchase/stripe")
    Call<UserRoot> purchsePlanStripeDiamons(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/stripe")
    Call<StripePaymentRoot2_e> setStripeVip(@Body JsonObject jsonObject);

    @POST("/vipPlan/purchase/stripe")
    Call<UserRoot> purchsePlanStripeVip(@Body JsonObject jsonObject);


    @GET("/diamondRcoinTotal")
    Call<CoinRecordRoot> getCoinRecord(@Query("userId") String userId,
                                       @Query("startDate") String startDate,
                                       @Query("endDate") String endDate);


    @GET("/diamondRcoinHistory")
    Call<HistoryListRoot> getCoinHostory(@Query("userId") String userId,
                                         @Query("startDate") String startDate,
                                         @Query("endDate") String endDate,
                                         @Query("type") String type,
                                         @Query("start") int start, @Query("limit") int limit);

    @POST("/createRoom")
    Call<ChatTopicRoot> createChatRoom(@Body JsonObject jsonObject);

    @GET("/getOldChat")
    Call<ChatListRoot> getOldChats(@Query("topicId") String chatRoomId,
                                   @Query("start") int start, @Query("limit") int limit);

    @GET("/chatList")
    Call<ChatUserListRoot> getChatUserList(@Query("userId") String userId,
                                           @Query("start") int start, @Query("limit") int limit);

    @Multipart
    @POST("/uploadImage")
    Call<UploadImageRoot> uploadChatImage(
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part requestBody);

    @DELETE("/deleteMessage")
    Call<RestResponse> deleteChat(@Query("chatId") String chatId);

    @DELETE("/comment")
    Call<RestResponse> deleteComment(@Query("commentId") String chatId);

    @POST("/user/online")
    Call<RestResponse> makeOnlineUser(@Body JsonObject jsonObject);

    @POST("/call")
    Call<CallRequestRoot> makeCallRequest(@Body JsonObject jsonObject);

    @POST("/user/notification")
    Call<UserRoot> changeUserNotificationSetting(@Body JsonObject jsonObject);

    @GET("/user/random")
    Call<GuestProfileRoot> getRandomUser(@Query("userId") String userId);

    @POST("/report")
    Call<RestResponse> reportThisUser(@Body JsonObject jsonObject);

    @GET("/sticker")
    Call<StickerRoot> getStickers();

    @GET("/level")
    Call<LevelRoot> getLevels();

    @GET("/advertisement")
    Call<AdsRoot> getAds();

    @DELETE("/deletePost")
    Call<RestResponse> deletePost(@Query("postId") String postId);

    @DELETE("/deleteRelite")
    Call<RestResponse> deleteRelite(@Query("videoId") String videoId);

    @POST("/sendGiftFakeHost")
    Call<CoinRoot> getCoin(@Body JsonObject jsonObject);

    @GET("/theme")
    Call<ThemeRoot> getTheme();

}
