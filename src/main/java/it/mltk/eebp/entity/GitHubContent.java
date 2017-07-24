package it.mltk.eebp.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubContent {
    private String name;
    private String sha;
    private String type;
    private String path;
    @SerializedName("download_url")
    private String downloadUrl;
}
