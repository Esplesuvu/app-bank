import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { AccountOperation, BankAccount, ChatResponse, Customer, DashboardSummary } from '../models/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  getDashboard(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${environment.apiUrl}/dashboard/summary`);
  }

  listCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${environment.apiUrl}/customers`);
  }

  addCustomer(payload: { name: string; email: string }): Observable<Customer> {
    return this.http.post<Customer>(`${environment.apiUrl}/customers`, payload);
  }

  deleteCustomer(id: string) {
    return this.http.delete(`${environment.apiUrl}/customers/${id}`);
  }

  listAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${environment.apiUrl}/accounts`);
  }

  debit(accountId: string, amount: number, description?: string) {
    return this.http.post(`${environment.apiUrl}/accounts/${accountId}/debit`, { amount, description });
  }

  credit(accountId: string, amount: number, description?: string) {
    return this.http.post(`${environment.apiUrl}/accounts/${accountId}/credit`, { amount, description });
  }

  history(accountId: string): Observable<AccountOperation[]> {
    return this.http.get<AccountOperation[]>(`${environment.apiUrl}/accounts/${accountId}/operations`);
  }

  askChatbot(question: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${environment.apiUrl}/chatbot/query`, { question });
  }
}
