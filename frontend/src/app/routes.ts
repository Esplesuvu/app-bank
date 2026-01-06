import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard.component';
import { CustomersComponent } from './components/customers.component';
import { AccountsComponent } from './components/accounts.component';
import { LoginComponent } from './components/login.component';
import { ChatbotComponent } from './components/chatbot.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'customers', component: CustomersComponent },
  { path: 'accounts', component: AccountsComponent },
  { path: 'chatbot', component: ChatbotComponent },
  { path: '**', redirectTo: 'dashboard' }
];
