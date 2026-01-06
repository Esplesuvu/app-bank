import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { DashboardSummary } from '../models/api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="card">
      <h2>Tableau de bord</h2>
      <p class="hint">Données retournées par l'API backend depuis /api/dashboard/summary.</p>
      <div class="grid" *ngIf="summary; else loading">
        <div class="tile">
          <h3>Clients</h3>
          <span>{{ summary.customers }}</span>
        </div>
        <div class="tile">
          <h3>Comptes</h3>
          <span>{{ summary.accounts }}</span>
        </div>
        <div class="tile">
          <h3>Opérations</h3>
          <span>{{ summary.operations }}</span>
        </div>
        <div class="tile">
          <h3>Solde total</h3>
          <span>{{ summary.totalBalance | number:'1.0-2' }} MAD</span>
        </div>
        <div class="tile">
          <h3>Total Crédit</h3>
          <span>{{ summary.totalCredit | number:'1.0-2' }} MAD</span>
        </div>
        <div class="tile">
          <h3>Total Débit</h3>
          <span>{{ summary.totalDebit | number:'1.0-2' }} MAD</span>
        </div>
      </div>
      <ng-template #loading>
        <p>Chargement du tableau de bord...</p>
      </ng-template>
    </section>
  `,
  styles: [
    `
      .card { background: #fff; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
      .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem; margin-top: 1rem; }
      .tile { background: #f7f9fc; padding: 1rem; border-radius: 6px; text-align: center; }
      .tile h3 { margin: 0 0 0.5rem 0; }
      .tile span { font-size: 1.4rem; font-weight: 700; color: #0b5ed7; }
      .hint { color: #6c757d; margin-bottom: 0.5rem; }
    `
  ]
})
export class DashboardComponent implements OnInit {
  summary?: DashboardSummary;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getDashboard().subscribe((res) => (this.summary = res));
  }
}
