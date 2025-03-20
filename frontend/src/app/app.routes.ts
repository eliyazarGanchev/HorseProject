import {Routes} from '@angular/router';
import {HorseCreateEditComponent, HorseCreateEditDetailMode} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {OwnerComponent} from "./component/owner/owner.component";
import {
  OwnerCreateComponent,
  OwnerCreateMode
} from "./component/owner/owner-create/owner-create.component";


export const routes: Routes = [
  {path: 'horses', children: [
      {path: '', component: HorseComponent},
      {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditDetailMode.create}},
      {path: 'edit/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditDetailMode.edit}},
      {path: 'detail/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditDetailMode.detail}},
    ]},

  { path: 'owners', children: [
      { path: '', component: OwnerComponent },
      { path: 'create', component: OwnerCreateComponent, data: { mode: OwnerCreateMode.create } },
    ]},

  {path: '**', redirectTo: 'horses'},
];
