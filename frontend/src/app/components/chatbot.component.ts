import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { ChatResponse } from '../models/api';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="card">
      <h2>Chatbot RAG</h2>
      <p class="hint">Interrogez le chatbot exposé par /api/chatbot/query. Le backend peut être désactivé selon la configuration.</p>
      <form class="inline-form" (ngSubmit)="ask()">
        <input [(ngModel)]="question" name="question" placeholder="Posez une question..." required />
        <button type="submit" [disabled]="loading">Envoyer</button>
      </form>
      <div *ngIf="answer">
        <h3>Réponse</h3>
        <p>{{ answer.answer }}</p>
        <div *ngIf="answer.sources?.length">
          <h4>Sources</h4>
          <ul>
            <li *ngFor="let s of answer.sources">{{ s.title }} <a *ngIf="s.url" [href]="s.url" target="_blank">lien</a></li>
          </ul>
        </div>
      </div>
    </section>
  `,
  styles: [
    `
      .inline-form { display: grid; grid-template-columns: 1fr auto; gap: 0.5rem; margin-bottom: 1rem; }
      input { padding: 0.5rem; border: 1px solid #d0d7de; border-radius: 4px; }
      button { background: #0b5ed7; border: none; color: #fff; padding: 0.5rem 0.75rem; border-radius: 4px; cursor: pointer; }
      .hint { color: #6c757d; margin-bottom: 0.5rem; }
    `
  ]
})
export class ChatbotComponent {
  question = '';
  loading = false;
  answer?: ChatResponse;

  constructor(private api: ApiService) {}

  ask() {
    this.loading = true;
    this.api.askChatbot(this.question).subscribe({
      next: (res) => {
        this.answer = res;
        this.loading = false;
      },
      error: () => (this.loading = false)
    });
  }
}
