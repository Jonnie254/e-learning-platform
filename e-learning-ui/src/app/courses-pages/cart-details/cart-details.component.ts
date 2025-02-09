import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';

@Component({
  selector: 'app-cart-details',
  standalone: true,
  imports: [
    NavbarComponent
  ],
  templateUrl: './cart-details.component.html',
  styleUrl: './cart-details.component.scss'
})
export class CartDetailsComponent {

}
