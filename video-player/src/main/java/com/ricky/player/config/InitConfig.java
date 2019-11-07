package com.ricky.player.config;

import com.ricky.player.VlcjPlayer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.support.Info;

@Component
public class InitConfig implements ApplicationRunner {
    @Autowired
    private RabbitTemplate rabbitTemplate;
//    @Autowired
//    private VlcjPlayer player;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Info info = Info.getInstance();
        VlcjPlayer player = new VlcjPlayer();
        System.out.printf("vlcj             : %s%n", info.vlcjVersion() != null ? info.vlcjVersion() : "<version not available>");
        System.out.printf("os               : %s%n", player.val(info.os()));
        System.out.printf("java             : %s%n", player.val(info.javaVersion()));
        System.out.printf("java.home        : %s%n", player.val(info.javaHome()));
        System.out.printf("jna.library.path : %s%n", player.val(info.jnaLibraryPath()));
        System.out.printf("java.library.path: %s%n", player.val(info.javaLibraryPath()));
        System.out.printf("PATH             : %s%n", player.val(info.path()));
        System.out.printf("VLC_PLUGIN_PATH  : %s%n", player.val(info.pluginPath()));

        if (RuntimeUtil.isNix()) {
            System.out.printf("LD_LIBRARY_PATH  : %s%n", player.val(info.ldLibraryPath()));
        } else if (RuntimeUtil.isMac()) {
            System.out.printf("DYLD_LIBRARY_PATH          : %s%n", player.val(info.dyldLibraryPath()));
            System.out.printf("DYLD_FALLBACK_LIBRARY_PATH : %s%n", player.val(info.dyldFallbackLibraryPath()));
        }

        player.setLookAndFeel();
        player.start();
    }
}
