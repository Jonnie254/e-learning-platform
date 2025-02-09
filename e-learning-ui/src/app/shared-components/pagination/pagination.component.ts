import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.scss'
})
export class PaginationComponent {
   @Input() currentPage: number = 0;
   @Input() totalPages: number = 0

  @Output() pageChange = new EventEmitter<number>();
  goToFirstPage(){
    if(this.currentPage > 0){
      this.pageChange.emit(0);
    }

  }

  goToPreviousPage() {
    if(this.currentPage > 0){
      this.pageChange.emit(this.currentPage - 1);
    }

  }

  goToNextPage() {
    if(this.currentPage < this.totalPages - 1){
      this.pageChange.emit(this.currentPage + 1);
    }

  }

  goToLastPage() {
    if(this.currentPage < this.totalPages - 1){
      this.pageChange.emit(this.totalPages - 1);
    }
  }
}
