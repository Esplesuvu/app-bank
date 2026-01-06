import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { Customer } from '../models/api';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="card">
      <h2>Clients</h2>
      <form class="inline-form" (ngSubmit)="create()">
        <input placeholder="Nom" [(ngModel)]="name" name="name" required />
        <input placeholder="Email" [(ngModel)]="email" name="email" required />
        <button type="submit">Ajouter</button>
      </form>
      <p class="hint">Les opérations sont sécurisées par JWT. Connectez-vous avant.</p>
      <table *ngIf="customers.length; else empty">
        <thead>
          <tr><th>Nom</th><th>Email</th><th>Créé le</th><th></th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let c of customers">
            <td>{{ c.name }}</td>
            <td>{{ c.email }}</td>
            <td>{{ c.createdAt | date:'short' }}</td>
            <td><button class="ghost" (click)="remove(c)">Supprimer</button></td>
          </tr>
        </tbody>
      </table>
      <ng-template #empty><p>Aucun client pour le moment.</p></ng-template>
    </section>
  `,
  styles: [
    `
      .inline-form { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)) auto; gap: 0.5rem; margin-bottom: 1rem; }
      input { padding: 0.5rem; border: 1px solid #d0d7de; border-radius: 4px; }
      button { background: #0b5ed7; border: none; color: #fff; padding: 0.5rem 0.75rem; border-radius: 4px; cursor: pointer; }
      button.ghost { background: transparent; border: 1px solid #d0d7de; color: #0b5ed7; }
      table { width: 100%; border-collapse: collapse; }
      th, td { text-align: left; padding: 0.5rem; border-bottom: 1px solid #eceff4; }
      .hint { color: #6c757d; margin-bottom: 0.5rem; }
    `
  ]
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  name = '';
  email = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.api.listCustomers().subscribe((res) => (this.customers = res));
  }

  create() {
    this.api.addCustomer({ name: this.name, email: this.email }).subscribe(() => {
      this.name = '';
      this.email = '';
      this.load();
    });
  }

  remove(customer: Customer) {
    if (!customer.id) return;
    this.api.deleteCustomer(customer.id).subscribe(() => this.load());
  }
}
