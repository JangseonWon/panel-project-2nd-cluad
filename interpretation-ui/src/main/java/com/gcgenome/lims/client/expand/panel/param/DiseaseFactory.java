package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.dto.ParameterPanel;
import elemental2.core.JsArray;
import elemental2.core.JsRegExp;
import elemental2.core.RegExpResult;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;

import static elemental2.core.Global.JSON;

@UtilityClass
class DiseaseFactory {
    public static class GeneDisease {
        public String gene;
        public ParameterPanel.Disease[] diseases;
        public GeneDisease add(ParameterPanel.Disease disease) {
            if(this.diseases == null) this.diseases = new ParameterPanel.Disease[]{disease};
            else {
                JsArray<ParameterPanel.Disease> tmp = Js.cast(disease);
                tmp.push(disease);
            }
            return this;
        }
    }
    public GeneDisease[] map(InterpretationPanel.Variant... variants) {
        List<GeneDisease> diseases = new LinkedList<>();
        if(variants!=null) Arrays.stream(variants).map(DiseaseFactory::map).forEach(r->{
            var match = diseases.stream().filter(d2->d2.gene.equals(r.gene)).findFirst();
            if(match.isPresent()) {
                var p = match.get();
                for(var disease: r.diseases) if( Arrays.stream(p.diseases).noneMatch(d->d.fullName.equals(disease.fullName))) match.get().add(disease);
            } else diseases.add(r);
        });
        return diseases.stream().toArray(GeneDisease[]::new);
    }
    private static final JsRegExp OMIM_DISEASE_FORMAT = new JsRegExp("^(.+),\\s*\\d+\\s*[(]\\d+[)],*\\s*([A-Za-z ,-]*)$");
    public GeneDisease map(InterpretationPanel.Variant variant) {
        GeneDisease gd = new GeneDisease();
        gd.gene = variant.gene;
        String mims = variant.mim;
        if(mims==null) mims = "";
        if(mims.contains("%")){
            String[] splits = mims.split("%");
            String diseasesString = splits[0];
            String[] inheritances = ((JsArray<String>)JSON.parse(splits[1])).asArray(new String[]{});
            String[] diseases = diseasesString.split("\\|");
            List<String[]> pairs = new ArrayList<>();
            for(int i = 0; i < diseases.length; i++){
                if(inheritances[i] != null){
                    pairs.add(new String[]{diseases[i], inheritances[i]});
                }else {
                    pairs.add(new String[]{diseases[i], ""});
                }
            }
            gd.diseases = pairs.stream().map(DiseaseFactory::map).filter(Objects::nonNull).toArray(ParameterPanel.Disease[]::new);
        }else {
            gd.diseases = Arrays.stream(mims.split("\\|")).map(DiseaseFactory::map).filter(Objects::nonNull).toArray(ParameterPanel.Disease[]::new);
        }
        if(gd.diseases.length == 1) {
            ParameterPanel.Disease disease = gd.diseases[0];
            if(variant.disease!=null) disease.abbreviation = variant.disease;
            if(variant.inheritance!=null) disease.inheritance = Arrays.stream(variant.inheritance.split(",")).map(String::trim).toArray(String[]::new);
        }
        return gd;
    }
    private ParameterPanel.Disease map(String mim) {
        if(mim == null) return null;
        RegExpResult m = OMIM_DISEASE_FORMAT.exec(mim.trim());
        if(m!=null) {
            ParameterPanel.Disease disease = new ParameterPanel.Disease();
            disease.fullName = unwrap(m.getAt(1));
            disease.abbreviation = diseaseShort(disease.fullName);
            disease.inheritance = inheritance(m.getAt(2));
            return disease;
        } else if(!mim.contains("%")){
            ParameterPanel.Disease disease = new ParameterPanel.Disease();
            disease.fullName = unwrap(mim);
            disease.abbreviation = diseaseShort(disease.fullName);
            return disease;
        }
        else return null;
    }
    private ParameterPanel.Disease map(String[] pair) {
        if(pair[0] == null) return null;
        String diseaseString = pair[0];
        String inheritance = pair[1];
        if(inheritance.equals("")) return map(diseaseString);
        if(!diseaseString.isEmpty()) {
            ParameterPanel.Disease disease = new ParameterPanel.Disease();
            disease.fullName = unwrap(diseaseString);
            disease.abbreviation = diseaseShort(disease.fullName);
            if(inheritance.trim().isEmpty()) {
                disease.inheritance = new String[]{};
            }else {
                disease.inheritance = Arrays.stream(inheritance.trim().split("[,;|]")).map(String::trim).collect(Collectors.toList()).toArray(new String[]{});
            }
            return disease;
        }else return null;
    }
    private String unwrap(String fullname) {
        if(fullname == null) return null;
        fullname = fullname.trim();
        if(fullname.contains("-")) fullname = fullname.replace("-", " ");
        if(fullname.contains("_")) fullname = fullname.replace("_", " ");
        if(fullname.contains("(")) fullname = fullname.replace("(", " ");
        if(fullname.contains(")")) fullname = fullname.replace(")", "");
        if(fullname.contains("{")) fullname = fullname.replace("{", " ");
        if(fullname.contains("}")) fullname = fullname.replace("}", "");
        fullname = fullname.trim();
        if(fullname.matches("^\\[.+\\]$")) fullname = fullname.substring(1, fullname.length()-2);
        return fullname.trim();
    }
    private static final JsRegExp NUMBER_FORMAT = new JsRegExp("^([A-Za-z]{0,1})(\\d*).*$");
    private String diseaseShort(String fullname) {
        if(fullname == null) return null;
        if(fullname.length()<15) return fullname;
        if(!fullname.contains(" ")) return fullname.substring(0, 4).toUpperCase();
        String[] split = fullname.split(" ");
        StringBuilder sb = new StringBuilder();
        for(String s: split) {
            RegExpResult m = NUMBER_FORMAT.exec(s.trim());
            if(m!=null) {
                if(m.getAt(1)!=null) sb.append(m.getAt(1));
                if(m.getAt(2)!=null) sb.append(m.getAt(2));
            } else sb.append(s.charAt(0));
        }
        return sb.toString().toUpperCase();
    }
    public static String[] inheritance(String orig) {
        if(orig == null) return new String[0];
        var builder = InheritanceBuilder.builder(orig);
        return builder.chk("autosomal dominant", "AD")
                .chk("x-linked dominant", "XLD")
                .chk("autosomal recessive", "AR")
                .chk("x-linked recessive", "XLR")
                .chk("digenic dominant", "DD")
                .chk("digenic recessive", "DR")
                .chk("xld", "XLD")
                .chk("xlr", "XLR")
                .chk("ad", "AD")
                .chk("ar", "AR")
                .chk("xd", "XLD")
                .chk("xr", "XLR")
                .chk("dd", "DD")
                .chk("dr", "DR").build();
    }
    private final static class InheritanceBuilder {
        private String input;
        private List<String> tmp = new LinkedList<>();
        public static InheritanceBuilder builder(String input) {
            return new InheritanceBuilder(input);
        }
        private InheritanceBuilder(String input) {
            this.input = input.toLowerCase();
        }
        public InheritanceBuilder chk(String inheritance, String abbr) {
            inheritance = inheritance.toLowerCase();
            if(input.contains(inheritance))	{
                tmp.add(abbr);
                input = input.replace(inheritance, "");
            }
            return this;
        }
        public String[] build() {
            return tmp.stream().toArray(String[]::new);
        }
    }
}

