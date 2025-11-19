package com.gcgenome.lims.client;

import com.gcgenome.lims.api.RouteApi;
import com.gcgenome.lims.client.interfaces.work.WorkScene;
import com.gcgenome.lims.client.interfaces.worklist.WorklistScene;
import com.google.gwt.core.client.EntryPoint;
import dev.sayaya.rx.Subscription;
import elemental2.dom.HTMLElement;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.Animation;
import org.jboss.elemento.IsElement;

import static org.jboss.elemento.Elements.body;

public class Application implements EntryPoint {
    private final Component components = DaggerComponent.create();
    private final WorklistScene worklist = components.worklistScene();
    private final WorkScene work = components.workScene();
    private IsElement<? extends HTMLElement> scene = null;
    private Subscription subscription;
    @Override
    public void onModuleLoad() {
        worklist.element().style.display = "none";
        work.element().style.display = "none";
        body().add(worklist);
        body().add(work);
        // components.keywordListener();
        initializeRouter();
    }
    protected IsElement<? extends HTMLElement> prepare(String param) {
        if(subscription!=null) subscription.unsubscribe();
        if(param == null || param.isEmpty() || param.endsWith(".html")){
            subscription = components.worklistSubscription();
            return worklist;
        } else {
            subscription = components.workSubscription();
            components.worklistId().next(param);
            return work;
        }
    }
    private void select(IsElement<? extends HTMLElement> target) {
        if(target == scene) return;
        var currentScene = scene;
        scene = target;
        target.element().style.display = "inherit";
        if(currentScene!=null) {
            Animation.AnimationImpl fadeOut = Animation.animate(currentScene.element(), 300,
                    JsPropertyMap.of("opacity", "1"),
                    JsPropertyMap.of("opacity", "0"));
            fadeOut.onfinish = ()->{
                currentScene.element().style.display = "none";
            };
            fadeOut.play();
        }
        Animation.animate(target.element(), 300,
                JsPropertyMap.of("opacity", "0"),
                JsPropertyMap.of("opacity", "1")).play();
    }
    private void initializeRouter() {
        Router.addValueChangeHandler(evt->{
            String param = evt.value();
            select(prepare(param));
        });
        Router.initialize();
        // 초기화 후에 발생하는 IFrame URL 변경 건에 대해 Parent URL 변경 코드를 실행한다.
        Router.addValueChangeHandler(evt->{
            String param = evt.value();
            RouteApi.location(toParentUrl(param), false, false);
        });
    }
    protected String toParentUrl(String param) {
        if(param == null || param.isEmpty() || param.endsWith(".html")) return "패널검사/Worklist";
        else return "패널검사/Worklist/" + param;
    }
}
