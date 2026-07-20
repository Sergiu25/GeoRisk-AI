import { Component } from '@angular/core';
import { RiskLevel } from '../../../core/models/risk-level';
import { RISK_COLORS, RISK_RANGES } from '../../../shared/risk-presentation';

interface LegendItem {
  readonly level: RiskLevel;
  readonly range: string;
  readonly color: string;
}

@Component({
  selector: 'app-risk-legend',
  templateUrl: './risk-legend.component.html',
  styleUrl: './risk-legend.component.scss',
})
export class RiskLegendComponent {
  protected readonly items: readonly LegendItem[] = [
    { level: 'LOW', range: RISK_RANGES.LOW, color: RISK_COLORS.LOW },
    { level: 'MODERATE', range: RISK_RANGES.MODERATE, color: RISK_COLORS.MODERATE },
    { level: 'HIGH', range: RISK_RANGES.HIGH, color: RISK_COLORS.HIGH },
  ];
}
