import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';
import { Owner } from 'src/app/dto/owner';
import { ErrorFormatterService } from 'src/app/service/error-formatter.service';
import { OwnerService } from 'src/app/service/owner.service';
import {NgIf} from "@angular/common";


export enum OwnerCreateMode {
  create,
}

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  imports: [
    FormsModule,
  ],
  standalone: true,
  styleUrls: ['./owner-create.component.scss']
})
export class OwnerCreateComponent implements OnInit {
  ownerForDeletion: Owner | undefined;

  mode: OwnerCreateMode = OwnerCreateMode.create;
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
      case OwnerCreateMode.create:
        return 'Create New Owner';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case OwnerCreateMode.create:
        return 'Create';
      default:
        return '?';
    }
  }


  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode !== OwnerCreateMode.create) {
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
        case OwnerCreateMode.create:
          observable = this.service.create(this.owner);
          break;
        default:
          console.error('Unknown OwnerCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Owner ${this.owner.firstName} ${this.owner.lastName} successfully ${this.mode === OwnerCreateMode.create ? 'created' : 'updated'}.`);
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
