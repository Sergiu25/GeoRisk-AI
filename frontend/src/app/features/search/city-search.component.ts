import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Coordinate } from '../../core/models/risk-cell.model';

export interface CityOption {
  readonly name: string;
  readonly coordinates: Coordinate;
}

@Component({
  selector: 'app-city-search',
  imports: [ReactiveFormsModule],
  templateUrl: './city-search.component.html',
  styleUrl: './city-search.component.scss',
})
export class CitySearchComponent {
  @Output() readonly citySelected = new EventEmitter<CityOption>();

  protected readonly searchControl = new FormControl('', { nonNullable: true });
  protected noMatch = false;

  private readonly cities: readonly CityOption[] = [
    { name: 'Cluj-Napoca', coordinates: [46.7712, 23.6236] },
    { name: 'Florești', coordinates: [46.7457, 23.4937] },
    { name: 'Baciu', coordinates: [46.7998, 23.5168] },
    { name: 'Apahida', coordinates: [46.8167, 23.75] },
  ];

  protected submitSearch(): void {
    const query = this.searchControl.value.trim().toLocaleLowerCase();
    const match = this.cities.find((city) => city.name.toLocaleLowerCase().includes(query));

    if (!query || !match) {
      this.noMatch = true;
      return;
    }

    this.noMatch = false;
    this.searchControl.setValue(match.name);
    this.citySelected.emit(match);
  }
}
