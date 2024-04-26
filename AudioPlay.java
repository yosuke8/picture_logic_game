import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class AudioPlay {
    Clip clip;
    AudioInputStream AudioIS;
    FloatControl ctrl;

    public AudioPlay(String s) {
        try {
            ClassLoader cl = this.getClass().getClassLoader(); //jarファイルで動作させるため追加
            //AudioIS = AudioSystem.getAudioInputStream(new File(s)); //wavファイル取得
            AudioIS = AudioSystem.getAudioInputStream(cl.getResource(s)); //jarファイルで動作させるために書き換え
            clip = AudioSystem.getClip();
            clip.open(AudioIS);
            FloatControl ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            ctrl.setValue((float)Math.log10(0.3) * 20); //音量調整
        } 
        catch (MalformedURLException e)         {e.printStackTrace();} //例外処理
        catch (UnsupportedAudioFileException e) {e.printStackTrace();} 
        catch (IOException e)                   {e.printStackTrace();}
        catch (LineUnavailableException e)      {e.printStackTrace();}
    }
}
