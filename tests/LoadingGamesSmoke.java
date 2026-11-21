package gdworld.test;

import pro.gravit.launcher.gui.components.LoadingGames;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;

/** Actual JavaFX/FXML rendering; fixture download text and isolated preferences, no network/account. */
public class LoadingGamesSmoke extends Application {
    Pane root;
    LoadingGames games;
    Stage stage;
    ProgressBar progress;
    static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    static Object field(Object object,String name) throws Exception {Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);}
    void game(int choice) throws Exception {var method=LoadingGames.class.getDeclaredMethod("startGame",int.class);method.setAccessible(true);method.invoke(games,choice);}
    @Override public void start(Stage stage) throws Exception {
        this.stage=stage;
        Path runtime=Path.of(getParameters().getRaw().getFirst());
        ResourceBundle words;
        if(Files.exists(runtime.resolve("runtime_ru.properties"))) {
            try(var reader=Files.newBufferedReader(runtime.resolve("runtime_ru.properties"))) {words=new PropertyResourceBundle(reader);}
        } else words=new ListResourceBundle(){protected Object[][] getContents(){return new Object[][]{{"runtime.scenes.update.headingDialog","Запуск Minecraft"}};}};
        // Match the launcher's selected bundle rather than the host OS locale.
        pro.gravit.launcher.base.Launcher.setConfig(new pro.gravit.launcher.base.LauncherConfig("fixture",null,null,Map.of(),"Fixture"));
        var fixtureApp=new pro.gravit.launcher.gui.core.JavaFXApplication();
        var instance=pro.gravit.launcher.gui.core.JavaFXApplication.class.getDeclaredField("INSTANCE");instance.setAccessible(true);
        ((java.util.concurrent.atomic.AtomicReference<pro.gravit.launcher.gui.core.JavaFXApplication>)instance.get(null)).set(fixtureApp);
        var resources=pro.gravit.launcher.gui.core.JavaFXApplication.class.getDeclaredField("resources");resources.setAccessible(true);resources.set(fixtureApp,words);
        root=FXMLLoader.load(runtime.resolve("scenes/update/update.fxml").toUri().toURL(),words);
        root.setStyle("-fx-font-family:'System';-fx-colors-text:#f7f3e8;-fx-colors-text-secondary:#bbb4a0;-fx-glass:#232528;-fx-colors-baseG:#383328;-fx-red:#f28c82;-fx-background-color:#141518;");
        progress=(ProgressBar)root.lookup("#progress");progress.setProgress(.42);
        ((TextArea)root.lookup("#outputUpdate")).setText("[ Обновление клиента ]\nПроверка файлов\nСравнение файлов\nЗагрузка модификаций…\n\n[ Обновление ассетов ]\nПодготовка ресурсов Minecraft");
        ((Label)root.lookup("#speed")).setText("18.4");((Label)root.lookup("#speed-text")).setText("МБ/с");((Label)root.lookup("#volume")).setText("248 / 590 МБ");
        games=(LoadingGames)root.lookup("#loadingGames");
        for(String id:List.of("speed","volume","speed-text","speedErr"))check(root.lookup("#"+id) instanceof Label,"Missing downloader label "+id);
        check(root.lookup("#speedOn") instanceof Pane&&root.lookup("#cancel") instanceof Button,"Downloader control contract changed");
        Scene scene=new Scene(root,930,560);stage.setTitle("GDWorld — тест мини-игр загрузки");stage.setScene(scene);stage.setResizable(false);stage.show();
        game(0);
        later(400,()->capture(0));
    }
    interface Step { void run() throws Exception; }
    void later(int delay,Step step){PauseTransition wait=new PauseTransition(Duration.millis(delay));wait.setOnFinished(e->{try{step.run();}catch(Throwable t){t.printStackTrace();System.exit(1);}});wait.play();}
    void capture(int index) throws Exception {
        root.applyCss();root.layout();
        var controls=(HBox)field(games,"controls");
        check(controls.getBoundsInParent().getMaxY()<=games.getHeight()+1,"Game controls clipped: "+controls.getBoundsInParent().getMaxY()+" > "+games.getHeight());
        check(games.getBoundsInParent().getMaxY()<=games.getParent().getLayoutBounds().getHeight()+1,"Game card clipped");
        Label instructions=(Label)field(games,"help");
        String painted=((javafx.scene.text.Text)instructions.lookup(".text")).getText();
        check(painted.contains(new String[]{"Пробел","Первый","Enter"}[index]),"Instruction second line clipped: "+painted);
        javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(root.snapshot(null,null),null),"png",new File("/tmp/gdworld-loading-"+new String[]{"snake","mines","memory"}[index]+".png"));
        if(index<2){game(index+1);later(120,()->capture(index+1));return;}
        verifyLifecycle();
    }
    void verifyLifecycle() throws Exception {
        Canvas canvas=(Canvas)field(games,"board");Timeline loop=(Timeline)field(games,"loop");
        game(1);
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.F,false,false,false,false));
        check(((Label)field(games,"score")).getText().startsWith("Мин: 9"),"Mines flag input failed");
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.F,false,false,false,false));
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.ENTER,false,false,false,false));
        check(!((Label)field(games,"score")).getText().contains("Открыто: 0/"),"Mines open input failed");
        game(2);
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.ENTER,false,false,false,false));
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.RIGHT,false,false,false,false));
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.ENTER,false,false,false,false));
        check(((Label)field(games,"score")).getText().contains("Попыток: 1"),"Memory selection input failed");
        game(0);check(loop.getStatus()==Animation.Status.STOPPED,"Snake must wait for input");
        Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.UP,false,false,false,false));
        check(loop.getStatus()==Animation.Status.RUNNING,"Keyboard did not start snake");
        ((Button)field(games,"pause")).fire();check(loop.getStatus()==Animation.Status.STOPPED,"Pause button failed");
        ((Button)field(games,"pause")).fire();check(loop.getStatus()==Animation.Status.RUNNING,"Resume button failed");
        root.setVisible(false);check(loop.getStatus()==Animation.Status.STOPPED,"Game running behind hidden scene");
        root.setVisible(true);check(loop.getStatus()==Animation.Status.RUNNING,"Game failed to resume");
        stage.hide();check(loop.getStatus()==Animation.Status.STOPPED,"Game running after window hide");stage.show();
        LoadingGames.begin(root);int previous=(int)field(games,"game");
        for(int i=0;i<15;i++){LoadingGames.begin(root);int next=(int)field(games,"game");check(next!=previous,"Visit repeated previous game");previous=next;}
        game(0);Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.RIGHT,false,false,false,false));
        LoadingGames.fail(root);check(loop.getStatus()==Animation.Status.STOPPED,"Animation did not stop for download error");
        check(((HBox)field(games,"controls")).isDisabled(),"Game remained interactive during download error");
        check(progress.getProgress()==.42&&((TextArea)root.lookup("#outputUpdate")).getText().contains("Загрузка модификаций"),"Minigame changed downloader state");
        game(0);Event.fireEvent(canvas,new KeyEvent(KeyEvent.KEY_PRESSED,"","",KeyCode.RIGHT,false,false,false,false));
        stage.setScene(new Scene(new StackPane(),200,100));check(loop.getStatus()==Animation.Status.STOPPED,"Timeline leaked after scene replacement");
        System.out.println("PASS: production FXML contract, all 3 games rendered without clipping, keyboard input, non-repeating visits, hidden/window/error/scene lifecycle, downloader state preserved");
        stage.close();Platform.exit();
    }
    public static void main(String[] args){launch(args);}
}
