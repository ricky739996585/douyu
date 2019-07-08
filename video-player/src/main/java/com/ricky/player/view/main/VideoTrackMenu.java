/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */

package com.ricky.player.view.main;

import com.ricky.player.view.action.mediaplayer.VideoTrackAction;
import uk.co.caprica.vlcj.player.base.TrackDescription;

import javax.swing.*;
import java.util.List;

import static com.ricky.player.Application.application;
import static com.ricky.player.view.action.Resource.resource;

final class VideoTrackMenu extends TrackMenu {

    VideoTrackMenu() {
        super(resource("menu.video.item.track"));
    }

    @Override
    protected Action createAction(TrackDescription trackDescription) {
        return new VideoTrackAction(trackDescription.description(), trackDescription.id());
    }

    @Override
    protected List<TrackDescription> onGetTrackDescriptions() {
        return application().mediaPlayer().video().trackDescriptions();
    }

    @Override
    protected int onGetSelectedTrack() {
        return application().mediaPlayer().video().track();
    }
}
