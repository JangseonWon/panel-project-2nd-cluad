package com.gcgenome.lims.client.expand.mrd;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.SampleApi;
import com.gcgenome.lims.dto.InterpretationMrd;
import com.gcgenome.lims.dto.InterpretationMrdScreen;
import com.gcgenome.lims.dto.Sample;
import com.google.gwt.i18n.client.DateTimeFormat;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.ListOf;
import net.sayaya.rx.subject.Subject;

import java.util.*;
import java.util.stream.Collectors;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.rx.subject.Subject.subject;

public class Subjects {
    public final static Subject<String> cancerType = subject(String.class);
    public final static BehaviorSubject<ListOf<InterpretationMrd.MrdHistory>> histories = (BehaviorSubject) behavior(ListOf.class);
    static {
        com.gcgenome.lims.client.Subjects.interpretation.subscribe(interpretation -> {
            try {
                InterpretationMrd cast = Js.cast(interpretation);
                cancerType.next(cast.cancerType);
                {
                    var listOf = new ListOf<InterpretationMrd.MrdHistory>();
                    var histories = cast.histories;
                    if(histories!=null && histories.length > 0) listOf.replaceTo(Arrays.stream(cast.histories).collect(Collectors.toList()));
                    listOf.onValueChange(evt -> Subjects.histories.next(listOf));
                    Subjects.histories.next(listOf);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });
        cancerType.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationMrd());
            InterpretationMrd cast = Js.cast(_interpretation);
            cast.cancerType = evt;
        });
        histories.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationMrd());
            InterpretationMrd cast = Js.cast(_interpretation);
            cast.histories = evt.value().stream().toArray(InterpretationMrd.MrdHistory[]::new);
        });
    }
    public static void findInterpretation(long sample, JsPropertyMap<?> service) {
        SampleApi.siblings(sample).then(siblings->{
            findCurrentMrd(sample, siblings, service).then(interpretation-> {
                com.gcgenome.lims.client.Subjects.interpretation.next(interpretation);
                return null;
            });
            return null;
        });
    }
    private static Promise<InterpretationMrd> findCurrentMrd(long sample, Sample[] siblings, JsPropertyMap<?> service) {
        // 가장 최근 MRD 검사를 불러온다.
        // i) 검사 결과가 존재한다.
        //   i-1) 불러온 검사의 검체와 현재 검체 번호가 작거나 같다.
        //   i-2) 현재 검체 번호가 크다(신규 검사 결과)
        // ii) 검사 결과가 존재하지 않는다.
        List<Sample> ordered = Arrays.stream(siblings).filter(k->k.id.longValue() <= sample).sorted(Comparator.comparing(Subjects::dateSamplingSafety, Comparator.reverseOrder())).collect(Collectors.toList());
        Sample _this = ordered.stream().filter(t->t.id.longValue() == sample).findFirst().get();
        String samplingDate = dateSamplingSafety(_this);
        return findCurrentInterpretation(ordered.stream().map(c->c.id.longValue()).collect(Collectors.toList()), service).then(response->{
            var currentInterpretation = response==null?null:response.interpretation;
            if(currentInterpretation == null) return Promise.resolve((InterpretationMrd)null);
            else { // 기존 MRD 검사는 있는데
                Sample responseSample = ordered.stream().filter(t->t.id.longValue() == response.sample).findFirst().get();
                String lastDate = currentInterpretation.histories!=null ? Arrays.stream(currentInterpretation.histories).max(Comparator.comparing(h -> h.date)).get().date : null;
                if(samplingDate.equals(lastDate)) return Promise.resolve(currentInterpretation);				// 해당 검사 결과가 이미 저장되어 있다
                else {
                    List<InterpretationMrd.MrdHistory> histories = new LinkedList<>();
                    histories.add(create(_this, service));
                    if(currentInterpretation.histories!=null) for(InterpretationMrd.MrdHistory h: currentInterpretation.histories) histories.add(h);
                    else histories.add(map(responseSample, Js.cast(currentInterpretation)));
                    var instance = new InterpretationMrd();
                    instance.histories = histories.stream().toArray(InterpretationMrd.MrdHistory[]::new);
                    return Promise.resolve(instance);
                }
            }
        });
    }
    private static String dateSamplingSafety(Sample sample) {
        String date = formatDate(sample.dateSampling!=null?sample.dateSampling.longValue():null);
        if(date == null || date.isEmpty()) {
            date = (sample.id.longValue()+"").substring(0, 8);
            date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
        }
        return date;
    }
    private static Promise<CurrentInterpretationResponse> findCurrentInterpretation(List<Long> samples, JsPropertyMap<?> service) {
        if(samples==null || samples.size()<=0) return Promise.resolve((CurrentInterpretationResponse) null);
        else {
            Promise<CurrentInterpretationResponse> promise = null;
            for(var sample: samples) {
                if(promise == null) promise = findCurrentInterpretation(sample, service).then(c->{
                    if(c==null) throw new RuntimeException();
                    else {
                        var response = new CurrentInterpretationResponse();
                        response.interpretation = c;
                        response.sample = sample;
                        return Promise.resolve(response);
                    }
                });else promise = promise.catch_(error2-> findCurrentInterpretation(sample, service).then(c->{
                    if(c==null) throw new RuntimeException();
                    else {
                        var response = new CurrentInterpretationResponse();
                        response.interpretation = c;
                        response.sample = sample;
                        return Promise.resolve(response);
                    }
                }));
            }
            return promise.then(Js::cast).catch_(error2->Promise.resolve((CurrentInterpretationResponse) null));
        }
    }
    private final static class CurrentInterpretationResponse {
        public InterpretationMrd interpretation;
        public Long sample;
    }
    private static Promise<InterpretationMrd> findCurrentInterpretation(long sampleId, JsPropertyMap<?> service) {
        return InterpretationApi.interpretation(sampleId, service.get("code").toString()).catch_(error->{
            var screens = Js.asArray(service.get("screens"));
            if(screens==null || screens.length==0) return Promise.resolve((InterpretationMrd) null);
            else {
                Promise<Object> promise = null;
                for(var screen: screens) {
                    var code = Js.asPropertyMap(screen).get("code").toString();
                    if(promise == null) promise = InterpretationApi.interpretation(sampleId, code);
                    else promise = promise.catch_(error2->InterpretationApi.interpretation(sampleId, code));
                }
                return promise.then(Js::cast).catch_(error2->Promise.resolve((InterpretationMrd) null));
            }
        });
    }

    private static InterpretationMrd.MrdHistory create(Sample sample, JsPropertyMap<?> service) {
        String date = dateSamplingSafety(sample);
        var history = new InterpretationMrd.MrdHistory();
        history.date = date;
        history.inputDna = 0.0;
        history.results = Arrays.stream(Js.asArray(service.get("genes"))).map(gene->{
            var result = new InterpretationMrd.MrdGeneResult();
            result.gene = gene.asString();
            result.target = new InterpretationMrd.MrdCloneResult();
            result.target.readDepth = 0.0;
            result.target.clonalDepth = 0.0;
            result.lqic = new InterpretationMrd.MrdCloneResult();
            result.lqic.readDepth = 0.0;
            return result;
        }).toArray(InterpretationMrd.MrdGeneResult[]::new);
        return history;
    }
    private static InterpretationMrd.MrdHistory map(Sample sample, InterpretationMrdScreen screen) {
        String date = dateSamplingSafety(sample);
        var history = new InterpretationMrd.MrdHistory();
        history.date = date;
        history.inputDna = screen.inputDna;
        history.results = Arrays.stream(screen.results).map(sr->{
            var result = new InterpretationMrd.MrdGeneResult();
            result.gene = sr.gene;
            result.result = InterpretationMrd.Result.DETECTED.name();
            result.target = new InterpretationMrd.MrdCloneResult();
            result.target.readDepth = sr.depthTotal;
            result.target.clonalDepth = sr.depthClonal() + 0.0;
            result.lqic = new InterpretationMrd.MrdCloneResult();
            result.lqic.readDepth = sr.depthLqic;
            return result;
        }).toArray(InterpretationMrd.MrdGeneResult[]::new);
        return history;
    }
    private static final DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    public static String formatDate(Long epoch) {
        return epoch == null ? null : DEFAULT_DATE_FORMAT.format(new Date(epoch));
    }
}
