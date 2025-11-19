package com.gcgenome.lims.client.expand.somatic;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationSomatic;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import jsinterop.base.Js;
import net.sayaya.ui.*;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class DrugPhenotype extends HTMLElementBuilder<HTMLDivElement, DrugPhenotype> implements InterpretationSubject<HTMLDivElement> {
    public static DrugPhenotype build(String gene) {
        return new DrugPhenotype(gene, Phenotypes.values(), div());
    }
    private final HTMLContainerBuilder<HTMLLabelElement> title = label().css("title").style("font-size: 1.5rem;");
    private final TextFieldElement.TextFieldOutlined<String> iptDiplotype = TextFieldElement.textBox().outlined().css("input").text("Diplotype");
    private final DropDownElement iptAlleleStatus;
    private final TextFieldElement.TextFieldOutlined<String> iptPhenotype = TextFieldElement.textBox().outlined().style("width: 230px;").css("input").text("Phenotype");
    private final TextFieldElement.TextFieldOutlined<String> iptResult = TextFieldElement.textBox().outlined().style("width: 230px;").css("input").text("EHR Priority Result ");
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 110px;").text("Interpretation");
    private final String gene;
    private final Phenotypes[] phenotypes;
    private InterpretationSomatic value;
    public DrugPhenotype(String gene, Phenotypes[] phenotypes, HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        this.gene = gene;
        this.phenotypes = phenotypes;
        ListElement<ListElement.SingleLineItem> selections = ListElement.singleLineList();
        for(Phenotypes phenotype: phenotypes) selections.add(ListElement.singleLine().label(phenotype.alleleStatus));
        iptAlleleStatus = DropDownElement.outlined(selections).style("width: 350px;").css("input").text("Allele function status");
        iptAlleleStatus.onSelectionChange(evt->autoFill(phenotypes[evt.selection()]));
        e.style("margin-left: 15px; padding-right: 15px; margin-bottom: 16px; margin-bottom: 16px;")
                .add(this.title.add(gene.toUpperCase()))
                .add(div().style("margin-top: 10px; margin-right: 15px;").add(iptAlleleStatus).add(iptDiplotype).add(iptPhenotype).add(iptResult))
                .add(iptInterpretation);
        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationSomatic value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ this.value = null; }});
        iptDiplotype.onValueChange(evt->sync(findNorAssign(value)));
        iptAlleleStatus.onValueChange(evt->sync(findNorAssign(value)));
        iptPhenotype.onValueChange(evt->sync(findNorAssign(value)));
        iptResult.onValueChange(evt->sync(findNorAssign(value)));
        iptInterpretation.onValueChange(evt->sync(findNorAssign(value)));
    }
    private void autoFill(Phenotypes phenotype) {
        iptPhenotype.value(phenotype.phenotype);
        iptResult.value(phenotype.result);
        iptInterpretation.value(interpretation(gene, phenotype));
    }
    private InterpretationSomatic.DrugPhenotype findNorAssign(InterpretationSomatic value) {
        if(value==null) return null;
        if(value.drugPhenotypes==null) value.drugPhenotypes = new InterpretationSomatic.DrugPhenotype[] {};
        var has = Arrays.stream(value.drugPhenotypes).filter(Objects::nonNull).filter(phenotype -> phenotype.gene!=null).filter(phenotype->phenotype.gene.equals(gene)).findAny();
        if(has.isPresent()) return has.get();
        else {
            if(iptDiplotype.value() == null || iptDiplotype.value().trim().isEmpty()) return null;
            var data = new InterpretationSomatic.DrugPhenotype();
            data.gene = gene;
            value.drugPhenotypes = Stream.concat(Stream.of(value.drugPhenotypes), Stream.of(data)).toArray(InterpretationSomatic.DrugPhenotype[]::new);
            return data;
        }
    }
    private void update(InterpretationSomatic value) {
        this.value = value;
        var phenotype = findNorAssign(value);
        if(phenotype == null) return;
        for(int i = 0; i < phenotypes.length; ++i) if(phenotypes[i].alleleStatus.equals(phenotype.alleleStatus)) iptAlleleStatus.select(i);
        iptPhenotype.value(phenotype.phenotype);
        iptDiplotype.value(phenotype.diplotype);
        iptResult.value(phenotype.result);
        iptInterpretation.value(phenotype.interpretation);
    }
    private void sync(InterpretationSomatic.DrugPhenotype phenotype) {
        if(phenotype == null) return;
        if(iptDiplotype.value() == null || iptDiplotype.value().trim().isEmpty()) return;
        phenotype.gene = gene;
        phenotype.diplotype = iptDiplotype.value();
        phenotype.alleleStatus = iptAlleleStatus.value();
        phenotype.phenotype = iptPhenotype.value();
        phenotype.result = iptResult.value();
        phenotype.interpretation = iptInterpretation.value();
    }
    @Override
    public DrugPhenotype that() {
        return this;
    }

    private enum Phenotypes {
        NN("Normal function/Normal function", "Normal Metabolizer", "Normal/Routine/Low risk"),
        NO("Normal function/No function", "Intermediate Metabolizer", "Abnormal/Priority/High Risk"),
        OO("No function/No function", "Poor Metabolizer", "Abnormal/Priority/High Risk"),
        NU("Normal function/Uncertain Function", "Indeterminate", "None"),
        UU("Uncertain Function/Uncertain Function", "Indeterminate", "None");
        private final String alleleStatus;
        private final String phenotype;
        private final String result;
        Phenotypes(String alleleStatus, String phenotype, String result) {
            this.alleleStatus = alleleStatus;
            this.phenotype = phenotype;
            this.result = result;
        }
    }
    private static String interpretation(String gene, Phenotypes phenotype) {
        if("NUDT15".equalsIgnoreCase(gene)) {
            switch (phenotype) {
                case NN: return "이 결과는 환자가 정상 기능의 allele (normal function allele) 두개를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 NUDT15 Normal Metabolizer (정상 대사자)로 예측됩니다. NUDT15에 의해 대사되는 대부분의 약물의 용량을 선택적으로 조정할 이유가 없습니다. 자세한 내용은 CPIC 가이드라인을 참조하십시오(https://cpicpgx.org/).";
                case NO: return "이 결과는 환자가 정상 기능의 allele (normal function allele) 하나와 기능이 없는 allele (no function allele) 하나를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 NUDT15 Intermediate Metabolizer (중간 대사자)로 예측됩니다. 이 환자는 NUDT15에 의해 대사되는 약물(예: 티오퓨린)에 대한 부작용의 위험이 있을 수 있으며, 부적절한 약물 반응을 피하기 위해 NUDT15에 의해 대사되는 약물에 대해 용량 조절 또는 대체 치료제가 필요할 수 있습니다. 자세한 내용은 CPIC 가이드라인을 참조하십시오(https://cpicpgx.org/).";
                case OO: return "이 결과는 환자가 기능이 없는 allele (no function allele) 두개를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 NUDT15 Poor Metabolizer (대사 불량자)로 예측됩니다. 이 환자는 NUDT15에 의해 대사되는 약물(예: 티오퓨린)에 대한 이상반응의 위험이 높을 수 있으며, 부적절한 약물 반응을 피하기 위해 NUDT15에 의해 대사되는 약물에 대해 용량 조절 또는 대체 요법이 필요할 수 있습니다. 자세한 내용은 CPIC 가이드라인을 참조하십시오(https://cpicpgx.org/).";
                case NU: return "이 결과는 환자가 정상 기능의 allele (normal function allele) 하나와 기능이 알려지지 않은 allele (uncertain function allele) 하나를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 NUDT15 phenotype을 예측할 수 없습니다.";
                case UU: return "이 결과는 환자가 기능이 알려지지 않은 allele (uncertain function allele) 두개를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 NUDT15 phenotype을 예측할 수 없습니다.";
                default: return null;
            }
        } else if("TPMT".equalsIgnoreCase(gene)) {
            switch (phenotype) {
                case NN: return "이 결과는 환자가 정상 기능의 allele (normal function allele) 두개를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 TPMT Normal Metabolizer (정상 대사자)로 예측됩니다. TPMT에 의해 대사되는 대부분의 약물의 용량을 선택적으로 조정할 이유가 없습니다. 자세한 내용은 CPIC 가이드라인을 참조하십시오(https://cpicpgx.org/).";
                case NO: return "이 결과는 환자가 정상 기능의 allele (normal function allele) 하나와 기능이 없는 allele (no function allele) 하나를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 TPMT Intermediate Metabolizer (중간 대사자)로 예측됩니다. 이 환자는 TPMT에 의해 대사되는 약물(예: 티오퓨린)에 대한 부작용의 위험이 있을 수 있으며, 부적절한 약물 반응을 피하기 위해 TPMT에 의해 대사되는 약물에 대해 용량 조절 또는 대체 치료제가 필요할 수 있습니다. 자세한 내용은 CPIC 가이드라인을 참조하십시오(https://cpicpgx.org/).";
                case OO: return "이 결과는 환자가 기능이 없는 allele (no function allele) 두개를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 TPMT Poor Metabolizer (대사 불량자)로 예측됩니다. 이 환자는 TPMT에 의해 대사되는 약물(예: 티오퓨린)에 대한 이상반응의 위험이 높을 수 있으며, 부적절한 약물 반응을 피하기 위해 TPMT에 의해 대사되는 약물에 대해 용량 조절 또는 대체 요법이 필요할 수 있습니다. 자세한 내용은 CPIC 가이드라인을 참조하십시오(https://cpicpgx.org/).";
                case NU: return "이 결과는 환자가 정상 기능의 allele (normal function allele) 하나와 기능이 알려지지 않은 allele (uncertain function allele) 하나를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 TPMT phenotype을 예측할 수 없습니다.";
                case UU: return "이 결과는 환자가 기능이 알려지지 않은 allele (uncertain function allele) 두개를 가지고 있음을 의미하며, 유전자형 결과에 기초하여 이 환자는 TPMT phenotype을 예측할 수 없습니다.";
                default: return null;
            }
        }
        return null;
    }
}
