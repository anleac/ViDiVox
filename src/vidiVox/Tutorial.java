package vidiVox;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.media.callback.seekable.RandomAccessFileMedia;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Tutorial {

    private final JFrame frame;

    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

    public static void main(String[] args) {
        NativeDiscovery nd = new NativeDiscovery();
        System.out.println(nd.discover());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
                new Tutorial();
            }
        });
    }

    public Tutorial() {
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        frame.setContentPane(mediaPlayerComponent);
        frame.setVisible(true);
        File f = new File("/home/nick/Documents/SOFTENG_206/A3/sample_video_big_buck_bunny_1_minute.avi");
        mediaPlayerComponent.getMediaPlayer().playMedia("/home/nick/Documents/SOFTENG_206/A3/206_A3/sample_video_big_buck_bunny_1_minute.avi");
    }
}