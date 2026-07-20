import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RiskCell } from '../models/risk-cell.model';

@Injectable({ providedIn: 'root' })
export class RiskApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/risk-assessments';

  getLatestForLocation(locationId: number): Observable<RiskCell> {
    return this.http.get<RiskCell>(`${this.endpoint}/location/${locationId}/latest`);
  }
}
