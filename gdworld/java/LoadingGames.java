package pro.gravit.launcher.gui.components;

import static pro.gravit.launcher.gui.components.GdWorldI18n.t;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.*;
import java.util.prefs.Preferences;

/** Native, offline loading entertainment. It never participates in download completion. */
public final class LoadingGames extends VBox {
    private static final String[] NAMES = {"snake", "mines", "memory"};
    private static final String GOLD = "#ffd300", LIGHT = "#fff5ce", MUTED = "#bbb4a0";
    private static final String BUTTON_STYLE = "-fx-background-color:#383328;-fx-text-fill:#fff5ce;-fx-background-radius:7;-fx-border-color:#665321;-fx-border-radius:7;-fx-padding:5 7;-fx-font-family:'System';-fx-font-size:12px;";
    private static int processLast = -1;
    private final Random random;
    private final Preferences preferences;
    private final Canvas board = new Canvas(280, 280);
    private final Label title = label(t("mini"), 21, LIGHT);
    private final Label score = label(t("preparing"), 13, MUTED);
    private final Label help = label(t("waiting"), 12, MUTED);
    private final Button restart = button(t("restart"), t("restartTip"));
    private final Button pause = button(t("pause"), t("pauseTip"));
    private final Button flag = button(t("flag"), t("flagTip"));
    private final HBox directions = new HBox(3);
    private final HBox controls = new HBox(4);
    private final Timeline loop;
    private final List<Observable> ancestors = new ArrayList<>();
    private final InvalidationListener visibilityListener = ignored -> updateActivity();
    private final InvalidationListener parentListener = ignored -> observeAncestors();
    private final InvalidationListener windowListener = ignored -> observeWindow();
    private Scene observedScene;
    private Window observedWindow;
    private LoadingGameModels.Snake snake;
    private LoadingGameModels.Mines mines;
    private LoadingGameModels.Memory memory;
    private int game = -1, selectedX, selectedY;
    private boolean paused, failed, flagMode, snakeStarted;
    private double accumulator;
    private long lastPulse;

    public LoadingGames() { this(new Random(), safePreferences()); }

    LoadingGames(Random random, Preferences preferences) {
        this.random = random; this.preferences = preferences;
        setId("loadingGames"); setSpacing(4); setPadding(new Insets(14)); setAlignment(Pos.TOP_CENTER);
        setMinWidth(340); setPrefWidth(340); setMaxWidth(340); setMinHeight(420);
        setStyle("-fx-background-color:#1e2023;-fx-background-radius:18;-fx-border-color:#665321;-fx-border-radius:18;-fx-border-width:1;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = label("GDWORLD", 10, "#daa520");
        HBox heading = new HBox(8, title, spacer, badge); heading.setAlignment(Pos.CENTER_LEFT);
        heading.setMinHeight(26); score.setMinHeight(19); score.setMaxWidth(Double.MAX_VALUE);
        help.setWrapText(true); help.setMinHeight(40); help.setPrefHeight(40); help.setMaxHeight(40); help.setMaxWidth(Double.MAX_VALUE);
        StackPane frame = new StackPane(board); frame.setMaxSize(280,280); frame.setMinSize(280,280);
        board.setFocusTraversable(true); board.setAccessibleRole(AccessibleRole.NODE);
        board.focusedProperty().addListener(ignored -> draw());
        controls.setAlignment(Pos.CENTER_LEFT); controls.setMinHeight(29);
        directions.getChildren().addAll(direction("↑",0,-1), direction("←",-1,0), direction("↓",0,1), direction("→",1,0));
        restart.setOnAction(e -> { if (!failed && game >= 0) { startGame(game); board.requestFocus(); } });
        pause.setOnAction(e -> togglePause());
        flag.setOnAction(e -> { flagMode = !flagMode; refreshText(); board.requestFocus(); });
        controls.getChildren().addAll(restart, pause, directions, flag);
        getChildren().addAll(heading, score, frame, help, controls);
        board.setOnMousePressed(e -> {
            board.requestFocus();
            e.consume();
            if (failed || paused || game < 0) return;
            if(game==0)snakeStarted=true;
            int x=(int)((e.getX()-8)/(264.0/9)), y=(int)((e.getY()-8)/(264.0/9));
            if (game==1 && x>=0 && y>=0 && x<9 && y<9 && e.getX()>=8 && e.getY()>=8) {
                selectedX=x; selectedY=y;
                if (e.getButton()==MouseButton.SECONDARY || flagMode && e.getButton()==MouseButton.PRIMARY) mines.flag(x,y);
                else if (e.getButton()==MouseButton.PRIMARY) mines.reveal(x,y);
            } else if (game==2 && e.getButton()==MouseButton.PRIMARY) {
                int column=(int)((e.getX()-8)/66), row=(int)((e.getY()-8)/88);
                if (e.getX()>=8 && e.getY()>=8 && column>=0 && column<4 && row>=0 && row<3) {
                    selectedX=column;selectedY=row;memory.reveal(row*4+column);
                }
            }
            refreshText(); draw(); updateActivity();
        });
        board.setOnMouseDragged(e -> e.consume());
        board.setOnKeyPressed(e -> {
            if (e.getCode()==KeyCode.SPACE) { togglePause(); e.consume(); return; }
            if (failed || paused || game<0) return;
            int dx=0,dy=0;
            switch (e.getCode()) {
                case LEFT, A -> dx=-1; case RIGHT, D -> dx=1; case UP, W -> dy=-1; case DOWN, S -> dy=1;
                default -> { }
            }
            if (dx!=0 || dy!=0) {
                if (game==0) {snake.turn(dx,dy);snakeStarted=true;updateActivity();}
                else {selectedX=Math.max(0,Math.min(game==1?8:3,selectedX+dx));selectedY=Math.max(0,Math.min(game==1?8:2,selectedY+dy));draw();}
                e.consume();
            } else if (game==1 && e.getCode()==KeyCode.F) {mines.flag(selectedX,selectedY);refreshText();draw();e.consume();}
            else if (e.getCode()==KeyCode.ENTER && game!=0) {
                if(game==1) {if(flagMode)mines.flag(selectedX,selectedY);else mines.reveal(selectedX,selectedY);}
                else memory.reveal(selectedY*4+selectedX);
                refreshText();draw();updateActivity();e.consume();
            }
        });
        loop = new Timeline(new KeyFrame(Duration.millis(40), e -> pulse()));
        loop.setCycleCount(Timeline.INDEFINITE);
        sceneProperty().addListener((o, old, current) -> {
            if (observedScene!=null) observedScene.windowProperty().removeListener(windowListener);
            observedScene=current;
            if (current!=null) current.windowProperty().addListener(windowListener);
            observeWindow();observeAncestors();
        });
        parentProperty().addListener(parentListener);
        draw();
    }

    private static Preferences safePreferences() {
        try { return Preferences.userRoot().node("pro/gravit/gdworld/loading-games"); }
        catch (RuntimeException ignored) { return null; }
    }
    private static Label label(String text, int size, String color) {
        Label label=new Label(text);label.setStyle("-fx-text-fill:"+color+";-fx-font-family:'System';-fx-font-size:"+size+"px;");return label;
    }
    private static Button button(String text, String description) {
        Button b=new Button(text);b.setAccessibleText(description);b.setTooltip(new Tooltip(description));b.setMinHeight(28);
        b.setStyle(BUTTON_STYLE);
        return b;
    }
    private Button direction(String arrow, int dx, int dy) {
        Button b=button(arrow,t(switch(arrow){case "↑"->"up";case "←"->"left";case "↓"->"down";default->"right";}));
        b.setMinWidth(26);b.setPrefWidth(26);b.setStyle(b.getStyle()+"-fx-padding:4 3;");
        b.setOnAction(e -> { if (game==0&&!paused&&!failed) {snake.turn(dx,dy);snakeStarted=true;updateActivity();}board.requestFocus(); });return b;
    }

    /** Called by UpdateScene.reset, exactly when the update screen starts a new visit. */
    public static void begin(Node root) {
        if (root.lookup("#loadingGames") instanceof LoadingGames view) {
            try { view.beginSession(); }
            catch (RuntimeException ignored) { view.unavailable(); }
        }
    }
    /** A download error always takes precedence over entertainment. */
    public static void fail(Node root) {
        if (root.lookup("#loadingGames") instanceof LoadingGames view) {
            view.failed=true;view.loop.stop();view.controls.setDisable(true);
            view.help.setText(t("loadInterrupted"));view.draw();
        }
    }
    void beginSession() {
        int previous=processLast;
        if(preferences!=null)try{previous=preferences.getInt("last-game",previous);}catch(RuntimeException ignored){}
        int choice=LoadingGameModels.chooseDifferent(random,previous,NAMES.length);processLast=choice;
        if(preferences!=null)try{preferences.putInt("last-game",choice);}catch(RuntimeException ignored){}
        startGame(choice);
    }
    void startGame(int choice) {
        if(choice<0||choice>=NAMES.length)throw new IllegalArgumentException("Unknown game");
        loop.stop();game=choice;failed=paused=flagMode=snakeStarted=false;accumulator=0;lastPulse=0;selectedX=selectedY=0;
        snake=game==0?new LoadingGameModels.Snake(16,random):null;
        mines=game==1?new LoadingGameModels.Mines(9,10,random):null;
        memory=game==2?new LoadingGameModels.Memory(random):null;
        controls.setDisable(false);title.setText(t(NAMES[game]));
        directions.setVisible(game==0);directions.setManaged(game==0);flag.setVisible(game==1);flag.setManaged(game==1);
        pause.setText(t("pause"));refreshText();draw();updateActivity();
    }
    private void unavailable() {
        failed=true;loop.stop();controls.setDisable(true);help.setText(t("unavailable"));
    }
    private void togglePause() {
        if (failed||game<0) return;
        paused=!paused;pause.setText(paused?t("resume"):t("pause"));refreshText();draw();updateActivity();
    }
    private void observeAncestors() {
        for(Observable observable:ancestors)observable.removeListener(visibilityListener);ancestors.clear();
        observeAncestor(this);
        updateActivity();
    }
    // A recursive traversal also avoids a JavaFX subtype merge in the launcher's
    // frame-rewriting build step, which runs on a headless server without JavaFX.
    private void observeAncestor(Node node) {
        if(node==null)return;
        node.visibleProperty().addListener(visibilityListener);ancestors.add(node.visibleProperty());
        node.disabledProperty().addListener(visibilityListener);ancestors.add(node.disabledProperty());
        observeAncestor(node.getParent());
    }
    private void observeWindow() {
        if(observedWindow!=null) {
            observedWindow.showingProperty().removeListener(visibilityListener);
            if(observedWindow instanceof Stage)((Stage)observedWindow).iconifiedProperty().removeListener(visibilityListener);
        }
        observedWindow=getScene()==null?null:getScene().getWindow();
        if(observedWindow!=null) {
            observedWindow.showingProperty().addListener(visibilityListener);
            if(observedWindow instanceof Stage)((Stage)observedWindow).iconifiedProperty().addListener(visibilityListener);
        }
        updateActivity();
    }
    private boolean attached() {
        if(getScene()==null||observedWindow==null||!observedWindow.isShowing())return false;
        if(observedWindow instanceof Stage&&((Stage)observedWindow).isIconified())return false;
        return ancestorsActive(this);
    }
    private static boolean ancestorsActive(Node node) {return node==null||node.isVisible()&&!node.isDisabled()&&ancestorsActive(node.getParent());}
    private void updateActivity() {
        if(loop==null)return;
        boolean moving=game==0&&snakeStarted&&snake!=null&&!snake.over||game==2&&memory!=null&&memory.second>=0;
        if(!failed&&!paused&&moving&&attached()) {
            if(loop.getStatus()!=javafx.animation.Animation.Status.RUNNING){lastPulse=0;loop.play();}
        } else {loop.stop();lastPulse=0;}
    }
    private void pulse() {
        if(!attached()||failed||paused){updateActivity();return;}
        long now=System.nanoTime();double dt=lastPulse==0?40:Math.min(100,(now-lastPulse)/1_000_000.0);lastPulse=now;
        boolean dirty=false;
        if(game==0&&!snake.over) {
            accumulator+=dt;
            if(accumulator>=Math.max(75,160-snake.score*4)){accumulator=0;snake.step();dirty=true;}
        } else if(game==2)dirty=memory.tick(dt);
        if(dirty){refreshText();draw();updateActivity();}
    }
    private void refreshText() {
        if(game<0)return;
        String hint;
        if(game==0) {
            score.setText(t("snakeScore",snake.score));
            hint=snake.over?(snake.won?t("snakeWon"):t("snakeLost")):t("snakeHelp");
        } else if(game==1) {
            score.setText(t("minesScore",mines.bombs-mines.flagCount,mines.openCount));
            hint=mines.over?(mines.won?t("minesWon"):t("minesLost")):t("minesHelp");
            flag.setText(flagMode?t("flag")+" ✓":t("flag"));flag.setStyle(BUTTON_STYLE+"-fx-background-color:"+(flagMode?"#665321":"#383328")+";");
        } else {
            score.setText(t("memoryScore",memory.pairs,memory.moves));
            hint=memory.won()?t("memoryWon"):t("memoryHelp");
        }
        help.setText(paused?t("pausedHelp"):hint);
        board.setAccessibleText(t(NAMES[game])+". "+score.getText()+". "+help.getText());
    }
    private void draw() {
        GraphicsContext g=board.getGraphicsContext2D();g.setFill(Color.web("#111214"));g.fillRoundRect(0,0,280,280,12,12);
        if(game==0)drawSnake(g);else if(game==1)drawMines(g);else if(game==2)drawMemory(g);
        if(paused||failed) {
            g.setFill(Color.web("#111214",.9));g.fillRoundRect(0,0,280,280,12,12);
            text(g,failed?t("loadStopped"):t("pause"),140,145,19,LIGHT);
        }
        if(board.isFocused()){g.setStroke(Color.web(GOLD));g.setLineWidth(2);g.strokeRoundRect(1,1,278,278,12,12);}
    }
    private static void text(GraphicsContext g,String text,double x,double y,double size,String color) {
        g.setTextAlign(TextAlignment.CENTER);g.setFont(Font.font("System",FontWeight.BOLD,size));g.setFill(Color.web(color));g.fillText(text,x,y);
    }
    private void drawSnake(GraphicsContext g) {
        double c=264.0/16;
        g.setStroke(Color.web("#202124"));g.setLineWidth(.5);
        for(int i=0;i<=16;i++){g.strokeLine(8+i*c,8,8+i*c,272);g.strokeLine(8,8+i*c,272,8+i*c);}
        if(snake.food!=null) {g.setFill(Color.web(LIGHT));g.fillOval(8+snake.food.x()*c+4,8+snake.food.y()*c+4,c-8,c-8);}
        for(int i=snake.body.size()-1;i>=0;i--) {
            var p=snake.body.get(i);g.setFill(Color.web(i==0?GOLD:i%2==0?"#fcd12a":"#daa520"));g.fillRoundRect(9+p.x()*c,9+p.y()*c,c-2,c-2,5,5);
        }
        var h=snake.body.getFirst();g.setFill(Color.web("#302407"));
        double hx=8+h.x()*c,hy=8+h.y()*c;g.fillOval(hx+4+snake.dx*2,hy+4+snake.dy*2,3,3);g.fillOval(hx+10+snake.dx,hy+10+snake.dy,3,3);
        if(snake.over)banner(g,snake.won?t("victory"):t("gameOver"));
    }
    private void drawMines(GraphicsContext g) {
        double c=264.0/9;
        for(int y=0;y<9;y++)for(int x=0;x<9;x++) {
            double px=8+x*c,py=8+y*c;g.setFill(Color.web(mines.opened[y][x]?"#222326":"#383328"));g.fillRoundRect(px+1,py+1,c-2,c-2,4,4);
            if(mines.mines[y][x]&&(mines.opened[y][x]||mines.over)) {
                g.setFill(Color.web(mines.won?GOLD:"#f28c82"));g.fillOval(px+9,py+9,c-18,c-18);g.setStroke(Color.web(LIGHT));g.setLineWidth(1);g.strokeLine(px+7,py+c/2,px+c-7,py+c/2);g.strokeLine(px+c/2,py+7,px+c/2,py+c-7);
            } else if(mines.flags[y][x]) {
                g.setStroke(Color.web(LIGHT));g.setLineWidth(2);g.strokeLine(px+10,py+7,px+10,py+c-7);g.setFill(Color.web(GOLD));g.fillPolygon(new double[]{px+11,px+23,px+11},new double[]{py+6,py+12,py+17},3);
            } else if(mines.opened[y][x]&&mines.around(x,y)>0)text(g,""+mines.around(x,y),px+c/2,py+c/2+6,17,LIGHT);
        }
        if(board.isFocused()){g.setStroke(Color.web(GOLD));g.setLineWidth(2);g.strokeRoundRect(9+selectedX*c,9+selectedY*c,c-2,c-2,4,4);}
        if(mines.won)banner(g,t("boardCleared"));
    }
    private void drawMemory(GraphicsContext g) {
        for(int i=0;i<12;i++) {
            double x=8+(i%4)*66,y=8+(i/4)*88;boolean up=memory.faceUp(i);
            g.setFill(Color.web(memory.matched[i]?"#665321":up?"#fcd12a":"#383328"));g.fillRoundRect(x+3,y+3,60,82,9,9);
            g.setStroke(Color.web(up?"#daa520":"#665321"));g.setLineWidth(1);g.strokeRoundRect(x+3,y+3,60,82,9,9);
            text(g,up?String.valueOf((char)('A'+memory.cards[i])):"?",x+33,y+54,30,up&&!memory.matched[i]?"#302407":GOLD);
            if(board.isFocused()&&i==selectedY*4+selectedX){g.setStroke(Color.web(LIGHT));g.setLineWidth(2);g.strokeRoundRect(x+3,y+3,60,82,9,9);}
        }
        if(memory.won())banner(g,t("allPairs"));
    }
    private static void banner(GraphicsContext g,String message) {
        g.setFill(Color.web("#111214",.93));g.fillRoundRect(18,112,244,54,10,10);text(g,message,140,146,21,GOLD);
    }
}
