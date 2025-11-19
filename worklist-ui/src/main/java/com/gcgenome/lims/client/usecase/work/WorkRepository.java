package com.gcgenome.lims.client.usecase.work;

import com.gcgenome.lims.client.domain.Work;
import dev.sayaya.rx.Observable;

public interface WorkRepository {
    Observable<Work[]> findByWorklist(String worklistId);
}
