import { Component } from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';
import {FormsModule} from '@angular/forms';;
import {CategoryResponse, PageResponse, TagResponse} from '../../../interfaces/responses';
import {CoursesService} from '../../../services/courses-service.service';
import {ModalService} from '../../../services/modal.service';

@Component({
  selector: 'app-categories-tags',
  standalone: true,
  imports: [
    PaginationComponent,
    FormsModule,
    NgForOf,
    NgIf,
  ],
  templateUrl: './categories-tags.component.html',
  styleUrl: './categories-tags.component.scss'
})
export class CategoriesTagsComponent {
  selectedType: string = 'categories';
  page: number = 0;
  size: number = 10;

  categoryResponse: PageResponse<CategoryResponse> = { content: [] } as PageResponse<CategoryResponse>;
  tagResponse: PageResponse<TagResponse> = { content: []} as PageResponse<TagResponse>;
  message: string = '';
  title: string = '';
  cancelButtonText: string = 'Cancel';
  confirmButtonText: string = 'Confirm';
  isVisible: boolean = false;

  constructor(
    private courseService: CoursesService,
    private modalService:ModalService){
    this.getTagsOrCategories();
    this.modalService.isVisible$.subscribe(visible => {
      this.isVisible = visible;
    });
  }


  getTagsOrCategories() {
    this.applyFilter();
  }

  applyFilter() {
    if (this.selectedType === 'categories') {
      this.getAllCategories();
    } else {
      this.getAllTags();
    }
  }

  getAllTags() {
    this.courseService.getTags({
      page: this.page,
      size: this.size,
    }).subscribe({
      next: (response) => {
        this.tagResponse = response;
      }
    });
  }

  getAllCategories() {
    this.courseService.getCategories({
      page: this.page,
      size: this.size,
    }).subscribe({
      next: (response) => {
        this.categoryResponse = response;
      }
    });
  }

  confirmAction() {

  }

  totalPages(): number {
    return this.selectedType === 'categories'
      ? this.categoryResponse.totalPages || 0
      : this.tagResponse.totalPages || 0;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.applyFilter();
  }

  closeModal() {
    this.isVisible = false;
  }

}
