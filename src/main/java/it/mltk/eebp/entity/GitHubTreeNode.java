package it.mltk.eebp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubTreeNode {
    private String path;
    private String type;
    private String sha;
}
