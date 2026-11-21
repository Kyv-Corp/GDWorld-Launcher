from pathlib import Path
import json
base=Path(__file__).resolve().parent.parent
rows=[l.split('|') for l in (base/'i18n/strings.tsv').read_text().splitlines() if l and not l.startswith('#')]
assert all(len(r)==6 for r in rows)
def q(v):return json.dumps(v,ensure_ascii=False)
# Generated English defaults are only for initialization/tests or missing translation keys.
helper='''package pro.gravit.launcher.gui.components;

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
'''
for r in rows: helper+='            case '+q(r[0])+' -> '+q(r[1].replace('\\n','\n'))+';\n'
helper+='            default -> key;\n        };\n    }\n}\n'
(base/'java/GdWorldI18n.java').write_text(helper)
for index,lang in enumerate(['en','ru','uk','be','pl'],1):
 (base/f'i18n/gdworld_{lang}.properties').write_text('# GDWorld launcher additions; generated from strings.tsv\n'+''.join('gdworld.'+r[0]+'='+r[index].encode('ascii','backslashreplace').decode().replace('\\x','\\u00')+'\n' for r in rows))

for path in (base/'i18n').glob('gdworld_*.properties'):
    path.write_text(path.read_text().replace('gdworld.notLinked= ', 'gdworld.notLinked=\\ '))
print('Generated five language files and Java English defaults')
