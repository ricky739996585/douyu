package com.ricky.player;

import com.ricky.player.event.AfterExitFullScreenEvent;
import com.ricky.player.event.BeforeEnterFullScreenEvent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import java.awt.*;

import static com.ricky.player.Application.application;


final class VlcjPlayerFullScreenStrategy extends AdaptiveFullScreenStrategy {

    VlcjPlayerFullScreenStrategy(Window window) {
        super(window);
    }

    @Override
    protected void onBeforeEnterFullScreen() {
        application().post(BeforeEnterFullScreenEvent.INSTANCE);
    }

    @Override
    protected  void onAfterExitFullScreen() {
        application().post(AfterExitFullScreenEvent.INSTANCE);
    }

}
