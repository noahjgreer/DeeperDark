import os, json, glob

for filepath in glob.glob(r'f:\DeeperDark\src\main\resources\data\minecraft\recipe\*.json'):
    with open(filepath, 'r') as f:
        data = json.load(f)
    
    modified = False
    
    if 'result' in data:
        res = data['result']
        if isinstance(res, dict) and res.get('id') == 'minecraft:sugar' and 'components' in res and 'minecraft:item_model' in res['components']:
            model = res['components']['minecraft:item_model']
            mod_item = model.replace('minecraft:', '')
            if mod_item == 'golden_cauldron_item': mod_item = 'golden_cauldron'
            elif mod_item == 'siphon': mod_item = 'siphon'
            data['result'] = {'id': f'deeperdark:{mod_item}', 'count': res.get('count', 1)}
            modified = True
            
    if 'ingredients' in data:
        for i, ing in enumerate(data['ingredients']):
            if isinstance(ing, dict) and ing.get('id') == 'minecraft:sugar' and 'components' in ing:
                model = ing['components'].get('minecraft:item_model', '')
                mod_item = model.replace('minecraft:', '')
                if mod_item == 'golden_cauldron_item': mod_item = 'golden_cauldron'
                elif mod_item == 'siphon': mod_item = 'siphon'
                if mod_item:
                    data['ingredients'][i] = {'item': f'deeperdark:{mod_item}'}
                    modified = True
                    # Also replace type if it is deeperdark:crafting_component_shapeless
                    if data.get('type') == 'deeperdark:crafting_component_shapeless':
                        data['type'] = 'minecraft:crafting_shapeless'
                        
    if 'ingredient' in data: # Furnace recipe
        ing = data['ingredient']
        if isinstance(ing, dict) and ing.get('id') == 'minecraft:sugar' and 'components' in ing:
            model = ing['components'].get('minecraft:item_model', '')
            mod_item = model.replace('minecraft:', '')
            if mod_item == 'golden_cauldron_item': mod_item = 'golden_cauldron'
            elif mod_item == 'siphon': mod_item = 'siphon'
            if mod_item:
                data['ingredient'] = {'item': f'deeperdark:{mod_item}'}
                modified = True
                
    if modified:
        with open(filepath, 'w') as f:
            json.dump(data, f, indent=2)
