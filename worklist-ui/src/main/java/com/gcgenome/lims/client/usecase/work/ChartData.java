package com.gcgenome.lims.client.usecase.work;

import com.gcgenome.lims.client.domain.*;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Data;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class ChartData {
    @Delegate private final BehaviorSubject<Data[]> data = behavior(new Data[0]);
    @Inject ChartData(WorkList worklists, WorkChanged changed) {
        worklists.map(work-> convertWorksToData(work, changed)).subscribe(data::next);
    }
    private Data[] convertWorksToData(Work[] works, WorkChanged changed) {
        return Arrays.stream(works)
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
    private void setupChangeListener(Data data, Work work, WorkChanged changed) {
        data.onValueChange(evt -> {
            Data updatedData = evt.value();
            updateWorkFromData(updatedData, work);
            var changedWorkSet = changed.getValue();
            if (updatedData.isChanged()) changedWorkSet.add(updatedData);
            else changedWorkSet.remove(updatedData);
            changed.next(changedWorkSet);
        });
    }
    private static void updateWorkFromData(Data data, Work work) {
        if (data.isChanged("i7 시퀀싱 인덱스")) work.i7indexName(data.get("i7 시퀀싱 인덱스"));
        if (data.isChanged("i5 시퀀싱 인덱스")) work.i5indexName(data.get("i5 시퀀싱 인덱스"));
        if (data.isChanged("i7 인덱스 시퀀스")) work.i7IndexSequence(data.get("i7 인덱스 시퀀스"));
        if (data.isChanged("i5 인덱스 시퀀스")) work.i5IndexSequence(data.get("i5 인덱스 시퀀스"));
    }
    private void syncPreviousChanges(Data data, Work work, WorkChanged changed) {
        if(data==null) return;
        String id = work.worklist() + ":" + work.index();
        var previouslyChanged = changed.getValue().stream()
                .filter(d->d.idx().equals(id))
                .findFirst().orElse(null);
        if (previouslyChanged == null) return; // 이전 변경 이력이 없으면 처리할 필요가 없음

        // 기존 변경 이력을 제거하고 이벤트를 발행
        var changedWorklistSet = changed.getValue();
        changedWorklistSet.remove(previouslyChanged);
        changed.next(changedWorklistSet);

        // 이전 값들을 현재 데이터에 적용
        applyPreviousChanges(data, previouslyChanged);
    }
    private void applyPreviousChanges(Data data, Data previouslyChanged) {
        updateIfDifferent(data, previouslyChanged, "i7 시퀀싱 인덱스");
        updateIfDifferent(data, previouslyChanged, "i5 시퀀싱 인덱스");
        updateIfDifferent(data, previouslyChanged, "i7 인덱스 시퀀스");
        updateIfDifferent(data, previouslyChanged, "i5 인덱스 시퀀스");
    }

    private void updateIfDifferent(Data data, Data prev, String key) {
        String value = prev.get(key);
        if (value != null && !value.equals(data.get(key))) data.put(key, value);
    }

    private static Data toData(Work work) {
        if(work==null) return null;
        var data = Data.create(work.worklist() + ":" + work.index())
                .put("#", String.valueOf(work.index()))
                .put("worklist", work.worklist())
                .put("id", work.id())
                .put("Serial", work.serial()!=null?work.serial():"생성")
                .put("serial", work.serial())
                .put("prefix", work.prefix())
                .put("suffix", work.idx()!=null? work.idx().toString() : null)
                .put("Type", work.type())
                .put("STB", work.gid())
                .put("Position", work.position())
                .put("시퀀싱 파일명", work.sequencingFileName())
                .put("i7 시퀀싱 인덱스", work.i7indexName())
                .put("i5 시퀀싱 인덱스", work.i5indexName())
                .put("i7 인덱스 시퀀스", work.i7IndexSequence())
                .put("i5 인덱스 시퀀스", work.i5IndexSequence());
        map(data, work.requests());
        return data;
    }
    private static void map(Data data, Request[] requests) {
        if(requests==null) return;
        var map = map(requests);
        for(var key: map.keySet()) data.put(key, map.get(key));
        map(data, Arrays.stream(requests).map(Request::sample).toArray(Sample[]::new));
        map(data, Arrays.stream(requests).map(Request::service).toArray(Service[]::new));
    }
    private static Map<String, String> map(Request[] requests) {
        var map = new HashMap<String, Set<String>>();
        if(requests!=null) for(var request: requests) {
            String dateRequest = request.dateRequest();
            String dateReception = request.dateReception();
            String dateDuePublish = request.dateDuePublish();
            if(dateRequest!=null) map.computeIfAbsent("의뢰일", k->new LinkedHashSet<>()).add(request.id());
            if(dateReception!=null) map.computeIfAbsent("접수일", k->new LinkedHashSet<>()).add(request.id());
            if(dateDuePublish!=null) map.computeIfAbsent("지놈예정일", k->new LinkedHashSet<>()).add(request.id());
        }
        var result = new HashMap<String, String>();
        for(var entry : map.entrySet()) result.put(entry.getKey(), entry.getValue().stream().collect(Collectors.joining(", ")));
        return result;
    }
    private static void map(Data data, Sample[] samples) {
        if(samples == null) return;
        var map = map(samples);
        for(var key: map.keySet()) data.put(key, map.get(key));
        map(data, Arrays.stream(samples).map(Sample::patient).toArray(Patient[]::new));
    }
    private static Map<String, String> map(Sample[] samples) {
        var map = new HashMap<String, Set<String>>();
        if(samples != null) for(var sample: samples) {
            String id = sample.id() != null ? String.valueOf(sample.id()) : "";
            String formattedId = formatSampleId(String.valueOf(sample.id()));
            String type = sample.type();
            String age = sample.age()!=null?String.valueOf(sample.age()):null;
            String remark = sample.remark();

            if(id != null && !id.isEmpty()) map.computeIfAbsent("sample", k -> new LinkedHashSet<>()).add(id);
            if(formattedId != null && !formattedId.isEmpty()) map.computeIfAbsent("의뢰번호", k -> new LinkedHashSet<>()).add(formattedId);
            if(type != null) map.computeIfAbsent("검체종류", k -> new LinkedHashSet<>()).add(type);
            if(age != null) map.computeIfAbsent("나이", k -> new LinkedHashSet<>()).add(age);
            if(remark != null) map.computeIfAbsent("비고", k -> new LinkedHashSet<>()).add(remark);
        }
        var result = new HashMap<String, String>();
        for(var entry : map.entrySet()) result.put(entry.getKey(), entry.getValue().stream().collect(Collectors.joining(", ")));
        return result;
    }
    private static void map(Data data, Service[] services) {
        if(services == null) return;
        var map = map(services);
        for(var key: map.keySet()) data.put(key, map.get(key));
    }

    private static Map<String, String> map(Service[] services) {
        var map = new HashMap<String, Set<String>>();
        if(services != null) for(var service: services) {
            String name = service.name();
            String id = service.id();
            if(name != null) map.computeIfAbsent("검사명", k -> new LinkedHashSet<>()).add(name);
            if(id != null) map.computeIfAbsent("service", k -> new LinkedHashSet<>()).add(id);
        }
        var result = new HashMap<String, String>();
        for(var entry : map.entrySet()) result.put(entry.getKey(), entry.getValue().stream().collect(Collectors.joining(", ")));
        return result;
    }
    private static void map(Data data, Patient[] patients) {
        if(patients == null) return;
        var map = map(patients);
        for(var key: map.keySet()) data.put(key, map.get(key));
        map(data, Arrays.stream(patients).map(Patient::organization).toArray(Organization[]::new));
    }

    private static Map<String, String> map(Patient[] patients) {
        var map = new HashMap<String, Set<String>>();
        if(patients != null) for(var patient: patients) {
            String mrn = patient.mrn();
            String name = patient.name();
            String code = patient.code();
            String sex = patient.sex();

            if(mrn != null) map.computeIfAbsent("MRN", k -> new LinkedHashSet<>()).add(mrn);
            if(name != null) map.computeIfAbsent("수진자명", k -> new LinkedHashSet<>()).add(name);
            if(code != null) map.computeIfAbsent("수진자코드", k -> new LinkedHashSet<>()).add(code);
            if(sex != null) map.computeIfAbsent("성별", k -> new LinkedHashSet<>()).add(sex);
        }
        var result = new HashMap<String, String>();
        for(var entry : map.entrySet()) result.put(entry.getKey(), entry.getValue().stream().collect(Collectors.joining(", ")));
        return result;
    }

    private static void map(Data data, Organization[] organizations) {
        if(organizations == null) return;
        var map = map(organizations);
        for(var key: map.keySet()) data.put(key, map.get(key));
    }

    private static Map<String, String> map(Organization[] organizations) {
        var map = new HashMap<String, Set<String>>();
        if(organizations != null) for(var organization: organizations) {
            String name = organization.name();
            if(name != null) map.computeIfAbsent("의뢰기관", k -> new LinkedHashSet<>()).add(name);
        }
        var result = new HashMap<String, String>();
        for(var entry : map.entrySet()) result.put(entry.getKey(), entry.getValue().stream().collect(Collectors.joining(", ")));
        return result;
    }

    public static String formatSampleId(String id) {
        if(id == null) return null;
        if(id.length() >= 15) return id.substring(0, 8) + "-" + id.substring(8, 11) + "-" + id.substring(11);
        else return id;
    }
}
