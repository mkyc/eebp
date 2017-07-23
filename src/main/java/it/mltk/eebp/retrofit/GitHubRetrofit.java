package it.mltk.eebp.retrofit;

import it.mltk.eebp.entity.GitHubContent;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GitHubRetrofit {
    @GET("repos/{user}/{repo}/contents/{path}")
    Call<List<GitHubContent>> repoContent(
            @Path("user") String user,
            @Path("repo") String repo,
            @Path("path") String path,
            @Query("clientId") String clientId,
            @Query("clientSecret") String clientSecret);
}
