export interface WeatherMeasurement {
  readonly id: number;
  readonly locationId: number;
  readonly temperature: number | null;
  readonly precipitation: number | null;
  readonly windSpeed: number | null;
  readonly measuredAt: string;
}
