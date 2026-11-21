"""Merge GDWorld strings into an installed launcher runtime, preserving upstream translations."""
from pathlib import Path
import sys

root = Path(sys.argv[1])
source = Path(__file__).parent
for lang in ('en', 'ru', 'uk', 'be', 'pl'):
    path = root / f'runtime_{lang}.properties'
    lines = path.read_text().splitlines()
    lines = [line for line in lines if not line.startswith('gdworld.') and line != '# GDWorld launcher additions; generated from strings.tsv']
    path.write_text('\n'.join(lines).rstrip() + '\n\n' + (source / f'gdworld_{lang}.properties').read_text())
replacements = {
    'components/userBlock.fxml': {'text="Личный кабинет"': 'text="%gdworld.cabinet"'},
    'scenes/update/update.fxml': {
        'text="ПОДГОТОВКА ИГРЫ"': 'text="%gdworld.prepareGame"',
        'text="Скачивание и проверка файлов"': 'text="%gdworld.checkFiles"',
        'text="Ошибка загрузки — подробности выше"': 'text="%gdworld.downloadError"',
    },
    'scenes/settings/settings.fxml': {'text="Имя"': 'text="%gdworld.settingName"', 'text="Описание"': 'text="%gdworld.settingDescription"'},
    'scenes/settings/globalsettings.fxml': {'text="Имя"': 'text="%gdworld.settingName"', 'text="Описание"': 'text="%gdworld.settingDescription"'},
}
for name, changes in replacements.items():
    path = root / name
    content = path.read_text()
    for old, new in changes.items():
        assert old in content or new in content, (name, old)
        content = content.replace(old, new)
    path.write_text(content)
print('Merged all five locales and localized custom FXML labels')
