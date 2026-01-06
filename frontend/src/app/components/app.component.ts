import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  template: `
    <div class="layout">
      <app-navbar></app-navbar>
      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [
    `
      :host { display: block; min-height: 100vh; background: #f5f7fb; }
      .layout { display: grid; grid-template-rows: auto 1fr; min-height: 100vh; }
      .content { padding: 1rem 2rem; }
    `
  ]
})
export class AppComponent {}
