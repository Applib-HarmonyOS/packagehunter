package org.applibgroup.codelabs.myapplication;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import org.applibgroup.codelabs.myapplication.slice.DetailAbilitySlice;

public class DetailAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(DetailAbilitySlice.class.getName());
    }
}
