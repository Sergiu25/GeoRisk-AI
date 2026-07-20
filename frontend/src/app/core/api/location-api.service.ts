import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Location } from '../models/location.model';

@Injectable({ providedIn: 'root' })
export class LocationApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/locations';

  getAll(): Observable<readonly Location[]> {
    return this.http.get<readonly Location[]>(this.endpoint);
  }

  getById(id: number): Observable<Location> {
    return this.http.get<Location>(`${this.endpoint}/${id}`);
  }
}
