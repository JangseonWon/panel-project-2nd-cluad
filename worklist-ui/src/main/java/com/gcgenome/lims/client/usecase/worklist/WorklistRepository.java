package com.gcgenome.lims.client.usecase.worklist;

import com.gcgenome.lims.client.api.Page;
import com.gcgenome.lims.client.domain.Search;
import com.gcgenome.lims.client.domain.Worklist;
import dev.sayaya.rx.Observable;

public interface WorklistRepository {
    Observable<Page<Worklist>> search(Search param);
}
