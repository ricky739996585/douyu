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
import com.ricky.player.event.*;
import com.ricky.player.utils.DataBaseUtils;
import com.ricky.player.view.BaseFrame;
import com.ricky.player.view.action.StandardAction;
import com.ricky.player.view.action.mediaplayer.MediaPlayerActions;
import com.ricky.player.view.action.mediaplayer.RendererAction;
import com.ricky.player.view.snapshot.SnapshotView;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.media.MediaSlaveType;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.*;
import uk.co.caprica.vlcj.player.renderer.RendererItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

import static com.ricky.player.Application.application;
import static com.ricky.player.Application.resources;
import static com.ricky.player.view.action.Resource.resource;


@SuppressWarnings("serial")
public final class MainFrame extends BaseFrame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user?useSSL=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    private static Integer limit = 1;

    private static final String ACTION_EXIT_FULLSCREEN = "exit-fullscreen";

    private static final KeyStroke KEYSTROKE_ESCAPE = KeyStroke.getKeyStroke("ESCAPE");

    private static final KeyStroke KEYSTROKE_TOGGLE_FULLSCREEN = KeyStroke.getKeyStroke("F11");

    private final Action mediaOpenAction;
    private final Action mediaQuitAction;

    private final Action playbackRendererLocalAction;

    private final StandardAction videoFullscreenAction;
    private final StandardAction videoAlwaysOnTopAction;

    private final Action subtitleAddSubtitleFileAction;

    private final Action toolsEffectsAction;
    private final Action toolsMessagesAction;
    private final Action toolsDebugAction;

    private final StandardAction viewStatusBarAction;

    private final Action helpAboutAction;

    private final JMenuBar menuBar;

    private final JMenu mediaMenu;
    private final JMenu mediaRecentMenu;

    private final JMenu playbackMenu;
    private final JMenu playbackTitleMenu;
    private final JMenu playbackChapterMenu;
    private final JMenu playbackRendererMenu;
    private final JMenu playbackSpeedMenu;

    private final JMenu audioMenu;
    private final JMenu audioTrackMenu;
    private final JMenu audioDeviceMenu;
    private final JMenu audioStereoMenu;

    private final JMenu videoMenu;
    private final JMenu videoTrackMenu;
    private final JMenu videoZoomMenu;
    private final JMenu videoAspectRatioMenu;
    private final JMenu videoCropMenu;
    private final JMenu videoOutputMenu;

    private final JMenu subtitleMenu;
    private final JMenu subtitleTrackMenu;

    private final JMenu toolsMenu;

    private final JMenu viewMenu;

    private final JMenu helpMenu;

    private final JFileChooser fileChooser;

    private final PositionPane positionPane;

    private final ControlsPane controlsPane;

    private final StatusBar statusBar;

    private final VideoContentPane videoContentPane;

    private final JPanel bottomPane;

//    private final MouseMovementDetector mouseMovementDetector;

    private final List<RendererItem> renderers = new ArrayList<>();

    public MainFrame() {
        super("vlcj player");

        MediaPlayerActions mediaPlayerActions = application().mediaPlayerActions();
        //打开文件
        mediaOpenAction = new StandardAction(resource("menu.media.item.openFile")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(MainFrame.this)) {
                    File file = fileChooser.getSelectedFile();
                    String mrl = file.getAbsolutePath();
                    application().addRecentMedia(mrl);
                    application().mediaPlayer().media().play(mrl);
                }
            }
        };
        //退出
        mediaQuitAction = new StandardAction(resource("menu.media.item.quit")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        };
        //回放
        playbackRendererLocalAction = new StandardAction(resource("menu.playback.item.renderer.item.local")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                application().mediaPlayer().setRenderer(null);
            }
        };
        //全屏
        videoFullscreenAction = new StandardAction(resource("menu.video.item.fullscreen")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                application().mediaPlayer().fullScreen().toggle();
            }
        };
        //总适应窗口
        videoAlwaysOnTopAction = new StandardAction(resource("menu.video.item.alwaysOnTop")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean onTop;
                Object source = e.getSource();
                if (source instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)source;
                    onTop = menuItem.isSelected();
                }
                else {
                    throw new IllegalStateException("Don't know about source " + source);
                }
                setAlwaysOnTop(onTop);
            }
        };
        //增加字幕
        subtitleAddSubtitleFileAction = new StandardAction(resource("menu.subtitle.item.addSubtitleFile")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(MainFrame.this)) {
                    File file = fileChooser.getSelectedFile();
//                    mediaPlayerComponent.getMediaPlayer().subpictures().setSubTitleFile(file);
                    // FIXME flawed on Windows because of \\ vs /
                    application().mediaPlayer().media().addSlave(MediaSlaveType.SUBTITLE, String.format("file://%s", file.getAbsolutePath()), true);
                }
            }
        };
        //滤镜与效果
        toolsEffectsAction = new StandardAction(resource("menu.tools.item.effects")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                application().post(ShowEffectsEvent.INSTANCE);
            }
        };
        //消息
        toolsMessagesAction = new StandardAction(resource("menu.tools.item.messages")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                application().post(ShowMessagesEvent.INSTANCE);
            }
        };
        //调试
        toolsDebugAction = new StandardAction(resource("menu.tools.item.debug")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                application().post(ShowDebugEvent.INSTANCE);
            }
        };
        //状态栏
        viewStatusBarAction = new StandardAction(resource("menu.view.item.statusBar")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean visible;
                Object source = e.getSource();
                if (source instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)source;
                    visible = menuItem.isSelected();
                }
                else {
                    throw new IllegalStateException("Don't know about source " + source);
                }
                statusBar.setVisible(visible);
                bottomPane.invalidate();
                bottomPane.revalidate();
                bottomPane.getParent().invalidate();
                bottomPane.getParent().revalidate();
                MainFrame.this.invalidate();
                MainFrame.this.revalidate();
            }
        };
        //关于
        helpAboutAction = new StandardAction(resource("menu.help.item.about")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog dialog = new AboutDialog(MainFrame.this);
                dialog.setLocationRelativeTo(MainFrame.this);

                dialog.setVisible(true);
            }
        };

        menuBar = new JMenuBar();

        mediaMenu = new JMenu(resource("menu.media").name());
        mediaMenu.setMnemonic(resource("menu.media").mnemonic());
        mediaMenu.add(new JMenuItem(mediaOpenAction));
        mediaRecentMenu = new RecentMediaMenu(resource("menu.media.item.recent")).menu();
        mediaMenu.add(mediaRecentMenu);
        mediaMenu.add(new JSeparator());
        mediaMenu.add(new JMenuItem(mediaQuitAction));
        menuBar.add(mediaMenu);

        playbackMenu = new JMenu(resource("menu.playback").name());
        playbackMenu.setMnemonic(resource("menu.playback").mnemonic());

        playbackTitleMenu = new TitleTrackMenu().menu();

        // Chapter could be an "on-demand" menu too, and it could be with radio buttons...

        playbackMenu.add(playbackTitleMenu);

        playbackChapterMenu = new ChapterMenu().menu();
        playbackMenu.add(playbackChapterMenu);
        playbackMenu.add(new JSeparator());

        playbackRendererMenu = new JMenu(resource("menu.playback.item.renderer").name());
        playbackRendererMenu.setMnemonic(resource("menu.playback.item.renderer").mnemonic());
        playbackRendererMenu.add(new JMenuItem(playbackRendererLocalAction));
        playbackMenu.add(playbackRendererMenu);

        playbackMenu.add(new JSeparator());

        playbackSpeedMenu = new JMenu(resource("menu.playback.item.speed").name());
        playbackSpeedMenu.setMnemonic(resource("menu.playback.item.speed").mnemonic());
        for (Action action : mediaPlayerActions.playbackSpeedActions()) {
            playbackSpeedMenu.add(new JMenuItem(action));
        }
        playbackMenu.add(playbackSpeedMenu);
        playbackMenu.add(new JSeparator());
        for (Action action : mediaPlayerActions.playbackSkipActions()) {
            playbackMenu.add(new JMenuItem(action));
        }
        playbackMenu.add(new JSeparator());
        for (Action action : mediaPlayerActions.playbackChapterActions()) {
            playbackMenu.add(new JMenuItem(action));
        }
        playbackMenu.add(new JSeparator());
        for (Action action : mediaPlayerActions.playbackControlActions()) {
            // FIXME need a standardmenuitem that disables the tooltip like this, very poor show...
            playbackMenu.add(new JMenuItem(action) {
                @Override
                public String getToolTipText() {
                    return null;
                }
            });
        }
        //增加回放菜单
        menuBar.add(playbackMenu);

        audioMenu = new JMenu(resource("menu.audio").name());
        audioMenu.setMnemonic(resource("menu.audio").mnemonic());

        audioTrackMenu = new AudioTrackMenu().menu();

        audioMenu.add(audioTrackMenu);
        audioDeviceMenu = new AudioDeviceMenu().menu();
        audioMenu.add(audioDeviceMenu);
        audioStereoMenu = new JMenu(resource("menu.audio.item.stereoMode").name());
        audioStereoMenu.setMnemonic(resource("menu.audio.item.stereoMode").mnemonic());
        for (Action action : mediaPlayerActions.audioStereoModeActions()) {
            // FIXME the radio buttons are not clearing when the selection changes
            audioStereoMenu.add(new JRadioButtonMenuItem(action));
        }
        audioMenu.add(audioStereoMenu);
        audioMenu.add(new JSeparator());
        for (Action action : mediaPlayerActions.audioControlActions()) {
            audioMenu.add(new JMenuItem(action));
        }
        //增加音频菜单
        menuBar.add(audioMenu);

        videoMenu = new JMenu(resource("menu.video").name());
        videoMenu.setMnemonic(resource("menu.video").mnemonic());

        videoTrackMenu = new VideoTrackMenu().menu();

        videoMenu.add(videoTrackMenu);
        videoMenu.add(new JSeparator());
        videoMenu.add(new JCheckBoxMenuItem(videoFullscreenAction));
        videoMenu.add(new JCheckBoxMenuItem(videoAlwaysOnTopAction));
        videoMenu.add(new JSeparator());
        videoZoomMenu = new JMenu(resource("menu.video.item.zoom").name());
        videoZoomMenu.setMnemonic(resource("menu.video.item.zoom").mnemonic());
        // FIXME how to handle zoom 1:1 and fit to window - also, probably should not use addActions to select
        addActions(mediaPlayerActions.videoZoomActions(), videoZoomMenu/*, true*/);
        videoMenu.add(videoZoomMenu);
        videoAspectRatioMenu = new JMenu(resource("menu.video.item.aspectRatio").name());
        videoAspectRatioMenu.setMnemonic(resource("menu.video.item.aspectRatio").mnemonic());
        addActions(mediaPlayerActions.videoAspectRatioActions(), videoAspectRatioMenu, true);
        videoMenu.add(videoAspectRatioMenu);
        videoCropMenu = new JMenu(resource("menu.video.item.crop").name());
        videoCropMenu.setMnemonic(resource("menu.video.item.crop").mnemonic());
        addActions(mediaPlayerActions.videoCropActions(), videoCropMenu, true);
        videoMenu.add(videoCropMenu);
        videoMenu.add(new JSeparator());
        videoMenu.add(new JMenuItem(mediaPlayerActions.videoSnapshotAction()));
        videoMenu.add(new JSeparator());
        videoOutputMenu = new JMenu(resource("menu.video.item.output").name());
        videoOutputMenu.setMnemonic(resource("menu.video.item.output").mnemonic());
//        videoMenu.add(videoOutputMenu);
        //增加视频菜单
        menuBar.add(videoMenu);

        subtitleMenu = new JMenu(resource("menu.subtitle").name());
        subtitleMenu.setMnemonic(resource("menu.subtitle").mnemonic());
        subtitleMenu.add(new JMenuItem(subtitleAddSubtitleFileAction));

        subtitleTrackMenu = new SubtitleTrackMenu().menu();

        subtitleMenu.add(subtitleTrackMenu);
        //增加字幕菜单
        menuBar.add(subtitleMenu);

        toolsMenu = new JMenu(resource("menu.tools").name());
        toolsMenu.setMnemonic(resource("menu.tools").mnemonic());
        toolsMenu.add(new JMenuItem(toolsEffectsAction));
        toolsMenu.add(new JMenuItem(toolsMessagesAction));
        toolsMenu.add(new JSeparator());
        toolsMenu.add(new JMenuItem(toolsDebugAction));
        //增加工具菜单
        menuBar.add(toolsMenu);

        viewMenu = new JMenu(resource("menu.view").name());
        viewMenu.setMnemonic(resource("menu.view").mnemonic());
        viewMenu.add(new JCheckBoxMenuItem(viewStatusBarAction));
        //增加视图菜单
        menuBar.add(viewMenu);

        helpMenu = new JMenu(resource("menu.help").name());
        helpMenu.setMnemonic(resource("menu.help").mnemonic());
        helpMenu.add(new JMenuItem(helpAboutAction));
        //增加帮助菜单
        menuBar.add(helpMenu);
        //设置菜单栏
        setJMenuBar(menuBar);
        //定义视频组件
        videoContentPane = new VideoContentPane();

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(videoContentPane, BorderLayout.CENTER);
        contentPane.setTransferHandler(new MediaTransferHandler() {
            @Override
            protected void onMediaDropped(String[] uris) {
                application().mediaPlayer().media().play(uris[0]);
            }
        });

        setContentPane(contentPane);

        fileChooser = new JFileChooser();

        bottomPane = new JPanel();
        bottomPane.setLayout(new BorderLayout());

        JPanel bottomControlsPane = new JPanel();
        bottomControlsPane.setLayout(new MigLayout("fill, insets 0 n n n", "[grow]", "[]0[]"));

        positionPane = new PositionPane();
        bottomControlsPane.add(positionPane, "grow, wrap");

        controlsPane = new ControlsPane(mediaPlayerActions);
        bottomPane.add(bottomControlsPane, BorderLayout.CENTER);
        bottomControlsPane.add(controlsPane, "grow");

        statusBar = new StatusBar();
        bottomPane.add(statusBar, BorderLayout.SOUTH);

        contentPane.add(bottomPane, BorderLayout.SOUTH);

        MediaPlayerEventListener mediaPlayerEventListener = (new MediaPlayerEventAdapter() {

            @Override
            public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType type, int id) {
//                System.out.printf("ELEMENTARY STREAM ADDED %s %d%n", type, id);
            }

            @Override
            public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType type, int id) {
//                System.out.printf("ELEMENTARY STREAM SELECTED %s %d%n", type, id);
            }
            //正在播放事件
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        videoContentPane.showVideo();
                        application().post(PlayingEvent.INSTANCE);
                    }
                });
            }
            //暂停事件
            @Override
            public void paused(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        application().post(PausedEvent.INSTANCE);
                    }
                });
            }
            //停止事件
            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        videoContentPane.showDefault();
                        application().post(StoppedEvent.INSTANCE);
                    }
                });
            }
            //播放结束事件
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        if(limit%2 == 0){
                            //获取排名最高的电影
                            Connection connection = DataBaseUtils.getConnection(JDBC_URL, USERNAME, PASSWORD);
                            HashMap<String, Object> highFilm = DataBaseUtils.getHighFilm(connection);
                            //设置正在播放的电影 status = 0
                            DataBaseUtils.updatePlayingFilm(connection);
                            //设置排名最高的电影status = 1
                            DataBaseUtils.updateFilmStatus(connection,highFilm.get("id").toString());
                            videoContentPane.showVideo();
                            String mrl = highFilm.get("url").toString();
//                            String mrl = "C:\\Users\\Administrator\\Music\\MV\\test.mp4";
                            System.out.println(mrl);
                            application().addRecentMedia(mrl);
                            application().mediaPlayer().media().play(mrl);
                        }
                        limit++;

                    }
                });
            }
            //播放失败事件
            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        videoContentPane.showDefault();
                        application().post(StoppedEvent.INSTANCE);
                        File selectedFile = fileChooser.getSelectedFile(); // FIXME this is flawed with recent file menu
                        JOptionPane.showMessageDialog(MainFrame.this, MessageFormat.format(resources().getString("error.errorEncountered"), selectedFile != null ? selectedFile.getAbsolutePath() : ""), resources().getString("dialog.errorEncountered"), JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            //视频长度变化事件
            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        positionPane.setDuration(newLength);
                        statusBar.setDuration(newLength);
                    }
                });
            }
            //时间变化事件
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        positionPane.setTime(newTime);
                        statusBar.setTime(newTime);
                    }
                });
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            }
        });

        application().mediaPlayerComponent().mediaPlayer().events().addMediaPlayerEventListener(mediaPlayerEventListener);
        application().callbackMediaPlayerComponent().mediaPlayer().events().addMediaPlayerEventListener(mediaPlayerEventListener);

        getActionMap().put(ACTION_EXIT_FULLSCREEN, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                application().mediaPlayer().fullScreen().toggle();
                videoFullscreenAction.select(false);
            }
        });

        applyPreferences();

        setMinimumSize(new Dimension(370, 240));

//        setLogoAndMarquee(application().mediaPlayerComponent().mediaPlayer());
//        setLogoAndMarquee(application().callbackMediaPlayerComponent().mediaPlayer());
    }

    private ButtonGroup addActions(List<Action> actions, JMenu menu, boolean selectFirst) {
        ButtonGroup buttonGroup = addActions(actions, menu);
        if (selectFirst) {
            Enumeration<AbstractButton> en = buttonGroup.getElements();
            if (en.hasMoreElements()) {
                StandardAction action = (StandardAction) en.nextElement().getAction();
                action.select(true);
            }
        }
        return buttonGroup;
    }

    private ButtonGroup addActions(List<Action> actions, JMenu menu) {
        ButtonGroup buttonGroup = new ButtonGroup();
        for (Action action : actions) {
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(action);
            buttonGroup.add(menuItem);
            menu.add(menuItem);
        }
        return buttonGroup;
    }

    private void applyPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
        setBounds(
            prefs.getInt("frameX"     , 100),
            prefs.getInt("frameY"     , 100),
            prefs.getInt("frameWidth" , 800),
            prefs.getInt("frameHeight", 600)
        );
        boolean alwaysOnTop = prefs.getBoolean("alwaysOnTop", false);
        setAlwaysOnTop(alwaysOnTop);
        videoAlwaysOnTopAction.select(alwaysOnTop);
        boolean statusBarVisible = prefs.getBoolean("statusBar", false);
        statusBar.setVisible(statusBarVisible);
        viewStatusBarAction.select(statusBarVisible);
        fileChooser.setCurrentDirectory(new File(prefs.get("chooserDirectory", ".")));
        String recentMedia = prefs.get("recentMedia", "");
        if (recentMedia.length() > 0) {
            List<String> mrls = Arrays.asList(prefs.get("recentMedia", "").split("\\|"));
            Collections.reverse(mrls);
            for (String mrl : mrls) {
                application().addRecentMedia(mrl);
            }
        }
    }

    @Override
    protected void onShutdown() {
        if (wasShown()) {
            Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
            prefs.putInt    ("frameX"          , getX     ());
            prefs.putInt    ("frameY"          , getY     ());
            prefs.putInt    ("frameWidth"      , getWidth ());
            prefs.putInt    ("frameHeight"     , getHeight());
            prefs.putBoolean("alwaysOnTop"     , isAlwaysOnTop());
            prefs.putBoolean("statusBar"       , statusBar.isVisible());
            prefs.put       ("chooserDirectory", fileChooser.getCurrentDirectory().toString());

            String recentMedia;
            List<String> mrls = application().recentMedia();
            if (!mrls.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String mrl : mrls) {
                    if (sb.length() > 0) {
                        sb.append('|');
                    }
                    sb.append(mrl);
                }
                recentMedia = sb.toString();
            }
            else {
                recentMedia = "";
            }
            prefs.put("recentMedia", recentMedia);
        }
    }

    @Subscribe
    public void onBeforeEnterFullScreen(BeforeEnterFullScreenEvent event) {
        menuBar.setVisible(false);
        bottomPane.setVisible(false);
        // As the menu is now hidden, the shortcut will not work, so register a temporary key-binding
        registerEscapeBinding();
    }

    @Subscribe
    public void onAfterExitFullScreen(AfterExitFullScreenEvent event) {
        deregisterEscapeBinding();
        menuBar.setVisible(true);
        bottomPane.setVisible(true);
    }

    @Subscribe
    public void onSnapshotImage(SnapshotImageEvent event) {
        new SnapshotView(event.image());
    }

    @Subscribe
    public void onRendererAdded(RendererAddedEvent event) {
        synchronized (renderers) {
            renderers.add(event.rendererItem());
        }
        updateRenderersMenu();
    }

    @Subscribe
    public void onRendererDeleted(RendererDeletedEvent event) {
        synchronized (renderers) {
            for (RendererItem renderer : renderers) {
                if (renderer.rendererItemInstance().equals(event.rendererItem().rendererItemInstance())) {
                    renderers.remove(renderer);
                    break;
                }
            }
        }
        updateRenderersMenu();
    }

    private void updateRenderersMenu() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                synchronized (renderers) {
                    for (int i = 1; i < playbackRendererMenu.getItemCount(); i++) {
                        playbackRendererMenu.remove(i);
                    }

                    for (RendererItem renderer : renderers) {
                        playbackRendererMenu.add(new RendererAction(renderer.name(), renderer));
                    }
                }
            }
        });
    }

    private void registerEscapeBinding() {
        getInputMap().put(KEYSTROKE_ESCAPE, ACTION_EXIT_FULLSCREEN);
        getInputMap().put(KEYSTROKE_TOGGLE_FULLSCREEN, ACTION_EXIT_FULLSCREEN);
    }

    private void deregisterEscapeBinding() {
        getInputMap().remove(KEYSTROKE_ESCAPE);
        getInputMap().remove(KEYSTROKE_TOGGLE_FULLSCREEN);
    }

    private InputMap getInputMap() {
        JComponent c = (JComponent) getContentPane();
        return c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private ActionMap getActionMap() {
        JComponent c = (JComponent) getContentPane();
        return c.getActionMap();
    }

    private void setLogoAndMarquee(MediaPlayer mediaPlayer) {
        Logo logo = Logo.logo()
                .file("src/main/resources/vlcj-logo.png")
                .position(LogoPosition.TOP_LEFT)
                .opacity(0.5f)
                .enable();

        mediaPlayer.logo().set(logo);

        Marquee marquee = Marquee.marquee()
                .text(String.format("vlcj-player"))
                .colour(Color.white.getRGB())
                .size(20)
                .position(MarqueePosition.BOTTOM_RIGHT)
                .location(10,5)
                .enable();

        mediaPlayer.marquee().set(marquee);
    }

}
