import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WeatherMeasurement } from '../models/weather-measurement.model';

@Injectable({ providedIn: 'root' })
export class WeatherApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/weather';

  getRecentForLocation(locationId: number, limit = 10): Observable<readonly WeatherMeasurement[]> {
    return this.http.get<readonly WeatherMeasurement[]>(
      `${this.endpoint}/location/${locationId}/recent`,
      { params: { limit } },
    );
  }
}
