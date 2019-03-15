package com.example.ivanriazantsev.nureschedule;

import api.Main;
import events.Events;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface CistAPI {


    @GET("p_api_events_group_json")
    Call<Events> getEventsForGroup(@Query("p_id_group") Integer groupID, @Query("time_from") Long timeFrom, @Query("time_to") Long timeTo, @Query("idClient") String key);

    @GET("p_api_group_json")
    Call<Main> getGroupsList();

    @GET("p_api_podr_json")
    Call<Main> getTeachersList();


}
