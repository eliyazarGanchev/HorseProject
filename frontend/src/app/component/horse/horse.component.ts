import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AutocompleteComponent } from 'src/app/component/autocomplete/autocomplete.component';
import { HorseService } from 'src/app/service/horse.service';
import { Horse } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { ConfirmDeleteDialogComponent } from 'src/app/component/confirm-delete-dialog/confirm-delete-dialog.component';
import {ErrorFormatterService} from "../../service/error-formatter.service";


@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  imports: [
    RouterLink,
    FormsModule,
    AutocompleteComponent,
    ConfirmDeleteDialogComponent
  ],
  standalone: true,
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  horses: Horse[] = [];
  bannerError: string | null = null;
  horseForDeletion: Horse | undefined;


  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
  ) { }

  searchParams = {
    name: '',
    description: '',
    date_of_birth: '',
    sex: '',
    ownerName: ''
  };

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.horses = data;
          this.bannerError = null;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  onSearch(): void {
    const query = {
      name: this.searchParams.name?.trim() || null,
      description: this.searchParams.description?.trim() || null,
      date_of_birth: this.searchParams.date_of_birth || null,
      sex: this.searchParams.sex || null,
      ownerName: this.searchParams.ownerName?.trim() || null
    };

    this.service.search(query).subscribe({
      next: data => {
        this.horses = data;
        this.bannerError = null;
      },
      error: error => {
        console.error('Error searching horses', error);
        this.bannerError = 'Could not fetch horses: ' + error.message;
        const errorMessage = error.status === 0
          ? 'Is the backend up?'
          : error.message.message;
        this.notification.error(errorMessage, 'Could Not Fetch Horses');
      }
    });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return horse.dateOfBirth.toLocaleDateString();
  }

  deleteHorse(horse: Horse) {
    this.service.delete(horse.id).subscribe({
      next: () => {
        this.horseForDeletion = undefined;
        this.notification.success(`Deleting horse: ${horse.name}.`);
        this.reloadHorses();
      },error: error => {
        this.notification.error(this.errorFormatter.format(error), 'Could Not Delete Horse', {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    })
  }
}
