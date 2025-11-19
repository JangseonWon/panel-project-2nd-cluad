package com.gcgenome.lims.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Page(
    String icon,
    @JsonProperty("icon_type")
    String iconType,
    String title,
    String uri,
    String order
) { }
