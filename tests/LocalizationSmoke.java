package gdworld.test;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.*;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.core.backend.*;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.service.AuthService;
import pro.gravit.launcher.gui.components.LoadingGames;
import pro.gravit.launcher.core.api.model.SelfUser;
import com.google.gson.JsonParser;

/** No account credentials or network. Exercises real locale bundles, FXML and native UI. */
public class LocalizationSmoke extends Application {
    static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    static Object field(Object target,String name)throws Exception{var f=target.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(target);}
    @Override public void start(Stage stage)throws Exception{
        Launcher.setConfig(new LauncherConfig("fixture",null,null,Map.of(),"Fixture"));
        LauncherBackendAPIHolder.setApi((LauncherBackendAPI)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{LauncherBackendAPI.class},(p,m,a)->null));
        var app=new JavaFXApplication();app.authService=new AuthService(app);
        SelfUser user=(SelfUser)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{SelfUser.class},(p,m,a)->m.getName().equals("getUsername")?"Fixture":null);
        app.authService.setUser(user);
        var instance=JavaFXApplication.class.getDeclaredField("INSTANCE");instance.setAccessible(true);
        ((AtomicReference<JavaFXApplication>)instance.get(null)).set(app);
        var resources=JavaFXApplication.class.getDeclaredField("resources");resources.setAccessible(true);
        Path runtime=Path.of(getParameters().getRaw().getFirst());
        Properties canonical=new Properties();try(var in=Files.newInputStream(runtime.resolve("runtime_en.properties"))){canonical.load(in);}
        for(String lang:List.of("en","ru","uk","be","pl")) {
            ResourceBundle words;try(var in=Files.newInputStream(runtime.resolve("runtime_"+lang+".properties"))){words=new PropertyResourceBundle(in);}
            resources.set(app,words);
            for(String key:canonical.stringPropertyNames())if(key.startsWith("gdworld.")) {
                check(words.containsKey(key)&&!words.getString(key).isBlank(),lang+" missing "+key);
                check(canonical.getProperty(key).split("%s",-1).length==words.getString(key).split("%s",-1).length,lang+" placeholders "+key);
            }
            Pane root=FXMLLoader.load(runtime.resolve("scenes/update/update.fxml").toUri().toURL(),words);
            root.setStyle("-fx-font-family:'System';-fx-colors-text:#fff5ce;-fx-colors-text-secondary:#bbb4a0;-fx-glass:#232528;-fx-colors-baseG:#383328;-fx-red:#f28c82;");
            stage.setScene(new Scene(root,930,560));stage.show();
            var games=(LoadingGames)root.lookup("#loadingGames");
            var start=LoadingGames.class.getDeclaredMethod("startGame",int.class);start.setAccessible(true);
            for(int game=0;game<3;game++) {
                start.invoke(games,game);root.applyCss();root.layout();
                check(((Label)field(games,"title")).getText().equals(words.getString("gdworld."+new String[]{"snake","mines","memory"}[game])),lang+" game title");
                var controls=(HBox)field(games,"controls");
                check(controls.getBoundsInParent().getMaxX()<=games.getWidth()+1,lang+" controls container overflow");
                for(Node n:controls.getChildren())if(n.isManaged())check(n.getBoundsInParent().getMaxX()<=controls.getWidth()+1,lang+" button overflow: "+n);
                Label help=(Label)field(games,"help");
                var text=(javafx.scene.text.Text)help.lookup(".text");
                check(text.getText().equals(help.getText()),lang+" instruction truncation: "+text.getText());
                if(game==0) {
                    ((Button)field(games,"pause")).fire();
                    check(((Button)field(games,"pause")).getText().equals(words.getString("gdworld.resume")),lang+" pause label");
                }
            }
            LoadingGames.fail(root);check(((Label)field(games,"help")).getText().equals(words.getString("gdworld.loadInterrupted")),lang+" download failure");
            // Reflection is used only for testing private cabinet composition without HTTP.
            Class<?> cabinet=Class.forName("pro.gravit.launcher.gui.components.NativeCabinet");
            var constructor=cabinet.getDeclaredConstructors()[0];constructor.setAccessible(true);
            Object view=constructor.newInstance(app,stage);
            var render=cabinet.getDeclaredMethod("render",com.google.gson.JsonObject.class);render.setAccessible(true);
            var profile=JsonParser.parseString("{\"username\":\"Fixture\",\"uuid\":\"fixture-uuid\",\"textures\":{},\"links\":{}}").getAsJsonObject();
            render.invoke(view,profile);render.invoke(view,profile);
            Dialog<?> dialog=(Dialog<?>)field(view,"dialog");dialog.show();dialog.getDialogPane().applyCss();dialog.getDialogPane().layout();
            check(dialog.getTitle().equals(words.getString("gdworld.cabinet")+" — GDWorld"),lang+" cabinet title");
            var labels=dialog.getDialogPane().lookupAll(".button").stream().filter(n->n instanceof Button).map(n->((Button)n).getText()).toList();
            check(labels.containsAll(List.of(words.getString("gdworld.uploadSkin"),words.getString("gdworld.uploadCape"),words.getString("gdworld.close"))),lang+" cabinet actions");
            javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(dialog.getDialogPane().snapshot(null,null),null),"png",new java.io.File("/tmp/gdworld-cabinet-"+lang+".png"));
            dialog.close();
            System.out.println("PASS "+lang+": complete keys/placeholders; 3 games, hints, controls, pause/error, native cabinet and upload labels");
        }
        stage.close();Platform.exit();
    }
    public static void main(String[]args){launch(args);}
}
