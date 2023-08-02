import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CurrencyRequest } from '../model/currencyRequest';
import { CurrencyValue } from '../model/currencyValue';

@Injectable({
  providedIn: 'root'
})
export class currencyRequestService {
  private apiServerUrl: string = 'http://localhost:8080';

  constructor(private http: HttpClient) {
    this.apiServerUrl = 'http://localhost:8080';
  }

 public findAll(): Observable<CurrencyRequest[]> {
  return this.http.get<CurrencyRequest[]>(`${this.apiServerUrl}/currencies/requests`);
 }

 public getCurrentCurrencyValue(currencyRequest: CurrencyRequest): Observable<CurrencyValue> {
  return this.http.post<CurrencyValue>(`${this.apiServerUrl}/currencies/get-current-currency-value-command`, currencyRequest);
 }
}
