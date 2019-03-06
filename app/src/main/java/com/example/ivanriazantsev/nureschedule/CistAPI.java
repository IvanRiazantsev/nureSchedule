package com.example.ivanriazantsev.nureschedule;

import events.Events;
import faculties.Faculties;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CistAPI {


    //http://cist.nure.ua/ias/app/tt/P_API_DEPARTMENTS_JSON?p_id_faculty=95
    //2145721

    //http://cist.nure.ua/ias/app/tt/p_api_events_group_json?p_id_group=6949674&idClient=IRtablet


    @GET("P_API_FACULTIES_JSON")
    Call<Faculties> getFaculties();

    @GET("p_api_events_group_json")
    Call<Events> getEventsForGroup(@Query("p_id_group") Integer groupID, @Query("time_from") Long timeFrom, @Query("time_to") Long timeTo, @Query("idClient") String key);



}
