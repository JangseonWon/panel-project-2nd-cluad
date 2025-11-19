package com.gcgenome.lims.client.interfaces.worklist.serial;

import lombok.experimental.Delegate;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.Dialog;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SerialCreateDialog {
    private final ButtonElementText btnCancel = ButtonElement.flat().text("닫기");
    private final ButtonElementText btnSubmit = ButtonElement.contain().text("적용");
    @Delegate private final Dialog dialog = Dialog.confirmation("워크리스트 시리얼 생성", btnCancel, btnSubmit);
    @Inject SerialCreateDialog() {

    }
}
