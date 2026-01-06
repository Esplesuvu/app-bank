import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="card">
      <h2>Connexion</h2>
      <form (ngSubmit)="submit()">
        <label>Nom d'utilisateur</label>
        <input [(ngModel)]="username" name="username" required />
        <label>Mot de passe</label>
        <input type="password" [(ngModel)]="password" name="password" required />
        <button type="submit" [disabled]="loading">{{ loading ? 'Connexion...' : 'Se connecter' }}</button>
        <p *ngIf="error" class="error">{{ error }}</p>
      </form>
      <p class="hint">Utilisez l'utilisateur admin seedé si vous n'avez pas encore créé de compte.</p>
    </section>
  `,
  styles: [
    `
    .card { max-width: 420px; margin: 2rem auto; padding: 1.5rem; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    form { display: grid; gap: 0.75rem; }
    input { padding: 0.5rem; border: 1px solid #d0d7de; border-radius: 4px; }
    button { background: #0b5ed7; border: none; color: #fff; padding: 0.5rem 0.75rem; border-radius: 4px; cursor: pointer; }
    .error { color: #b42318; font-weight: 600; }
    .hint { color: #6c757d; font-size: 0.9rem; margin-top: 0.5rem; }
    `
  ]
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  submit() {
    this.error = '';
    this.loading = true;
    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.loading = false;
        this.error = 'Identifiants invalides ou API injoignable';
      }
    });
  }
}
