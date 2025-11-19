package com.gcgenome.lims.client.interfaces.work.op;

import com.gcgenome.lims.client.interfaces.api.SequencingApi;
import elemental2.promise.Promise;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent=true)
public class WorkPatchSequencingOperation implements Operation {
    private SequencingApi api;
    private String worklist;
    private Integer index;
    private PatchOperation[] patches;

    @Override
    public Promise<Void> update() {
        return api.update(worklist, index, patches);
    }
}
