package com.gcgenome.lims.client;

import com.gcgenome.lims.test.Reportable;
import elemental2.dom.DomGlobal;
import jsinterop.base.JsPropertyMap;

public class InterpretationI18nUtil {
    public static class Messages {
        private Messages() {}
        public static final String ENUS_AUTO_ALERT = "이 검사는 영문 검사입니다.\n 영문 자동 판독 미지원 시 국문으로 소견이 생성됩니다.";
        public static final String AUTO_ALERT = "판독 문구를 자동 생성합니다.";
        public static final String KOREAN_DETECTED = "영문 검사 결과지에 한글 판독 문구가 포함되었습니다. 영문으로 판독 부탁드립니다.";
    }

    private static boolean isEnUsTest() {
        return Reportable.I18N.EnUs.toString().equals(Subjects.service.getValue().get("i18n"));
    }
    public static boolean confirmAutoInterpretation() {
        String message = isEnUsTest() ? Messages.ENUS_AUTO_ALERT : Messages.AUTO_ALERT;
        return DomGlobal.confirm(message);
    }

    public static boolean validateEnglishReport(JsPropertyMap<?> interpretation) {
        if (!isEnUsTest()) return true;

        if (hasKoreanInJson(interpretation)) {
            DomGlobal.alert(Messages.KOREAN_DETECTED);
            return false;
        }
        return true;
    }
    private static boolean hasKoreanInJson(Object obj) {
        try {
            String jsonStr = stringify(obj);
            return searchKoreanInAllFields(jsonStr);
        } catch (Exception e) {
            DomGlobal.console.error("JSON processing failed during Korean detection.", e);
            return false;
        }
    }

    private static native String stringify(Object obj) /*-{
        return JSON.stringify(obj);
    }-*/;

    private static native boolean searchKoreanInAllFields(String json) /*-{
        var obj = JSON.parse(json);
        var koreanRegex = /[가-힣]+/;

        function checkValue(value) {
            return typeof value === 'string' && koreanRegex.test(value);
        }

        function findKorean(obj) {
            if (!obj || typeof obj !== 'object') {
                return checkValue(obj);
            }

            for (var key in obj) {
            var value = obj[key];
                if (checkValue(value)) return true;
                if (typeof value === 'object' && value !== null) {
                    if (findKorean(value)) return true;
                }
            }
            return false;
        }

        return findKorean(obj);
    }-*/;
}