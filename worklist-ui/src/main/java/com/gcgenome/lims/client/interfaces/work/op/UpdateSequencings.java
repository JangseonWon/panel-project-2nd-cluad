package com.gcgenome.lims.client.interfaces.work.op;

import com.gcgenome.lims.client.interfaces.api.SequencingApi;
import com.gcgenome.lims.client.usecase.work.WorkChanged;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Data;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class UpdateSequencings {
    @Delegate private final BehaviorSubject<Set<WorkPatchSequencingOperation>> targets = behavior(null);
    @Inject UpdateSequencings(WorkChanged changed, SequencingApi api) {
        changed.map(set-> set.stream().map(data->wrap(api, data))
                .filter(ops->ops.patches().length > 0)
                .collect(Collectors.toSet())
        ).subscribe(targets::next);

    }
    private static WorkPatchSequencingOperation wrap(SequencingApi api, Data data) {
        String worklist = data.get("worklist");
        Integer index = Integer.parseInt(data.get("#"));
        return WorkPatchSequencingOperation.builder().api(api).worklist(worklist).index(index).patches(toOps(data)).build();
    }
    private static PatchOperation[] toOps(Data data) {
        List<PatchOperation> ops = new LinkedList<>();
        stringValue(ops, data, "i7 시퀀싱 인덱스", "i7_index_name");
        stringValue(ops, data, "i7 인덱스 시퀀스", "i7_index_sequence");
        stringValue(ops, data, "i5 시퀀싱 인덱스", "i5_index_name");
        stringValue(ops, data, "i5 인덱스 시퀀스", "i5_index_sequence");
        return ops.stream().toArray(PatchOperation[]::new);
    }
    private static void stringValue(List<PatchOperation> ops, Data data, String key, String target) {
        value(ops, data, key, target, Function.identity());
    }
    private static void value(List<PatchOperation> ops, Data data, String key, String target, Function<String, ?> func) {
        String op = findOperation(data, key);
        if(op!=null) ops.add( build(op, target, func.apply(data.get(key))));
    }
    private static String findOperation(Data data, String key) {
        if(!data.isChanged(key)) return null;
        String value = data.get(key);
        if(value==null || value.trim().isEmpty()) return "remove";
        else return "replace";
    }
    private static PatchOperation build(String op, String field, Object value) {
        PatchOperation patch = new PatchOperation();
        patch.op = op;
        patch.path = "/" + field;
        patch.value = value;
        return patch;
    }
}
