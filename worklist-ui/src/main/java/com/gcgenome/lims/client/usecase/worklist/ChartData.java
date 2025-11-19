package com.gcgenome.lims.client.usecase.worklist;

import com.gcgenome.lims.client.domain.Worklist;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Data;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Arrays;
import java.util.Objects;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class ChartData {
    private static final String KEY_SERIAL = "serial";
    private static final String KEY_PREFIX = "prefix";
    private static final String KEY_SUFFIX = "suffix";
    private static final String KEY_NOTE = "비고";

    @Delegate private final BehaviorSubject<Data[]> data = behavior(new Data[0]);
    @Inject ChartData(WorklistList worklists, WorklistChanged changed) {
        worklists.map(worklist-> convertWorklistsToData(worklist, changed)).subscribe(data::next);
    }
    private Data[] convertWorklistsToData(Worklist[] worklists, WorklistChanged changed) {
        return Arrays.stream(worklists)
                .map(worklist->{
                    var data = toData(worklist);
                    // 데이터가 변경되면 변경된 워크 목록에 추가한다
                    if(data!=null) {
                        setupChangeListener(data, worklist, changed);
                        syncPreviousChanges(data, worklist, changed);
                    }
                    return data;
                }).toArray(Data[]::new);
    }
    private void setupChangeListener(Data data, Worklist worklist, WorklistChanged changed) {
        data.onValueChange(evt -> {
            Data updatedData = evt.value();
            updateWorklistFromData(updatedData, worklist);
            var changedWorklistSet = changed.getValue();
            if (updatedData.isChanged()) changedWorklistSet.add(worklist);
            else changedWorklistSet.remove(worklist);
            changed.next(changedWorklistSet);
        });
    }

    private void syncPreviousChanges(Data data, Worklist worklist, WorklistChanged changed) {
        if(data==null) return;
        Worklist previouslyChanged = changed.getValue().stream().filter(w->w.id().equals(worklist.id())).findFirst().orElse(null);
        if (previouslyChanged == null) return; // 이전 변경 이력이 없으면 처리할 필요가 없음

        // 기존 변경 이력을 제거하고 이벤트를 발행
        var changedWorklistSet = changed.getValue();
        changedWorklistSet.remove(previouslyChanged);
        changed.next(changedWorklistSet);

        // 이전 값들을 현재 데이터에 적용
        applyPreviousChanges(data, previouslyChanged);
    }
    private void applyPreviousChanges(Data data, Worklist previouslyChanged) {
        updateIfDifferent(data, KEY_SERIAL, previouslyChanged.serial());
        updateIfDifferent(data, KEY_PREFIX, previouslyChanged.prefix());
        updateIfDifferent(data, KEY_SUFFIX, previouslyChanged.idx() != null ? previouslyChanged.idx().toString() : null);
        updateIfDifferent(data, KEY_NOTE, previouslyChanged.note());
    }

    private void updateIfDifferent(Data data, String key, String value) {
        if (value != null && !value.equals(data.get(key))) data.put(key, value);
    }

    private static Data toData(Worklist worklist) {
        if(worklist==null) return null;
        String serialDisplay = worklist.serial()!=null ? worklist.serial() : "생성";
        String suffixValue = worklist.idx() != null ? worklist.idx().toString() : null;
        return Data.create(worklist.id())
                .put("Serial", serialDisplay)
                .put(KEY_SERIAL, worklist.serial())
                .put("Title", worklist.title())
                .put("생성일", worklist.createAt())
                .put("Created by", worklist.createBy())
                .put("검체", String.valueOf(worklist.sampleCount()))
                .put("Status", worklist.status())
                .put("Remark", worklist.remark())
                .put(KEY_NOTE, worklist.note())
                .put(KEY_PREFIX, worklist.prefix())
                .put(KEY_SUFFIX, suffixValue);
    }
    private static void updateWorklistFromData(Data data, Worklist worklist) {
        if (data.isChanged(KEY_NOTE)) worklist.note(data.get(KEY_NOTE));
    }
}
