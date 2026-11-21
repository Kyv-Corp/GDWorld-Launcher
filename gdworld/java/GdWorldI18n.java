package pro.gravit.launcher.gui.components;

import pro.gravit.launcher.gui.core.JavaFXApplication;

/** Uses the launcher's active resource bundle, independent of the operating system locale. */
public final class GdWorldI18n {
    private GdWorldI18n() {}
    public static String t(String key, Object... args) {
        String fallback = english(key);
        JavaFXApplication app = JavaFXApplication.getInstance();
        String value = app == null ? fallback : app.getTranslation("gdworld." + key, fallback);
        return args.length == 0 ? value : String.format(java.util.Locale.ROOT, value, args);
    }
    private static String english(String key) {
        return switch (key) {
            case "cabinet" -> "Account";
            case "close" -> "Close";
            case "yes" -> "Yes";
            case "cancel" -> "Cancel";
            case "retry" -> "Retry";
            case "loadingProfile" -> "Loading your profile…";
            case "slim" -> "Slim arms (Alex / slim)";
            case "loginFirst" -> "Sign in to the launcher, then open Account beside your username.";
            case "noSession" -> "Session unavailable. Sign in to the launcher again.";
            case "expired" -> "Session expired. Sign out and sign in again.";
            case "serviceError" -> "Account service unavailable (%s). Try again.";
            case "requestCancelled" -> "Request cancelled.";
            case "saved" -> "Changes are saved to your GDWorld account.";
            case "skin" -> "Skin";
            case "cape" -> "Cape";
            case "reconnect" -> "Reconnect to the game server after changing your skin or cape.";
            case "links" -> "Linked accounts";
            case "notLinked" -> " — not linked";
            case "providersPending" -> "Microsoft and LittleSkin sign-in awaits setup. Linking will preserve your username, UUID and game progress.";
            case "refresh" -> "Refresh";
            case "noTexture" -> "Not uploaded yet";
            case "skinFormat" -> "PNG 64×64 or 64×32 · up to 1 MB";
            case "capeFormat" -> "PNG 64×32 · up to 1 MB";
            case "autoModel" -> "Arm model detected automatically";
            case "uploadSkin" -> "Upload skin";
            case "uploadCape" -> "Upload cape";
            case "resetSkin" -> "Reset skin";
            case "resetCape" -> "Reset cape";
            case "chooseSkin" -> "Choose a skin";
            case "chooseCape" -> "Choose a cape";
            case "uploadingSkin" -> "Uploading skin…";
            case "uploadingCape" -> "Uploading cape…";
            case "fileSize" -> "The file must be no larger than 1 MB.";
            case "confirmSkin" -> "Restore the default skin?";
            case "confirmCape" -> "Restore the default cape?";
            case "resetAppearance" -> "Reset appearance";
            case "connectionError" -> "Could not reach your account. Check your connection and try again.";
            case "mini" -> "Mini-game";
            case "preparing" -> "While Minecraft gets ready";
            case "waiting" -> "A game will appear when loading starts";
            case "restart" -> "Restart";
            case "restartTip" -> "Restart this mini-game";
            case "pause" -> "Pause";
            case "pauseTip" -> "Pause the mini-game; loading continues";
            case "resume" -> "Resume";
            case "flag" -> "Flag";
            case "flagTip" -> "Toggle flag placement";
            case "up" -> "Snake: up";
            case "left" -> "Snake: left";
            case "down" -> "Snake: down";
            case "right" -> "Snake: right";
            case "loadInterrupted" -> "Loading interrupted. See the error on the right.";
            case "unavailable" -> "Mini-game unavailable. Loading continues.";
            case "snake" -> "Snake";
            case "mines" -> "Minesweeper";
            case "memory" -> "Match pairs";
            case "snakeScore" -> "Score: %s · Collect bright dots";
            case "snakeWon" -> "The board is yours! Press Restart.";
            case "snakeLost" -> "Game over. Press Restart.";
            case "snakeHelp" -> "Click board · arrows / WASD\nSpace — pause";
            case "minesScore" -> "Mines: %s · Open: %s/71";
            case "minesWon" -> "Board cleared! Press Restart.";
            case "minesLost" -> "That was a mine. Try again?";
            case "minesHelp" -> "Left click — open · right click / F — flag\nThe first move is safe";
            case "memoryScore" -> "Pairs: %s/6 · Attempts: %s";
            case "memoryWon" -> "All pairs found! Press Restart.";
            case "memoryHelp" -> "Flip cards and remember pairs\nMouse or arrows + Enter";
            case "pausedHelp" -> "Mini-game paused. Loading continues.";
            case "loadStopped" -> "Loading stopped";
            case "victory" -> "You win!";
            case "gameOver" -> "Game over";
            case "boardCleared" -> "Board cleared!";
            case "allPairs" -> "All pairs found!";
            case "prepareGame" -> "PREPARING GAME";
            case "checkFiles" -> "Downloading and checking files";
            case "downloadError" -> "Download failed — see details above";
            case "settingName" -> "Name";
            case "settingDescription" -> "Description";
            default -> key;
        };
    }
}
