package pro.gravit.launcher.gui.components;

import static pro.gravit.launcher.gui.components.GdWorldI18n.t;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.core.api.features.TextureUploadFeatureAPI;
import pro.gravit.launcher.core.backend.LauncherBackendAPIHolder;
import pro.gravit.launcher.core.backend.extensions.TextureUploadExtension;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.scenes.interfaces.SceneSupportUserBlock;

import java.io.File;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Native JavaFX account dashboard; no WebView, browser navigation or extra password. */
public final class NativeCabinet {
    private static final String BASE = "https://gd-network.ddns.net/cabinet/api";
    private final JavaFXApplication app;
    private final TextureUploadExtension upload;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
    private final Dialog<Void> dialog = new Dialog<>();
    private final VBox content = new VBox(18);
    private final Label status = new Label(t("loadingProfile"));
    private final VBox details = new VBox(18);
    private final CheckBox slim = new CheckBox(t("slim"));
    private final java.util.List<Button> actions = new java.util.ArrayList<>();
    private boolean busy;
    private boolean manualSlim = true;

    private NativeCabinet(JavaFXApplication app, Window owner) {
        this.app = app;
        upload = LauncherBackendAPIHolder.getApi().getExtension(TextureUploadExtension.class);
        dialog.initOwner(owner);
        dialog.setTitle(t("cabinet")+" — GDWorld");
        dialog.setResizable(true);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE)).setText(t("close"));
        dialog.getDialogPane().setStyle("-fx-background-color:#18191c;-fx-font-family:'System';-fx-font-size:14px;");
        content.setPadding(new Insets(22));
        Label heading = text(t("cabinet"), 26, "#fff5ce");
        status.setWrapText(true);
        status.setStyle("-fx-text-fill:#fcd12a;");
        content.getChildren().addAll(heading, status, details);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportWidth(730);
        scroll.setPrefViewportHeight(570);
        scroll.setStyle("-fx-background:#18191c;-fx-background-color:#18191c;");
        dialog.getDialogPane().setContent(scroll);
        dialog.getDialogPane().setPrefSize(790, 680);
        dialog.getDialogPane().setMinSize(660, 520);
        javafx.beans.value.ChangeListener<pro.gravit.launcher.core.api.model.SelfUser> accountListener = (observable, previous, current) -> {
            if (current != previous && dialog.isShowing()) dialog.close();
        };
        app.authService.user.addListener(accountListener);
        dialog.setOnHidden(e -> app.authService.user.removeListener(accountListener));
    }

    public static void open(JavaFXApplication app, Window owner) {
        if (app.authService.user.get() == null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION, t("loginFirst"), ButtonType.OK);
            info.setHeaderText(t("cabinet"));
            ((Button) info.getDialogPane().lookupButton(ButtonType.OK)).setText("OK");
            info.initOwner(owner);
            info.show();
            return;
        }
        var cabinet = new NativeCabinet(app, owner);
        cabinet.dialog.show();
        cabinet.refresh();
    }

    public static boolean openForUrl(JavaFXApplication app, String url) {
        if ("https://gd-network.ddns.net/cabinet/".equals(url)) {
            open(app, app.getMainStage().getStage());
            return true;
        }
        return app.openURL(url);
    }

    private static Label text(String value, int size, String color) {
        Label label = new Label(value);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill:"+color+";-fx-font-size:"+size+"px;");
        return label;
    }

    private Button button(String label, Runnable action, boolean primary) {
        Button b = new Button(label);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(primary ? "-fx-background-color:#ffd300;-fx-text-fill:#302407;-fx-padding:10 18;-fx-background-radius:7;" : "-fx-background-color:#353128;-fx-text-fill:#f5f0df;-fx-padding:9 16;-fx-background-radius:7;");
        b.setOnAction(e -> action.run());
        actions.add(b);
        return b;
    }

    private void busy(boolean value) {
        busy = value;
        actions.forEach(b -> b.setDisable(value));
        slim.setDisable(value);
    }

    private CompletableFuture<JsonObject> api(String path, boolean post) {
        // Capture the current account token at dispatch, never write it into the UI or logs.
        String token = Request.getAccessToken();
        return CompletableFuture.supplyAsync(() -> {
            if (token == null || token.isBlank()) throw new CompletionException(new UiFailure(t("noSession")));
            var builder = HttpRequest.newBuilder(URI.create(BASE + path)).timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + token).header("Accept", "application/json");
            if (post) builder.POST(HttpRequest.BodyPublishers.noBody());
            try {
                var r = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                if (r.statusCode() == 401) throw new UiFailure(t("expired"));
                if (r.statusCode() != 200) throw new UiFailure(t("serviceError",r.statusCode()));
                return Launcher.gsonManager.gson.fromJson(r.body(), JsonObject.class);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException(new UiFailure(t("requestCancelled")));
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    private void refresh() {
        busy(true);
        // A normal Gravit request lets its backend refresh the launcher session if needed.
        CompletableFuture<?> ready = upload == null ? CompletableFuture.completedFuture(null) : upload.fetchTextureUploadInfo();
        ready.thenCompose(info -> {
            if (info instanceof TextureUploadFeatureAPI.TextureUploadInfo uploadInfo) manualSlim = uploadInfo.isRequireManualSlimSkinSelect();
            return api("/me", false);
        }).whenComplete((profile, error) -> Platform.runLater(() -> {
            if (!dialog.isShowing()) return;
            busy(false);
            if (error != null) { failure(error); return; }
            render(profile);
            status.setText(t("saved"));
        }));
    }

    private void render(JsonObject profile) {
        actions.clear();
        details.getChildren().clear();
        Label name = text(profile.get("username").getAsString(), 22, "#fff5ce");
        Label id = text("UUID: " + profile.get("uuid").getAsString(), 12, "#c8bea2");
        details.getChildren().add(new VBox(5, name, id));
        var textures = profile.getAsJsonObject("textures");
        boolean isSlim = textures.has("SKIN") && textures.getAsJsonObject("SKIN").getAsJsonObject("metadata").get("model").getAsString().equals("slim");
        slim.setSelected(isSlim);
        slim.setStyle("-fx-text-fill:#e9dfc5;");
        HBox cards = new HBox(18);
        cards.getChildren().addAll(textureCard("SKIN", t("skin"), textures), textureCard("CAPE", t("cape"), textures));
        details.getChildren().add(cards);
        details.getChildren().add(text(t("reconnect"), 13, "#c8bea2"));
        VBox links = new VBox(10, text(t("links"), 19, "#fff5ce"));
        JsonObject linked = profile.getAsJsonObject("links");
        for (String provider : new String[]{"microsoft", "littleskin"}) {
            String label = provider.equals("microsoft") ? "Microsoft" : "LittleSkin";
            boolean connected = linked.has(provider);
            links.getChildren().add(text(label + (connected ? ": " + linked.get(provider).getAsString() : t("notLinked")), 14, connected ? "#ffd300" : "#e0d9c4"));
        }
        links.getChildren().add(text(t("providersPending"), 13, "#c8bea2"));
        details.getChildren().addAll(new Separator(), links, button(t("refresh"), this::refresh, false));
    }

    private VBox textureCard(String kind, String title, JsonObject textures) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:#242529;-fx-background-radius:12;");
        card.setPrefWidth(320);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.getChildren().add(text(title, 19, "#fff5ce"));
        StackPane preview = new StackPane();
        preview.setPrefHeight(135);
        preview.setStyle("-fx-background-color:#141417;-fx-background-radius:8;");
        if (textures.has(kind)) {
            String url = textures.getAsJsonObject(kind).get("url").getAsString();
            // Only our own immutable textures are loaded by the native image renderer.
            if (url.startsWith("https://gd-network.ddns.net/cabinet/textures/") || url.startsWith("https://gd-network.ddns.net/cms-api/")) {
                ImageView image = new ImageView(new Image(url, true));
                image.setFitWidth(122); image.setFitHeight(122); image.setPreserveRatio(true); image.setSmooth(false);
                preview.getChildren().add(image);
            }
        } else preview.getChildren().add(text(t("noTexture"), 14, "#c8bea2"));
        card.getChildren().addAll(preview, text(kind.equals("SKIN") ? t("skinFormat") : t("capeFormat"), 12, "#c8bea2"));
        if (kind.equals("SKIN")) card.getChildren().add(manualSlim ? slim : text(t("autoModel"), 12, "#c8bea2"));
        else { Region spacer = new Region(); spacer.setPrefHeight(20); card.getChildren().add(spacer); }
        Button pick = button(t(kind.equals("SKIN") ? "uploadSkin" : "uploadCape"), () -> choose(kind), true);
        if (upload == null) pick.setDisable(true);
        card.getChildren().add(pick);
        if (textures.has(kind)) card.getChildren().add(button(t(kind.equals("SKIN") ? "resetSkin" : "resetCape"), () -> remove(kind), false));
        return card;
    }

    private void choose(String kind) {
        if (busy || upload == null) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle(kind.equals("SKIN") ? t("chooseSkin") : t("chooseCape"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png", "*.PNG"));
        File file = chooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
        if (file == null) return;
        busy(true);
        status.setText(t(kind.equals("SKIN") ? "uploadingSkin" : "uploadingCape"));
        boolean modelSlim = slim.isSelected();
        CompletableFuture.supplyAsync(() -> {
            try {
                if (Files.size(file.toPath()) > 1048576) throw new UiFailure(t("fileSize"));
                return Files.readAllBytes(file.toPath());
            } catch (Exception e) { throw new CompletionException(e); }
        }).thenCompose(bytes -> upload.uploadTexture(kind, bytes, new TextureUploadFeatureAPI.UploadSettings(modelSlim)))
          .whenComplete((texture, error) -> Platform.runLater(() -> {
            if (!dialog.isShowing()) return;
            if (error != null) { busy(false); failure(error); return; }
            if (kind.equals("SKIN")) {
                app.skinManager.addOrReplaceSkin(app.authService.getUsername(), URI.create(texture.getUrl()));
                for (var component : app.gui.getComponents()) {
                    if (component.isInit() && component instanceof SceneSupportUserBlock support) support.getUserBlock().resetAvatar();
                }
            }
            refresh();
          }));
    }

    private void remove(String kind) {
        if (busy) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, t(kind.equals("SKIN") ? "confirmSkin" : "confirmCape"), ButtonType.YES, ButtonType.CANCEL);
        confirm.initOwner(dialog.getDialogPane().getScene().getWindow());
        confirm.setHeaderText(t("resetAppearance"));
        ((Button) confirm.getDialogPane().lookupButton(ButtonType.YES)).setText(t("yes"));
        ((Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(t("cancel"));
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.YES) return;
        busy(true);
        api("/remove/" + kind, true).whenComplete((value, error) -> Platform.runLater(() -> {
            if (!dialog.isShowing()) return;
            if (error != null) { busy(false); failure(error); return; }
            refresh();
        }));
    }

    private static final class UiFailure extends RuntimeException {
        UiFailure(String message) { super(message); }
    }

    private void failure(Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null) cause = cause.getCause();
        // Do not expose request dumps or bearer tokens in error messages.
        String message = cause.getMessage();
        if (!(cause instanceof UiFailure) || message == null || message.length() > 200 || message.contains("Bearer") || message.contains("http")) message = t("connectionError");
        status.setText(message);
        if (details.getChildren().isEmpty()) details.getChildren().add(button(t("retry"), this::refresh, false));
    }
}
