import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.core.backend.LauncherBackendAPI;
import pro.gravit.launcher.core.backend.LauncherBackendAPIHolder;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.service.AuthService;
import pro.gravit.launcher.core.api.model.SelfUser;
import pro.gravit.launcher.gui.components.NativeCabinet;
import java.lang.reflect.*;
import java.util.Map;
import javafx.scene.control.Dialog;
import javafx.scene.control.Button;

/** No network or real account: exercise the actual JavaFX dashboard with fixture data. */
public class NativeCabinetSmoke extends Application {
    @Override public void start(Stage stage) throws Exception {
        Launcher.setConfig(new LauncherConfig("fixture", null, null, Map.of(), "Fixture"));
        LauncherBackendAPIHolder.setApi((LauncherBackendAPI) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{LauncherBackendAPI.class},(p,m,a)->null));
        var app = new JavaFXApplication();
        app.authService = new AuthService(app);
        SelfUser user = (SelfUser) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{SelfUser.class},(p,m,a)->m.getName().equals("getUsername") ? "Best_Jaro" : null);
        app.authService.setUser(user);
        stage.setScene(new Scene(new StackPane(),100,60));stage.show();
        Constructor<?> constructor = NativeCabinet.class.getDeclaredConstructors()[0];constructor.setAccessible(true);
        var view = constructor.newInstance(app,stage);
        Method render=NativeCabinet.class.getDeclaredMethod("render",com.google.gson.JsonObject.class);render.setAccessible(true);
        var profile=JsonParser.parseString("{\"username\":\"Best_Jaro\",\"uuid\":\"1920377f-131d-41b0-84d5-85580043bc09\",\"textures\":{},\"links\":{},\"providers\":{\"microsoft\":false,\"littleskin\":false}}").getAsJsonObject();
        render.invoke(view,profile);
        // Re-render ensures the reusable slim selector can be attached again safely.
        render.invoke(view,profile);
        Field field=NativeCabinet.class.getDeclaredField("dialog");field.setAccessible(true);
        Dialog<?> dialog=(Dialog<?>)field.get(view);dialog.show();
        javafx.animation.PauseTransition pulse = new javafx.animation.PauseTransition(javafx.util.Duration.millis(350));
        pulse.setOnFinished(event -> {
            try {
                dialog.getDialogPane().applyCss();dialog.getDialogPane().layout();
                var image=dialog.getDialogPane().snapshot(null,null);
                if(image.getHeight()<500)throw new AssertionError("Dashboard did not receive usable height");
                javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image,null),"png",new java.io.File("/tmp/gdworld-native-cabinet.png"));
                long buttons=dialog.getDialogPane().lookupAll(".button").stream().filter(n->n instanceof Button b && java.util.Set.of(pro.gravit.launcher.gui.components.GdWorldI18n.t("uploadSkin"), pro.gravit.launcher.gui.components.GdWorldI18n.t("uploadCape")).contains(b.getText())).count();
                if(buttons!=2)throw new AssertionError("Missing upload controls");
                app.authService.exit();
                if(dialog.isShowing())throw new AssertionError("Cabinet remained open after account exit");
                System.out.println("PASS: native JavaFX dashboard rendered twice, both upload controls present, dashboard closed on account exit");
            } catch(Exception e) {e.printStackTrace();System.exit(1);}
            finally {Platform.exit();}
        });
        pulse.play();
    }
    public static void main(String[] args){launch(args);}
}
