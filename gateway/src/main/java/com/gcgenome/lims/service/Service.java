package com.gcgenome.lims.service;

import java.util.List;

public record Service(
    String title,
    String order,
    String prefix,
    List<Page> children
) { }
