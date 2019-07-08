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

import com.google.common.eventbus.Subscribe;
import com.ricky.player.event.TickEvent;
import com.ricky.player.view.BorderedStandardLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static com.ricky.player.Application.application;
import static com.ricky.player.time.Time.formatTime;

final class StatusBar extends JPanel {

    private final JLabel titleLabel;

    private final JLabel rateLabel;

    private final JLabel timeLabel;

    private long time;

    private long duration;

    StatusBar() {
        titleLabel = new BorderedStandardLabel();
        rateLabel = new BorderedStandardLabel();
        timeLabel = new BorderedStandardLabel();

        setLayout(new MigLayout("fillx, insets 2 n n n", "[grow]16[][]", "[]"));
        add(titleLabel, "grow");
        add(rateLabel);
        add(timeLabel);

        application().subscribe(this);
    }

    void setTitle(String title) {
        titleLabel.setText(title);
    }

    void setRate(String rate) {
        rateLabel.setText(rate);
    }

    void setTime(long time) {
        this.time = time;
    }

    void setDuration(long duration) {
        this.duration = duration;
        refresh();
    }

    void clear() {
        titleLabel.setText(null);
        rateLabel.setText(null);
        timeLabel.setText(null);
    }

    void refresh() {
        timeLabel.setText(String.format("%s/%s", formatTime(time), formatTime(duration)));
    }

    @Subscribe
    public void onTick(TickEvent tick) {
        refresh();
    }
}
