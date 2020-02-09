import {Component, Input, OnInit} from '@angular/core';


declare const paypal: any;

@Component({
  selector: 'app-paypal-button',
  templateUrl: './paypal-button.component.html',
  styleUrls: ['./paypal-button.component.scss']
})
export class PaypalButtonComponent implements OnInit {
  @Input() private paymentFunction;
  @Input() private onAuthorizeFunction;
  @Input() private buttonId;

  constructor() {
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.configPayPal();
  }

  private configPayPal() {
    paypal.Button.render({
      style: {
        size: 'small',
        color: 'gold',
        shape: 'pill',
        label: 'pay',
        tagline: false
      },
      env: 'sandbox',
      payment: this.paymentFunction,
      onAuthorize: this.onAuthorizeFunction
    }, this.buttonId);
  }
}
