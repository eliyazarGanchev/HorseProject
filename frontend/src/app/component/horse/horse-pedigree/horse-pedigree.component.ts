import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import { HorsePedigree } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';
import { ToastrService } from 'ngx-toastr';
import { ErrorFormatterService } from 'src/app/service/error-formatter.service';
import {DatePipe, NgIf, NgTemplateOutlet} from "@angular/common";
import {ConfirmDeleteDialogComponent} from "../../confirm-delete-dialog/confirm-delete-dialog.component";

@Component({
  selector: 'app-horse-pedigree',
  templateUrl: './horse-pedigree.component.html',
  styleUrls: ['./horse-pedigree.component.scss'],
  imports: [
    DatePipe,
    RouterLink,
    NgIf,
    NgTemplateOutlet,
    ConfirmDeleteDialogComponent
  ],
  standalone: true
})
export class HorsePedigreeComponent implements OnInit {
  horseTree?: HorsePedigree;
  maxGenerations: number | null = null;
  id!: number;
  horseForDeletion?: HorsePedigree | undefined;

  constructor(
    private route: ActivatedRoute,
    private horseService: HorseService,
    private toastr: ToastrService,
    private errorFormatter: ErrorFormatterService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.id = +idParam;
      } else {
        this.toastr.error('No horse id provided. Redirecting to the horse list.', 'Error');
        return;
      }
      const genParam = this.route.snapshot.queryParamMap.get('maxGenerations');
      if (genParam) {
        this.maxGenerations = +genParam;
      }
      this.fetchPedigree();
    });
  }

  onDeleteClicked(horse: HorsePedigree): void {
    this.horseForDeletion = horse;
  }

  deleteHorse(horse: HorsePedigree): void {
    if (!horse.id) {
      return;
    }
    this.horseService.delete(horse.id).subscribe({
      next: () => {
        this.horseForDeletion = undefined;
        this.toastr.success(`Deleting horse: ${horse.name}.`);
        this.fetchPedigree();
      },
      error: error => {
        console.error('Error deleting horse:', error);
        this.toastr.error(
          this.errorFormatter.format(error),
          'Could Not Delete Horse',
          { enableHtml: true, timeOut: 10000 }
        );
      }
    });
  }

  private fetchPedigree(): void {
    this.horseService.getPedigree(this.id, this.maxGenerations).subscribe({
      next: (data: HorsePedigree) => {
        console.log('Pedigree data:', data);
        this.horseTree = data;
      },
      error: (error: any) => {
        console.error('Error fetching pedigree', error);
        this.toastr.error(this.errorFormatter.format(error), 'Could Not Fetch Pedigree');
      }
    });
  }

}


