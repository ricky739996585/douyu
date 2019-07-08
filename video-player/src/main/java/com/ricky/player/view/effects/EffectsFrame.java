package com.ricky.player.view.effects;

import com.google.common.eventbus.Subscribe;
import com.ricky.player.event.ShowEffectsEvent;
import com.ricky.player.view.BaseFrame;
import com.ricky.player.view.effects.audio.AudioEffectsPanel;
import com.ricky.player.view.effects.video.VideoEffectsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.prefs.Preferences;

import static com.ricky.player.Application.resources;

@SuppressWarnings("serial")
public class EffectsFrame extends BaseFrame {

    private final JTabbedPane tabbedPane;

    private final AudioEffectsPanel audioEffectsPanel;
    private final VideoEffectsPanel videoEffectsPanel;

    public EffectsFrame() {
        super(resources().getString("dialog.effects"));

        tabbedPane = new JTabbedPane();

        audioEffectsPanel = new AudioEffectsPanel();
        tabbedPane.addTab(resources().getString("dialog.effects.tabs.audio"), audioEffectsPanel);

        videoEffectsPanel = new VideoEffectsPanel();
        tabbedPane.addTab(resources().getString("dialog.effects.tabs.video"), videoEffectsPanel);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(4,  4,  4,  4));
        contentPane.setLayout(new MigLayout("fill", "[grow]", "[grow]"));
        contentPane.add(tabbedPane, "grow");

        setContentPane(tabbedPane);

        applyPreferences();
    }

    private void applyPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(EffectsFrame.class);
        setBounds(
            prefs.getInt("frameX"     , 300),
            prefs.getInt("frameY"     , 300),
            prefs.getInt("frameWidth" , 500),
            prefs.getInt("frameHeight", 500)
        );
    }

    @Override
    protected void onShutdown() {
        if (wasShown()) {
            Preferences prefs = Preferences.userNodeForPackage(EffectsFrame.class);
            prefs.putInt("frameX"      , getX     ());
            prefs.putInt("frameY"      , getY     ());
            prefs.putInt("frameWidth"  , getWidth ());
            prefs.putInt("frameHeight" , getHeight());
        }
    }

    @Subscribe
    public void onShowEffects(ShowEffectsEvent event) {
        setVisible(true);
    }
}
