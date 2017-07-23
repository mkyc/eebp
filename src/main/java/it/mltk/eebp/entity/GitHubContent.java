package it.mltk.eebp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubContent {
    private String name;
    private String sha;
    private String type;
    private String path;
}
