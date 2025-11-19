package com.gcgenome.lims.client.usecase.worklist;

import com.gcgenome.lims.client.domain.Batch;
import com.gcgenome.lims.client.domain.PanelEvent;

import java.util.function.Consumer;

public interface UpdateWorklistEvent {
    void subscribe(Consumer<PanelEvent<Batch>> consumer);
}
