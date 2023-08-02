import { NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CurrencyRequestComponent } from './currency-request/currency-request.component';

@NgModule({
  declarations: [
    CurrencyRequestComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [CurrencyRequestComponent]
})
export class AppModule {
  constructor(private titleService: Title) {
    this.titleService.setTitle("Currency checker");
  }
}
