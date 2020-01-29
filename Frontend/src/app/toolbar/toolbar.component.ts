import {Component, OnInit} from '@angular/core';
import {AuthenticationApiService} from '../core/authentication-api.service';
import {MatSnackBar} from '@angular/material';
import {Router} from '@angular/router';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent implements OnInit {

  constructor(private authService: AuthenticationApiService, private snackBar: MatSnackBar,
              private router: Router) {
  }

  getRole: string = this.authService.getRole();

  ngOnInit() {
  }

  logOut() {
    const logoutObserver = {
      next: x => {
        localStorage.removeItem('token');
        this.snackBar.open('You logged out successfully!', 'Dismiss', {
          duration: 3000
        });
        this.router.navigate(['/login']);
      },
      error: (err: Response) => {
        this.snackBar.open(JSON.parse(JSON.stringify(err)).error, 'Dismiss', {
          duration: 3000
        });
      }
    };
    this.authService.logout().subscribe(logoutObserver);
  }

}
