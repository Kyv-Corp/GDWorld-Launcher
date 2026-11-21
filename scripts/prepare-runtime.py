"""Create a source-build worktree; vendored upstream files remain unchanged."""
from pathlib import Path
import shutil
root=Path(__file__).resolve().parent.parent
out=root/'build/java-runtime'
if out.exists():shutil.rmtree(out)
shutil.copytree(root/'vendor/java-runtime',out)
classes=out/'src/main/java/pro/gravit/launcher/gui/components'
for p in (root/'gdworld/java').glob('*.java'):
 if p.name!='PatchRuntime.java':shutil.copy2(p,classes/p.name)
p=out/'src/main/java/pro/gravit/launcher/gui/scenes/login/LoginScene.java'
s=p.read_text()
s=s.replace('import org.slf4j.Logger;','import org.slf4j.Logger;\nimport pro.gravit.launcher.gui.components.NativeCabinet;')
for field in ['createAccountURL','forgotPassURL']:
 old=f'application.openURL(application.guiModuleConfig.{field})'
 assert s.count(old)==1
 s=s.replace(old,f'NativeCabinet.openForUrl(application, application.guiModuleConfig.{field})')
p.write_text(s)
p=out/'src/main/java/pro/gravit/launcher/gui/scenes/update/UpdateScene.java';s=p.read_text()
s=s.replace('import org.slf4j.Logger;','import org.slf4j.Logger;\nimport pro.gravit.launcher.gui.components.LoadingGames;')
assert s.count('speedon.setVisible(true);')==1
s=s.replace('speedon.setVisible(true);','speedon.setVisible(true);\n        LoadingGames.begin(layout);')
assert s.count('logger.error("", e);')==1
s=s.replace('logger.error("", e);','logger.error("", e);\n        LoadingGames.fail(layout);')
p.write_text(s)
p=out/'build.gradle.kts';p.write_text(p.read_text().replace('launcher-runtime:5.7.13','launcher-runtime:5.7.14'))
# Public deployment FXML/theme are versioned separately from the GUI classes.
shutil.copytree(root/'runtime',root/'build/runtime',dirs_exist_ok=True)
print(out)
