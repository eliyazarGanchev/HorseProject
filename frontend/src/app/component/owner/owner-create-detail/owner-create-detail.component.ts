import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';
import { Owner } from 'src/app/dto/owner';
import { ErrorFormatterService } from 'src/app/service/error-formatter.service';
import { OwnerService } from 'src/app/service/owner.service';
import {NgIf} from "@angular/common";


export enum OwnerCreateEditDetailMode {
  create,
  edit,
  detail
}

@Component({
  selector: 'app-owner-create-detail',
  templateUrl: './owner-create-detail.component.html',
  imports: [
    FormsModule,
    RouterLink,
    NgIf,
  ],
  standalone: true,
  styleUrls: ['./owner-create-detail.component.scss']
})
export class OwnerCreateEditComponent implements OnInit {
  ownerForDeletion: Owner | undefined;

  mode: OwnerCreateEditDetailMode = OwnerCreateEditDetailMode.create;
  owner: Owner = {
    firstName: '',
    lastName: '',
    description: ''
  };

  constructor(
    private service: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService
  ) {}

  public get heading(): string {
    switch (this.mode) {
      case OwnerCreateEditDetailMode.create:
        return 'Create New Owner';
      case OwnerCreateEditDetailMode.edit:
        return 'Edit Owner';
      case OwnerCreateEditDetailMode.detail:
        return 'View Owner';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case OwnerCreateEditDetailMode.create:
        return 'Create';
      case OwnerCreateEditDetailMode.edit:
        return 'Save Changes';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === OwnerCreateEditDetailMode.create;
  }

  get modeIsDetail(): boolean {
    return this.mode === OwnerCreateEditDetailMode.detail;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode !== OwnerCreateEditDetailMode.create) {
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.service.getById(Number(id)).subscribe({
            next: (data) => {
              this.owner = data;
            },
            error: (error) => {
              console.error('Error loading owner data:', error);
              this.notification.error(this.errorFormatter.format(error), "Failed to load owner data", {
                enableHtml: true,
                timeOut: 10000,
              });
            }
          });
        }
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<Owner>;
      switch (this.mode) {
        case OwnerCreateEditDetailMode.create:
          observable = this.service.create(this.owner);
          break;
        default:
          console.error('Unknown OwnerCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Owner ${this.owner.firstName} ${this.owner.lastName} successfully ${this.mode === OwnerCreateEditDetailMode.create ? 'created' : 'updated'}.`);
          this.router.navigate(['/owners']);
        },
        error: error => {
          console.error('Error saving owner', error);
          this.notification.error(this.errorFormatter.format(error), 'Could Not Save Owner', {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
    }
  }
}
