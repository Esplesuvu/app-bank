import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="nav">
      <div class="brand">App Bank</div>
      <div class="links">
        <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
        <a routerLink="/customers" routerLinkActive="active">Clients</a>
        <a routerLink="/accounts" routerLinkActive="active">Comptes</a>
        <a routerLink="/chatbot" routerLinkActive="active">Chatbot</a>
      </div>
      <div class="auth">
        <ng-container *ngIf="(auth.user$ | async) as user; else login">
          <span class="user">{{ user.username }}</span>
          <button (click)="logout()">Déconnexion</button>
        </ng-container>
        <ng-template #login>
          <a routerLink="/login" routerLinkActive="active">Connexion</a>
        </ng-template>
      </div>
    </nav>
  `,
  styles: [
    `
    .nav { display: flex; align-items: center; justify-content: space-between; padding: 0.75rem 1.5rem; background: #0b5ed7; color: #fff; }
    .brand { font-weight: 700; letter-spacing: 0.5px; }
    .links a { margin-right: 1rem; color: #fff; text-decoration: none; font-weight: 500; }
    .links a.active { text-decoration: underline; }
    .auth { display: flex; align-items: center; gap: 0.75rem; }
    button { background: #fff; color: #0b5ed7; border: none; padding: 0.35rem 0.75rem; border-radius: 4px; cursor: pointer; }
    button:hover { opacity: 0.9; }
    .user { font-weight: 600; }
    `
  ]
})
export class NavbarComponent {
  constructor(public auth: AuthService) {}

  logout() {
    this.auth.logout();
  }
}
