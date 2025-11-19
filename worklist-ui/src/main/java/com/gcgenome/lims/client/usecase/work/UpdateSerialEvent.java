package com.gcgenome.lims.client.usecase.work;

import com.gcgenome.lims.client.domain.PanelEvent;
import com.gcgenome.lims.client.domain.Serial;

import java.util.function.Consumer;

public interface UpdateSerialEvent {
    void subscribe(Consumer<PanelEvent<Serial>> consumer);
}
