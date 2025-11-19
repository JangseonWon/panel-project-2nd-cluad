package com.gcgenome.lims.client.expand.mrd.screen;

import com.gcgenome.lims.dto.InterpretationMrdScreen;
import jsinterop.base.Js;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.ListOf;
import net.sayaya.rx.subject.Subject;

import java.util.Arrays;
import java.util.stream.Collectors;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.rx.subject.Subject.subject;

public class Subjects {
    public final static Subject<String> cancerType = subject(String.class);
    public final static Subject<Double> inputDna = subject(Double.class);
    public final static BehaviorSubject<ListOf<InterpretationMrdScreen.MrdScreenGeneResult>> geneResults = (BehaviorSubject) behavior(ListOf.class);
    public final static BehaviorSubject<ListOf<InterpretationMrdScreen.SomaticMutation>> somaticMutations = (BehaviorSubject) behavior(ListOf.class);
    static {
        com.gcgenome.lims.client.Subjects.interpretation.subscribe(interpretation -> {
            try {
                InterpretationMrdScreen cast = Js.cast(interpretation);
                cancerType.next(cast.cancerType);
                inputDna.next(cast.inputDna);
                {
                    var listOf = new ListOf<InterpretationMrdScreen.MrdScreenGeneResult>();
                    var results = cast.results;
                    if(results!=null && results.length > 0) listOf.replaceTo(Arrays.stream(cast.results).collect(Collectors.toList()));
                    listOf.onValueChange(evt -> geneResults.next(listOf));
                    geneResults.next(listOf);
                } {
                    var listOf = new ListOf<InterpretationMrdScreen.SomaticMutation>();
                    var results = cast.somaticMutations;
                    if(results!=null && results.length > 0) listOf.replaceTo(Arrays.stream(cast.somaticMutations).collect(Collectors.toList()));
                    listOf.onValueChange(evt -> somaticMutations.next(listOf));
                    somaticMutations.next(listOf);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });
        cancerType.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationMrdScreen());
            InterpretationMrdScreen cast = Js.cast(_interpretation);
            cast.cancerType = evt;
        });
        inputDna.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationMrdScreen());
            InterpretationMrdScreen cast = Js.cast(_interpretation);
            cast.inputDna = evt;
        });
        geneResults.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationMrdScreen());
            InterpretationMrdScreen cast = Js.cast(_interpretation);
            cast.results = evt.value().stream().toArray(InterpretationMrdScreen.MrdScreenGeneResult[]::new);
        });
        somaticMutations.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationMrdScreen());
            InterpretationMrdScreen cast = Js.cast(_interpretation);
            cast.somaticMutations = evt.value().stream().toArray(InterpretationMrdScreen.SomaticMutation[]::new);
        });
    }
}
