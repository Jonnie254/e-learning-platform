import { Component } from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';
import {FormsModule} from '@angular/forms';;
import {CategoryRequest, CategoryResponse, PageResponse, TagRequest, TagResponse} from '../../../interfaces/responses';
import {CoursesService} from '../../../services/courses-service.service';
import {ModalService} from '../../../services/modal.service';
import {NotificationsComponent} from '../../../shared-components/notifications/notifications.component';
import {TagsService} from '../../../services/tags.service';

@Component({
  selector: 'app-categories-tags',
  standalone: true,
  imports: [
    PaginationComponent,
    FormsModule,
    NgForOf,
    NgIf,
    NotificationsComponent,
  ],
  templateUrl: './categories-tags.component.html',
  styleUrl: './categories-tags.component.scss'
})
export class CategoriesTagsComponent {
  selectedType: string = 'categories';
  tagId: string = '';
  categoryId: string = '';
  page: number = 0;
  size: number = 10;
  inputValue: string = '';
  categoryResponse: PageResponse<CategoryResponse> = {content: []} as PageResponse<CategoryResponse>;
  tagResponse: PageResponse<TagResponse> = {content: []} as PageResponse<TagResponse>;
  message: string = '';
  title: string = '';
  cancelButtonText: string = 'Cancel';
  confirmButtonText: string = 'Confirm';
  isVisible: boolean = false;
  isEditing: boolean = false;

  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };

  constructor(
    private courseService: CoursesService,
    private tagService: TagsService,
    private modalService: ModalService) {
    this.getTagsOrCategories();
    this.modalService.isVisible$.subscribe(visible => {
      this.isVisible = visible;
    });
  }

  get operationTitle(): string {
    if (this.isEditing) {
      return this.selectedType === 'categories' ? 'Update Category' : 'Update Tag';
    } else {
      return this.selectedType === 'categories' ? 'Add New Category' : 'Add New Tag';
    }
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

  addNewCategoryOrTag() {
    if (this.selectedType === 'categories') {
      this.addNewCategory();
    } else {
      this.addNewTag();
    }
  }

  confirmAction() {
    if (this.isEditing) {
      this.selectedType === 'categories' ? this.updateCategory() : this.updateTag();
    } else {
      this.addNewCategoryOrTag();
    }
  }

  addNewCategory() {
    if (!this.inputValue.trim()) {
     this.showNotification(
        'Category name cannot be empty',
        'error'
     )
      return;
    }
    const newCategory: CategoryRequest = {
      categoryName: this.inputValue,
    };
    this.tagService.addCategory(newCategory).subscribe({
      next: (response) => {
        this.showNotification(
          'Category added successfully',
          'success'
        )
        this.getTagsOrCategories();
        this.closeModal();
      },
      error: (error) => {
       this.showNotification(
          'Error adding Category',
          'error'
       )},
    });
  }

  addNewTag() {
    if (!this.inputValue.trim()) {
      this.showNotification(
        'Tag name cannot be empty',
        'error'
      )
      return;
    }
    const newTag: TagRequest = {
      tagName: this.inputValue,
    };
    this.tagService.addTag(newTag).subscribe({
      next: (response) => {
       this.showNotification(
          'Tag added successfully',
          'success'
       )
        this.getTagsOrCategories();
        this.closeModal();
      },
      error: (error) => {
        this.showNotification(
          'Error adding Tag',
          'error'
        )
        this.closeModal();
      },
    });
  }

  updateCategoryModal(categoryResponse: CategoryResponse) {
    this.isEditing = true;
    this.categoryId = categoryResponse.categoryId;
    this.inputValue = categoryResponse.categoryName;
    this.isVisible = true;
  }

  updateTagModal(tagResponse: TagResponse) {
    this.isEditing = true;
    this.tagId = tagResponse.tagId;
    this.inputValue = tagResponse.tagName;
    this.isVisible = true;
  }

  updateCategory() {
    if (!this.inputValue.trim()) {
      this.showNotification('Category name cannot be empty', 'error');
      return;
    }
    const updatedCategory: CategoryRequest = { categoryName: this.inputValue };
    this.tagService.updateCategory(this.categoryId, updatedCategory).subscribe({
      next: () => {
        this.showNotification('Category updated successfully', 'success');
        this.getTagsOrCategories();
        this.closeModal();
      },
      error: () => {
        this.showNotification('Error updating category', 'error');
      },
    });
  }


  updateTag() {
    if (!this.inputValue.trim()) {
      this.showNotification('Tag name cannot be empty', 'error');
      return;
    }
    const updatedTag: TagRequest = { tagName: this.inputValue };
    this.tagService.updateTag(this.tagId, updatedTag).subscribe({
      next: () => {
        this.showNotification('Tag updated successfully', 'success');
        this.getTagsOrCategories();
        this.closeModal();
      },
      error: () => {
        this.showNotification('Error updating Tag', 'error');
      },
    });
  }

  showNotification(message: string, type: 'success' | 'error') {
    this.notification = { show: true, message, type };
    setTimeout(() => this.closeNotification(), 3000);
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
    this.modalService.hideModal();
    this.isEditing = false;
    this.inputValue = '';
  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }

}
