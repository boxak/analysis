package com.javas.analysis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ToString
public class EsResult {
    @Id
    @Nullable
    String _id;

    @Nullable
    String title;

    @Nullable
    String content;

    @Nullable
    String summary;

    @Nullable
    String mediaName;

    @Nullable
    String sid1;

    @Nullable
    String sid2;

    @Nullable
    String class1;

    @Nullable
    String class2;

    @Nullable
    String uri;

    @Nullable
    String rootDomain;

    @Nullable
    String pubDate;

    @Nullable
    String regDate;

    int readCheck;

    String keywords;
}
