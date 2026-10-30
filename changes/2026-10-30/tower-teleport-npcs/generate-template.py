"""Generate standalone Adyeshach / Trigrex / ChestCommands configs. No remote writes."""
import copy, json, pathlib, re, sys

root = pathlib.Path(__file__).resolve().parent
template = pathlib.Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else root / 'template.json'
t = json.loads(template.read_text(encoding='utf-8-sig'))
for key in ('npc_id', 'menu_file', 'trigger_file'):
    if not re.fullmatch(r'[A-Za-z0-9_-]+(?:\.yml)?', t[key]):
        raise ValueError('Invalid ' + key)
if not t['menu_file'].endswith('.yml') or not t['trigger_file'].endswith('.yml'):
    raise ValueError('Menu and trigger names must end with .yml')
import uuid
t.setdefault('npc_uuid', str(uuid.uuid5(uuid.NAMESPACE_URL, 'shensi/npc/' + t['npc_id'])))
uuid.UUID(t['npc_uuid'])
if not 1 <= len(t['routes']) <= 54:
    raise ValueError('Use 1..54 routes')
for route in t['routes']:
    if not re.fullmatch(r'[A-Za-z0-9_-]+', route['world']):
        raise ValueError('Invalid world name')
out = root / 'generated' / t['npc_id']
base = json.loads((root / 'base-npc.json').read_text(encoding='utf-8-sig'))
base['id'], base['uniqueId'] = t['npc_id'], t['npc_uuid']
base['gameProfile']['name'] = t['npc_name']
base['position'] = copy.deepcopy(t['location'])
base['clientPosition'] = copy.deepcopy(t['location'])
base['controller'], base['passengers'] = [], []
base['isCollision'], base['isNameTagVisible'] = False, True
def write(rel, value):
    p = out / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(value, encoding='utf-8', newline='\n')
def q(s): return json.dumps(s, ensure_ascii=False)
write('plugins/Adyeshach/npc/' + t['npc_uuid'] + '.json', json.dumps(base, ensure_ascii=False, indent=2) + '\n')
trigger = t['npc_id'] + ':\n  type: ady\n  npc: ' + q(t['npc_id']) + '\n  action:\n    - ' + q("执行指令('cc open " + t['menu_file'] + " %player_name%')") + '\n'
write('plugins/Trigrex/trigger/' + t['trigger_file'], trigger)
menu = ['menu-settings:', '  name: ' + q(t['menu_title']), '  rows: ' + str((len(t['routes']) + 8) // 9), '']
for i, route in enumerate(t['routes']):
    menu += ['route_' + str(i+1) + ':', '  NAME: ' + q(route['name']), '  LORE:', '    - ' + q(route['description']), '  ID: 276', "  ENCHANTMENT: 'SMITE,1'", '  COMMAND: ' + q('op:mv tp ' + route['world']), '  POSITION-X: ' + str(i % 9 + 1), '  POSITION-Y: ' + str(i // 9 + 1), '']
write('plugins/ChestCommands/menu/' + t['menu_file'], '\n'.join(menu))
print(out)
