import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import { HorsePedigree } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
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
    ConfirmDeleteDialogComponent,
  ],
  standalone: true
})
export class HorsePedigreeComponent implements OnInit {
  horseTree?: HorsePedigree;
  maxGenerations: number | null = null;
  id!: number;
  horseForDeletion?: HorsePedigree | undefined;
  showNoGenerationsNote = false;

  constructor(
    private route: ActivatedRoute,
    private horseService: HorseService,
    private toastr: ToastrService,
    private errorFormatter: ErrorFormatterService,
    private router: Router
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
      if (genParam && genParam.trim() !== "") {
        this.maxGenerations = +genParam;
        this.showNoGenerationsNote = false;
      } else {
        this.maxGenerations = null;
        this.showNoGenerationsNote = true;
      }
      if (!this.showNoGenerationsNote) {
        this.fetchPedigree();
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/horses/detail', this.id]);
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
        this.horseTree = data;
        this.initializeExpanded(this.horseTree);
      },
      error: (error: any) => {
        this.toastr.error(this.errorFormatter.format(error), 'Could Not Fetch Pedigree');
      }
    });
  }

  private initializeExpanded(horse: HorsePedigree): void {
    horse.expanded = true;
    if (horse.mother) {
      this.initializeExpanded(horse.mother);
    }
    if (horse.father) {
      this.initializeExpanded(horse.father);
    }
  }

}


