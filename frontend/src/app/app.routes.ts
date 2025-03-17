import {Routes} from '@angular/router';
import {HorseCreateEditComponent, HorseCreateEditDetailMode} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {OwnerComponent} from "./component/owner/owner.component";
import {
  OwnerCreateEditComponent,
  OwnerCreateEditDetailMode
} from "./component/owner/owner-create-detail/owner-create-detail.component";


export const routes: Routes = [
  {path: 'horses', children: [
      {path: '', component: HorseComponent},
      {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditDetailMode.create}},
      {path: 'edit/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditDetailMode.edit}},
      {path: 'detail/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditDetailMode.detail}},
    ]},

  { path: 'owners', children: [
      { path: '', component: OwnerComponent },
      { path: 'create', component: OwnerCreateEditComponent, data: { mode: OwnerCreateEditDetailMode.create } },
    ]},

  {path: '**', redirectTo: 'horses'},
];
