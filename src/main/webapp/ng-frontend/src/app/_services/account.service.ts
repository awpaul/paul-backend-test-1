import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '@environments/environment';
import { User } from '@app/_models';

@Injectable({ providedIn: 'root' })
export class AccountService {
    private userSubject: BehaviorSubject<User>;
    public user: Observable<User>;

    constructor(
        private router: Router,
        private http: HttpClient
    ) {
        this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
        this.user = this.userSubject.asObservable();
    }

    public get userValue(): User {
        return this.userSubject.value;
    }

    login(email, password) {

        password = btoa(password);
        var usr = {};
        return this.http.post<any>('/rest/user/login', { email, password})
            .pipe(map(x => {
            if (x.success) {
                localStorage.setItem('user', JSON.stringify(x.dataMap.user));
                this.userSubject.next(x.dataMap.user);

            }
            return x;
                // store user details and jwt token in local storage to keep user logged in between page refreshes


            }));
    }

    logout() {
        // remove user from local storage and set current user to null
        localStorage.removeItem('user');
        this.userSubject.next(null);
        this.router.navigate(['/account/login']);
    }

    register(user: User) {
        user.password=btoa(user.password);
        return this.http.post('/rest/users/register', user);
    }

    getAll() {
        return this.http.get<any>('/rest/allUsers').pipe(map(x=>{
            if (x.success) {
                return x.dataMap.users;
            }
        }));
    }

    getById(id: string) {
        return this.http.get<any>('/rest/getUserById/'+id).pipe(map(x=>{
                                                                      if (x.success) {
                                                                          return x.dataMap.user;
                                                                      }
                                                                  }));
    }

    update(id, updateUser) {
    if (updateUser.password.length>0) {
      updateUser.password=btoa(updateUser.password);
    }
        return this.http.post('/rest/updateUser/'+id, updateUser)
            .pipe(map(x => {
                // update stored user if the logged in user updated their own record
                if (id == this.userValue.id) {
                    // update local storage
                    const user = { ...this.userValue, ...updateUser };
                    localStorage.setItem('user', JSON.stringify(user));

                    // publish updated user to subscribers
                    this.userSubject.next(user);
                }
                return x;
            }));
    }

    delete(id: string) {
        return this.http.delete<any>('/rest/deleteUser/'+id)
            .pipe(map(x => {
                // logout if the logged in user deleted their own record
                if (x.success && id==this.userValue.id) {
                    this.logout();
                }
                return x;
            }));
    }
}
