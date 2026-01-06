import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AuthResponse } from '../models/api';

const TOKEN_KEY = 'appbank_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSubject = new BehaviorSubject<{ username: string | null }>({ username: null });
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {
    const existing = localStorage.getItem(TOKEN_KEY);
    if (existing) {
      this.userSubject.next({ username: this.getUsernameFromToken(existing) });
    }
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { username, password }).pipe(
      tap((res) => {
        localStorage.setItem(TOKEN_KEY, res.token);
        this.userSubject.next({ username: res.username });
      })
    );
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    this.userSubject.next({ username: null });
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private getUsernameFromToken(token: string): string | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload?.sub ?? null;
    } catch (err) {
      return null;
    }
  }
}
