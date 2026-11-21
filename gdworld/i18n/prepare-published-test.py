"""Adapt the fixture smoke test to the exact published ProGuard mapping (keep signatures intact)."""
from pathlib import Path
import re,sys
mapping=Path(sys.argv[1]);target=Path(sys.argv[2]);target.mkdir(parents=True,exist_ok=True)
classes={};members={};current=None
for line in mapping.read_text().splitlines():
 if line.startswith('#') or not line:continue
 if not line.startswith(' '):
  current,new=line[:-1].split(' -> ');classes[current]=new;members[current]=[]
 else:members[current].append(line.strip())
def member(cls,name,method=False):
 lines=members[cls]
 pattern=r'\b'+re.escape(name)+(r'\(' if method else r' ->')
 values={line.split(' -> ')[-1] for line in lines if re.search(pattern,line)}
 assert len(values)==1,(cls,name,values)
 return next(iter(values))
src=(Path(__file__).resolve().parents[2]/'tests/LocalizationSmoke.java').read_text()
app='pro.gravit.launcher.gui.core.JavaFXApplication'
auth='pro.gravit.launcher.gui.core.service.AuthService'
cab='pro.gravit.launcher.gui.components.NativeCabinet'
launcher='pro.gravit.launcher.base.Launcher'
holder='pro.gravit.launcher.core.backend.LauncherBackendAPIHolder'
src=src.replace('app.authService', 'app.'+member(app,'authService'))
src=src.replace('.setUser(','.'+member(auth,'setUser',True)+'(')
src=src.replace('Launcher.setConfig(','Launcher.'+member(launcher,'setConfig',True)+'(')
src=src.replace('LauncherBackendAPIHolder.setApi(','LauncherBackendAPIHolder.'+member(holder,'setApi',True)+'(')
for field in ['INSTANCE','resources']:src=src.replace('"'+field+'"','"'+member(app,field)+'"')
src=src.replace('"render"','"'+member(cab,'render',True)+'"').replace('field(view,"dialog")','field(view,"'+member(cab,'dialog')+'")')
for cls in [app,auth,cab,launcher,'pro.gravit.launcher.base.LauncherConfig','pro.gravit.launcher.core.backend.LauncherBackendAPI',holder]:
 simple=cls.rsplit('.',1)[-1];new=classes[cls];src=re.sub(re.escape(cls)+r'\b',new,src)
 src=re.sub(r'\b'+simple+r'\b',new.rsplit('.',1)[-1],src)
 # Wildcard backend imports do not cover flattened obfuscated class names.
 if 'import '+new+';' not in src:src=src.replace('import javafx.application.*;','import javafx.application.*;\nimport '+new+';')
src=src.replace('import pro.gravit.launcher.core.backend.*;','')
src=src.replace('LocalizationSmoke','LocalizationPublishedSmoke')
(target/'LocalizationPublishedSmoke.java').write_text(src)
