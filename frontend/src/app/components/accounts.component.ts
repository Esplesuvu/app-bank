import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { AccountOperation, BankAccount } from '../models/api';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="card">
      <h2>Comptes</h2>
      <p class="hint">Liste des comptes disponibles. Sélectionnez un compte pour débiter/créditer et voir l'historique.</p>
      <div class="grid">
        <div class="list">
          <div class="item" *ngFor="let a of accounts" (click)="select(a)" [class.active]="a.id === selected?.id">
            <div class="title">{{ a.id }}</div>
            <div class="sub">{{ a.type }} • {{ a.balance | number:'1.0-2' }} MAD</div>
            <div class="sub">{{ a.status }}</div>
            <div class="sub">Client: {{ a.customer?.name }}</div>
          </div>
        </div>
        <div class="detail" *ngIf="selected; else placeholder">
          <h3>Détails</h3>
          <p>Solde: <strong>{{ selected.balance | number:'1.0-2' }} MAD</strong></p>
          <form class="inline-form" (ngSubmit)="operate('debit')">
            <input type="number" min="0" step="0.01" [(ngModel)]="amount" name="amount" placeholder="Montant" required />
            <input [(ngModel)]="description" name="description" placeholder="Motif" />
            <button type="submit">Débiter</button>
            <button type="button" class="ghost" (click)="operate('credit')">Créditer</button>
          </form>
          <h4>Historique</h4>
          <table *ngIf="history.length; else noops">
            <thead><tr><th>Date</th><th>Type</th><th>Montant</th><th>Par</th></tr></thead>
            <tbody>
              <tr *ngFor="let op of history">
                <td>{{ op.operationDate | date:'short' }}</td>
                <td>{{ op.operationType }}</td>
                <td>{{ op.amount | number:'1.0-2' }}</td>
                <td>{{ op.operator || '-' }}</td>
              </tr>
            </tbody>
          </table>
          <ng-template #noops><p>Aucune opération enregistrée.</p></ng-template>
        </div>
        <ng-template #placeholder>
          <div class="detail"><p>Sélectionnez un compte pour voir les détails.</p></div>
        </ng-template>
      </div>
    </section>
  `,
  styles: [
    `
      .grid { display: grid; grid-template-columns: 1fr 2fr; gap: 1rem; }
      .list { max-height: 70vh; overflow: auto; border-right: 1px solid #eceff4; padding-right: 1rem; }
      .item { padding: 0.75rem; border: 1px solid #eceff4; border-radius: 6px; margin-bottom: 0.5rem; cursor: pointer; background: #fff; }
      .item.active { border-color: #0b5ed7; box-shadow: 0 0 0 2px rgba(11,94,215,0.1); }
      .title { font-weight: 700; }
      .sub { color: #6c757d; font-size: 0.9rem; }
      .detail { background: #fff; padding: 1rem; border-radius: 6px; box-shadow: 0 2px 6px rgba(0,0,0,0.05); }
      .inline-form { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 0.5rem; margin: 1rem 0; }
      input { padding: 0.5rem; border: 1px solid #d0d7de; border-radius: 4px; }
      button { background: #0b5ed7; border: none; color: #fff; padding: 0.5rem 0.75rem; border-radius: 4px; cursor: pointer; }
      button.ghost { background: transparent; border: 1px solid #d0d7de; color: #0b5ed7; }
      table { width: 100%; border-collapse: collapse; }
      th, td { text-align: left; padding: 0.4rem; border-bottom: 1px solid #eceff4; }
    `
  ]
})
export class AccountsComponent implements OnInit {
  accounts: BankAccount[] = [];
  selected?: BankAccount;
  history: AccountOperation[] = [];
  amount = 0;
  description = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.listAccounts().subscribe((res) => (this.accounts = res));
  }

  select(account: BankAccount) {
    this.selected = account;
    this.api.history(account.id).subscribe((ops) => (this.history = ops));
  }

  operate(type: 'debit' | 'credit') {
    if (!this.selected) return;
    const action = type === 'debit' ? this.api.debit(this.selected.id, this.amount, this.description) : this.api.credit(this.selected.id, this.amount, this.description);
    action.subscribe(() => {
      this.api.listAccounts().subscribe((res) => {
        this.accounts = res;
        const refreshed = res.find((a) => a.id === this.selected?.id);
        if (refreshed) this.selected = refreshed;
      });
      this.api.history(this.selected.id).subscribe((ops) => (this.history = ops));
      this.amount = 0;
      this.description = '';
    });
  }
}
