import { Component, signal } from '@angular/core';
import { RiskCell } from './core/models/risk-cell.model';
import { CityOption, CitySearchComponent } from './features/search/city-search.component';
import { MOCK_RISK_CELLS } from './features/map/data/mock-risk-cells';
import { RiskDetailsPanelComponent } from './features/map/risk-details-panel/risk-details-panel.component';
import { RiskLegendComponent } from './features/map/risk-legend/risk-legend.component';
import { RiskMapComponent } from './features/map/risk-map/risk-map.component';

@Component({
  selector: 'app-root',
  imports: [
    CitySearchComponent,
    RiskMapComponent,
    RiskDetailsPanelComponent,
    RiskLegendComponent,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly riskCells = MOCK_RISK_CELLS;
  protected readonly selectedCell = signal<RiskCell | null>(null);
  protected readonly mapCenter = signal<readonly [number, number]>([46.7712, 23.6236]);
  protected readonly activeCity = signal('Cluj-Napoca');

  protected selectCell(cell: RiskCell): void {
    this.selectedCell.set(cell);
  }

  protected selectCity(city: CityOption): void {
    this.activeCity.set(city.name);
    this.mapCenter.set(city.coordinates);
    this.selectedCell.set(null);
  }
}
