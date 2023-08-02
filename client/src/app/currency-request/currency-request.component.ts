import { Component, OnInit } from '@angular/core';
import { currencyRequestService } from '../service/currency-request-service';
import { CurrencyRequest } from '../model/currencyRequest';
import { HttpErrorResponse } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { CurrencyValue } from '../model/currencyValue';

@Component({
  selector: 'app-currency-request',
  templateUrl: './currency-request.component.html',
  styleUrls: ['./currency-request.component.css']
})
export class CurrencyRequestComponent implements OnInit {
  public currencyRequests: CurrencyRequest[] = [];
  public allCurrencyRequests: CurrencyRequest[] = [];

  public requestedCurrencyValue!: Number | undefined;
  public requestedCurrencyCode!: Number | undefined;

  public errorMessage: string | null = null;


  constructor (
    private currencyRequestService: currencyRequestService
  ) {}

  ngOnInit(): void {
    this.getCurrencyRequests();
  }

  public getCurrencyRequests(): void {
    this.currencyRequestService.findAll().subscribe({
      next: (response: CurrencyRequest[]) => {
        this.currencyRequests = response;
        this.allCurrencyRequests = this.currencyRequests;
      },
      error: (error: HttpErrorResponse) => {
        alert(error.message);
      }
    });
  }

  public onGetCurrentCurrencyValue(requestForm: NgForm): void {
    this.errorMessage = null; // Clear the previous error message

    this.currencyRequestService.getCurrentCurrencyValue(requestForm.value).subscribe({
      next: (response: CurrencyValue) => {
        console.log(response);
        this.requestedCurrencyValue = response.value;
        this.requestedCurrencyCode = requestForm.value.currency;

        this.getCurrencyRequests();
        requestForm.reset();
        document.getElementById('request-form')!.click();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = error.error.errorMessage;
      }
    });
  }

  public searchCurrencyRequests(key: string): void {
    const results: CurrencyRequest[] = [];
    this.currencyRequests = this.allCurrencyRequests;
    for (const task of this.currencyRequests) {
      if (task.currency.toLowerCase().indexOf(key.toLowerCase()) !== -1
      || task.name.toLowerCase().indexOf(key.toLowerCase()) !== -1
      ) {
        results.push(task);
      }
    }
    this.currencyRequests = results;
    if (!key) {
      this.currencyRequests = this.allCurrencyRequests;
    }
  }

  public onOpenRequestModal(): void {
    const container = document.getElementById('main-container');
    const button = document.createElement('button');
    button.type = 'button';
    button.style.display = 'none';
    button.setAttribute('data-toggle', 'modal');
    button.setAttribute('data-target', '#requestModal');
    container!.appendChild(button);
    button.click();
  }
}
