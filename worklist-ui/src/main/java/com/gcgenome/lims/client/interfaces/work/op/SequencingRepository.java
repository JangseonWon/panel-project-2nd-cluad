package com.gcgenome.lims.client.interfaces.work.op;

import elemental2.promise.Promise;

public interface SequencingRepository {
    Promise<Void> update(String worklist, Integer index, PatchOperation[] patches);
}
