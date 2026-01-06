export interface AuthResponse { token: string; username: string; roles: string[]; }
export interface DashboardSummary { customers: number; accounts: number; operations: number; totalBalance: number; totalCredit: number; totalDebit: number; }
export interface Customer { id?: string; name: string; email: string; createdAt?: string; }
export interface BankAccount { id: string; balance: number; status: string; type: string; createdAt?: string; customer: Customer; }
export interface AccountOperation { id: string; operationType: string; amount: number; description?: string; operationDate: string; operator?: string; }
export interface ChatResponse { answer: string; sources: { title: string; url?: string }[]; }
